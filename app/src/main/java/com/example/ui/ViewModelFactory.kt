package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.*

class MarketViewModelFactory(
    private val userDao: UserDao,
    private val productDao: ProductDao,
    private val savedProductDao: SavedProductDao,
    private val orderDao: OrderDao,
    private val chatReportDao: ChatReportDao,
    private val cartItemDao: CartItemDao,
    private val bankCardDao: BankCardDao,
    private val searchQueryDao: SearchQueryDao,
    private val familyShoppingListDao: FamilyShoppingListDao,
    private val storedOrganicItemDao: StoredOrganicItemDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MarketViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MarketViewModel(
                userDao,
                productDao,
                savedProductDao,
                orderDao,
                chatReportDao,
                cartItemDao,
                bankCardDao,
                searchQueryDao,
                familyShoppingListDao,
                storedOrganicItemDao
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
