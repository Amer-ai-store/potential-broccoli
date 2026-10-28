package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY id DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: Int): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE email = :email OR phoneNumber = :phone LIMIT 1")
    suspend fun getUserByEmailOrPhone(email: String, phone: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET permissionGranted = :granted WHERE id = :userId")
    suspend fun updatePermission(userId: Int, granted: Boolean)
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY id DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    fun getProductById(id: Int): Flow<ProductEntity?>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductByIdSuspend(id: Int): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProducts(products: List<ProductEntity>)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProduct(id: Int)
}

@Dao
interface SavedProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedProduct(saved: SavedProductEntity)

    @Query("DELETE FROM saved_products WHERE userId = :userId AND productId = :productId")
    suspend fun deleteSavedProduct(userId: Int, productId: Int)

    @Query("SELECT products.* FROM products INNER JOIN saved_products ON products.id = saved_products.productId WHERE saved_products.userId = :userId")
    fun getSavedProductsForUser(userId: Int): Flow<List<ProductEntity>>
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY id DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE customerId = :customerId ORDER BY id DESC")
    fun getOrdersForCustomer(customerId: Int): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE status != 'Delivered' ORDER BY id DESC")
    fun getActiveOrders(): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Int, status: String)

    @Query("UPDATE orders SET deliveryDriverId = :driverId, status = :status WHERE id = :orderId")
    suspend fun assignOrderDriver(orderId: Int, driverId: Int, status: String)
}

@Dao
interface ChatReportDao {
    @Query("SELECT * FROM chat_reports ORDER BY id DESC")
    fun getAllChatReports(): Flow<List<ChatReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatReport(report: ChatReportEntity)
}

@Dao
interface CartItemDao {
    @Query("SELECT * FROM cart_items WHERE userId = :userId ORDER BY id DESC")
    fun getCartItems(userId: Int): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity)

    @Update
    suspend fun updateCartItem(item: CartItemEntity)

    @Query("SELECT * FROM cart_items WHERE userId = :userId AND productId = :productId AND orderType = :orderType LIMIT 1")
    suspend fun getCartItemSpecific(userId: Int, productId: Int, orderType: String): CartItemEntity?

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun deleteCartItemById(id: Int)

    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun clearCart(userId: Int)
}

@Dao
interface BankCardDao {
    @Query("SELECT * FROM bank_cards WHERE userId = :userId ORDER BY isPrimary DESC, cardNumber DESC")
    fun getBankCards(userId: Int): Flow<List<BankCardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBankCard(card: BankCardEntity)

    @Query("DELETE FROM bank_cards WHERE cardNumber = :cardNumber AND userId = :userId")
    suspend fun deleteBankCard(cardNumber: String, userId: Int)
}

@Dao
interface SearchQueryDao {
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 15")
    fun getRecentSearchQueries(): Flow<List<SearchQueryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchQuery(query: SearchQueryEntity)

    @Query("DELETE FROM search_history")
    suspend fun clearAll()

    @Query("DELETE FROM search_history WHERE query = :query")
    suspend fun deleteQuery(query: String)
}

@Dao
interface FamilyShoppingListDao {
    @Query("SELECT * FROM family_shopping_list ORDER BY isChecked ASC, CASE priority WHEN 'High' THEN 1 WHEN 'Medium' THEN 2 ELSE 3 END, id DESC")
    fun getAllFamilyItems(): Flow<List<FamilyShoppingListEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamilyItem(item: FamilyShoppingListEntity): Long

    @Query("UPDATE family_shopping_list SET isChecked = :isChecked WHERE id = :id")
    suspend fun updateFamilyItemStatus(id: Int, isChecked: Boolean)

    @Query("DELETE FROM family_shopping_list WHERE id = :id")
    suspend fun deleteFamilyItem(id: Int)

    @Query("DELETE FROM family_shopping_list")
    suspend fun clearAllFamilyItems()
}

@Dao
interface StoredOrganicItemDao {
    @Query("SELECT * FROM stored_organic_items WHERE userId = :userId ORDER BY expiryDate ASC")
    fun getStoredItems(userId: Int): Flow<List<StoredOrganicItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStoredItem(item: StoredOrganicItemEntity): Long

    @Query("UPDATE stored_organic_items SET isNotified = 1 WHERE id = :id")
    suspend fun markNotified(id: Int)

    @Query("DELETE FROM stored_organic_items WHERE id = :id")
    suspend fun deleteStoredItem(id: Int)

    @Query("DELETE FROM stored_organic_items WHERE userId = :userId")
    suspend fun clearStoredItems(userId: Int)
}

@Database(
    entities = [
        UserEntity::class,
        ProductEntity::class,
        SavedProductEntity::class,
        OrderEntity::class,
        ChatReportEntity::class,
        CartItemEntity::class,
        BankCardEntity::class,
        SearchQueryEntity::class,
        FamilyShoppingListEntity::class,
        StoredOrganicItemEntity::class
    ],
    version = 9,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun savedProductDao(): SavedProductDao
    abstract fun orderDao(): OrderDao
    abstract fun chatReportDao(): ChatReportDao
    abstract fun cartItemDao(): CartItemDao
    abstract fun bankCardDao(): BankCardDao
    abstract fun searchQueryDao(): SearchQueryDao
    abstract fun familyShoppingListDao(): FamilyShoppingListDao
    abstract fun storedOrganicItemDao(): StoredOrganicItemDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: android.content.Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "aicommerce.db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
    }
}

