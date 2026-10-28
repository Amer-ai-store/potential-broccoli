package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.*
import com.example.ui.MarketViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var db: AppDatabase
    private lateinit var viewModel: MarketViewModel

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
            
        viewModel = MarketViewModel(
            db.userDao(),
            db.productDao(),
            db.savedProductDao(),
            db.orderDao(),
            db.chatReportDao(),
            db.cartItemDao(),
            db.bankCardDao(),
            db.searchQueryDao(),
            db.familyShoppingListDao(),
            db.storedOrganicItemDao()
        )
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testViewModelInitialization() {
        assertNotNull(viewModel)
    }

    @Test
    fun testGetUserByEmailIgnoresUsersWithBlankPhone() = runBlocking {
        db.userDao().insertUser(UserEntity(name = "Email User", phoneNumber = "", email = "someone@example.com", role = "Customer"))

        // Looking up by email alone must not match another account just because its phone number is blank.
        assertNull(db.userDao().getUserByEmail("new@example.com"))
        assertEquals("someone@example.com", db.userDao().getUserByEmail("someone@example.com")?.email)
    }

    @Test
    fun testDashboardExportFormatExcludesBankCards() = runBlocking {
        db.userDao().insertUser(UserEntity(name = "Buyer", phoneNumber = "0790000001", email = "buyer@example.com", role = "Customer"))
        db.orderDao().insertOrder(OrderEntity(customerId = 1, customerName = "Buyer", productId = 1, productName = "Honey", quantity = 2, totalPrice = 49.98, orderType = "Retail", merchantName = "Al-Baraka"))
        db.chatReportDao().insertChatReport(ChatReportEntity(orderId = 1, customerMessage = "Broken", merchantMessage = "Courier", disputeReason = "Damaged"))
        db.bankCardDao().insertBankCard(BankCardEntity(cardNumber = "4111111111111111", userId = 1, cardHolder = "Buyer", expiryDate = "12/30", cvv = "123"))

        val json = viewModel.buildDashboardExport()
        val root = org.json.JSONObject(json)

        // Must match FORMAT in dashboard/index.html, which rejects anything else.
        assertEquals("amer-store-export", root.getString("format"))
        assertEquals("buyer@example.com", root.getJSONArray("users").getJSONObject(0).getString("email"))
        assertEquals(49.98, root.getJSONArray("orders").getJSONObject(0).getDouble("totalPrice"), 0.001)
        assertEquals("Damaged", root.getJSONArray("chatReports").getJSONObject(0).getString("disputeReason"))
        assertNotNull(root.getJSONArray("products"))
        assertEquals(false, json.contains("4111111111111111") || json.contains("cvv"))
    }

    @Test
    fun testSearchHistoryFlowAndStateTransitions() = runBlocking {
        // Assert initial state is empty
        var recentSearches = viewModel.searchHistory.value
        assertEquals(0, recentSearches.size)

        // Add search query
        viewModel.addToSearchHistory("Product Lookup", "Organic Milk")
        
        recentSearches = viewModel.searchHistory.value
        assertEquals(1, recentSearches.size)
        assertEquals("Organic Milk", recentSearches[0].query)

        // Clear search history
        viewModel.clearSearchHistory()
        recentSearches = viewModel.searchHistory.value
        assertEquals(0, recentSearches.size)
    }
}
