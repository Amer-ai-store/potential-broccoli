package com.example.ui

import android.app.Application
import android.util.Log
import com.example.BuildConfig
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private const val ADMIN_EMAIL = "zxzx.Mohammad91@gmail.com"

class MarketViewModel(
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
) : ViewModel() {

    private val TAG = "MarketViewModel"

    // Onboarding & Interests States
    private val _onboardingCompleted = MutableStateFlow(false)
    val onboardingCompleted = _onboardingCompleted.asStateFlow()

    private val _chosenInterests = MutableStateFlow<Set<String>>(emptySet())
    val chosenInterests = _chosenInterests.asStateFlow()

    private val _geminiRecommendedCategories = MutableStateFlow<List<String>>(emptyList())
    val geminiRecommendedCategories = _geminiRecommendedCategories.asStateFlow()

    private val _isGeminiRecommending = MutableStateFlow(false)
    val isGeminiRecommending = _isGeminiRecommending.asStateFlow()

    private val _customerActiveTab = MutableStateFlow(0)
    val customerActiveTab = _customerActiveTab.asStateFlow()

    fun setCustomerActiveTab(tab: Int) {
        _customerActiveTab.value = tab
    }

    private val _aiExtractedInterests = MutableStateFlow<Set<String>>(emptySet())
    val aiExtractedInterests = _aiExtractedInterests.asStateFlow()

    private val _scannedStores = MutableStateFlow<List<String>>(emptyList())
    val scannedStores = _scannedStores.asStateFlow()

    private val _isScanningGallery = MutableStateFlow(false)
    val isScanningGallery = _isScanningGallery.asStateFlow()

    // Cryptocurrency Wallet Balance States
    private val _cryptoWalletMtc = MutableStateFlow(5000.0) // 5000 MARKET Coin (MTC)
    val cryptoWalletMtc = _cryptoWalletMtc.asStateFlow()

    private val _cryptoWalletBtc = MutableStateFlow(0.045)
    val cryptoWalletBtc = _cryptoWalletBtc.asStateFlow()

    private val _cryptoWalletEth = MutableStateFlow(0.65)
    val cryptoWalletEth = _cryptoWalletEth.asStateFlow()

    private val _cryptoWalletUsdt = MutableStateFlow(320.0)
    val cryptoWalletUsdt = _cryptoWalletUsdt.asStateFlow()

    private val _cryptoWalletAigh = MutableStateFlow(1000.0)
    val cryptoWalletAigh = _cryptoWalletAigh.asStateFlow()

    private val _walletTransactions = MutableStateFlow<List<String>>(
        listOf(
            "System initialization award: +4000 MTC",
            "Airdrop transfer reward: +1000 MTC",
            "Sign-up bonus: +320 USDT"
        )
    )
    val walletTransactions = _walletTransactions.asStateFlow()

    // Cross-Role Messenger States
    private val _directMessages = MutableStateFlow<List<DirectMessage>>(
        listOf(
            DirectMessage(1, "Merchant", "Al-Baraka Honey Shop", "Customer", "Welcome! Let me know if you need any pure honey recommendations! 🍯"),
            DirectMessage(2, "Delivery", "Samer (Express Driver)", "Customer", "Hi! I will be delivering your orders today. Active GPS tracking is live! 🚚"),
            DirectMessage(3, "Admin", "Platform Coordinator", "Customer", "Hello! Platform support is active. Any disputes will be resolved instantly! ⚙️")
        )
    )
    val directMessages = _directMessages.asStateFlow()

    // معالج أخطاء مركزي لحماية Coroutine Scopes من الانهيار المفاجئ
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "Coroutine Exception caught", throwable)
        val userFriendlyMessage = when (throwable) {
            is java.net.UnknownHostException, is java.net.ConnectException -> "تحقق من اتصالك بالإنترنت."
            is android.database.sqlite.SQLiteException -> "خطأ في قاعدة البيانات، يرجى المحاولة لاحقاً."
            else -> "حدث خطأ غير متوقع: ${throwable.localizedMessage ?: "غير معروف"}"
        }
        triggerError(userFriendlyMessage)
    }

    // UI States
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser = _currentUser.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val cartItems = _currentUser
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList())
            else cartItemDao.getCartItems(user.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val bankCards = _currentUser
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList())
            else bankCardDao.getBankCards(user.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val storedOrganicItems = _currentUser
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList())
            else storedOrganicItemDao.getStoredItems(user.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val carbonSavings = storedOrganicItems
        .map { items ->
            var totalCo2Saved = 0.0
            items.forEach { item ->
                val nameLower = item.productName.lowercase()
                val factor = when {
                    nameLower.contains("milk") || nameLower.contains("dairy") -> 0.8
                    nameLower.contains("honey") -> 0.6
                    nameLower.contains("strawberry") || nameLower.contains("fruit") || nameLower.contains("berry") -> 0.5
                    else -> 0.4
                }
                totalCo2Saved += factor * item.quantity
            }
            totalCo2Saved
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val shoppingList = familyShoppingListDao.getAllFamilyItems()
        .map { list -> list.sortedBy { it.category } } // Using category as a proxy for aisle initially
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentRole = MutableStateFlow("Customer") // "Customer", "Merchant", "Delivery", "Admin"
    val currentRole = _currentRole.asStateFlow()

    val products = productDao.getAllProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders = orderDao.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsersPrivate = userDao.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatReports = chatReportDao.getAllChatReports()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _savedProducts = MutableStateFlow<List<ProductEntity>>(emptyList())
    val savedProducts = _savedProducts.asStateFlow()

    private val _aiResponse = MutableStateFlow<String?>(null)
    val aiResponse = _aiResponse.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading = _isAiLoading.asStateFlow()

    // --- NEW ADVANCED STATES ---
    // Chatbot States
    private val _customerChatHistory = MutableStateFlow<List<ChatMessage>>(emptyList())
    val customerChatHistory = _customerChatHistory.asStateFlow()

    private val _merchantChatHistory = MutableStateFlow<List<ChatMessage>>(emptyList())
    val merchantChatHistory = _merchantChatHistory.asStateFlow()

    private val _deliveryChatHistory = MutableStateFlow<List<ChatMessage>>(emptyList())
    val deliveryChatHistory = _deliveryChatHistory.asStateFlow()

    private val _adminChatHistory = MutableStateFlow<List<ChatMessage>>(emptyList())
    val adminChatHistory = _adminChatHistory.asStateFlow()

    private val _isChatbotLoading = MutableStateFlow(false)
    val isChatbotLoading = _isChatbotLoading.asStateFlow()

    // Gemini Live Voice Call States
    private val _isVoiceCallActive = MutableStateFlow(false)
    val isVoiceCallActive = _isVoiceCallActive.asStateFlow()

    private val _voiceCallStatus = MutableStateFlow("Idle")
    val voiceCallStatus = _voiceCallStatus.asStateFlow()

    private val _voiceTranscriptionResult = MutableStateFlow("")
    val voiceTranscriptionResult = _voiceTranscriptionResult.asStateFlow()

    // Deep reasoning (High Thinking) States
    private val _isHighThinkingEnabled = MutableStateFlow(false)
    val isHighThinkingEnabled = _isHighThinkingEnabled.asStateFlow()

    private val _thinkingSteps = MutableStateFlow<List<String>>(emptyList())
    val thinkingSteps = _thinkingSteps.asStateFlow()

    // AI Media Studio States (Veo 3 & Nano Banana)
    private val _generatedVideoUrl = MutableStateFlow<String?>(null)
    val generatedVideoUrl = _generatedVideoUrl.asStateFlow()

    private val _generatedImageUrl = MutableStateFlow<String?>(null)
    val generatedImageUrl = _generatedImageUrl.asStateFlow()

    private val _customPromoTitle = MutableStateFlow<String?>(null)
    val customPromoTitle = _customPromoTitle.asStateFlow()

    private val _customPromoDesc = MutableStateFlow<String?>(null)
    val customPromoDesc = _customPromoDesc.asStateFlow()

    private val _customPromoMediaType = MutableStateFlow<String?>(null) // "Image" or "Video"
    val customPromoMediaType = _customPromoMediaType.asStateFlow()

    // Firestore Real-Time Emulation Console
    val firebaseConnectedStatus = MutableStateFlow("Firebase Connected (Sandboxed Local Cloud Mode)")

    // Profile Extended States
    private val _userGender = MutableStateFlow("Male")
    val userGender = _userGender.asStateFlow()

    private val _userAge = MutableStateFlow(25)
    val userAge = _userAge.asStateFlow()

    private val _userAddress = MutableStateFlow("Amman, Jordan")
    val userAddress = _userAddress.asStateFlow()

    private val _deliveryLocations = MutableStateFlow<List<DeliveryLocation>>(
        listOf(
            DeliveryLocation(label = "Home 🏠", address = "Amman, Al-Madina Al-Monawara St", latitude = 31.9522, longitude = 35.9158, isPrimary = true),
            DeliveryLocation(label = "Office 💼", address = "King Hussein Business Park, Building 23", latitude = 31.9715, longitude = 35.8361, isPrimary = false)
        )
    )
    val deliveryLocations = _deliveryLocations.asStateFlow()

    private val _userPassword = MutableStateFlow("")
    val userPassword = _userPassword.asStateFlow()

    // AI Features Toggles
    private val _aiVoiceEnabled = MutableStateFlow(true)
    val aiVoiceEnabled = _aiVoiceEnabled.asStateFlow()

    private val _aiDashboardEnabled = MutableStateFlow(true)
    val aiDashboardEnabled = _aiDashboardEnabled.asStateFlow()

    private val _aiRecommendationEnabled = MutableStateFlow(true)
    val aiRecommendationEnabled = _aiRecommendationEnabled.asStateFlow()

    private val _lastCheckInDate = MutableStateFlow<String?>(null)
    val lastCheckInDate = _lastCheckInDate.asStateFlow()

    fun updateProfile(name: String, email: String, phone: String, gender: String, age: Int, address: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val updatedUser = user.copy(name = name, email = email, phoneNumber = phone)
            userDao.updateUser(updatedUser)
            _currentUser.value = updatedUser
            
            _userGender.value = gender
            _userAge.value = age
            _userAddress.value = address
        }
    }

    fun changePassword(newPassword: String): Boolean {
        val regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{6,}$".toRegex()
        return if (newPassword.matches(regex)) {
            _userPassword.value = newPassword
            true
        } else {
            false
        }
    }

    fun toggleAiVoiceEnabled(enabled: Boolean) {
        _aiVoiceEnabled.value = enabled
    }

    fun toggleAiDashboardEnabled(enabled: Boolean) {
        _aiDashboardEnabled.value = enabled
    }

    fun toggleAiRecommendationEnabled(enabled: Boolean) {
        _aiRecommendationEnabled.value = enabled
    }

    fun addDeliveryLocation(label: String, address: String, latitude: Double, longitude: Double) {
        val list = _deliveryLocations.value
        val isPrimary = list.isEmpty() || !list.any { it.isPrimary }
        val newLoc = DeliveryLocation(label = label, address = address, latitude = latitude, longitude = longitude, isPrimary = isPrimary)
        _deliveryLocations.value = list + newLoc
        if (isPrimary) {
            _userAddress.value = address
        }
    }

    fun updateDeliveryLocation(id: String, label: String, address: String, latitude: Double, longitude: Double) {
        _deliveryLocations.value = _deliveryLocations.value.map {
            if (it.id == id) {
                it.copy(label = label, address = address, latitude = latitude, longitude = longitude)
            } else {
                it
            }
        }
        val primary = _deliveryLocations.value.find { it.isPrimary }
        if (primary != null) {
            _userAddress.value = primary.address
        }
    }

    fun deleteDeliveryLocation(id: String) {
        val oldPrimary = _deliveryLocations.value.find { it.id == id }?.isPrimary ?: false
        val list = _deliveryLocations.value.filter { it.id != id }
        if (oldPrimary && list.isNotEmpty()) {
            _deliveryLocations.value = list.mapIndexed { idx, loc ->
                if (idx == 0) loc.copy(isPrimary = true) else loc
            }
        } else {
            _deliveryLocations.value = list
        }
        val primary = _deliveryLocations.value.find { it.isPrimary }
        if (primary != null) {
            _userAddress.value = primary.address
        }
    }

    fun setPrimaryDeliveryLocation(id: String) {
        _deliveryLocations.value = _deliveryLocations.value.map {
            it.copy(isPrimary = (it.id == id))
        }
        val primary = _deliveryLocations.value.find { it.isPrimary }
        if (primary != null) {
            _userAddress.value = primary.address
        }
    }

    fun performDailyCheckIn(): Double? {
        val randomVal = Math.random()
        val reward = when {
            randomVal < 0.60 -> 0.02 + Math.random() * (0.1 - 0.02) // 60%
            randomVal < 0.80 -> 0.1 + Math.random() * (0.2 - 0.1)   // 20%
            randomVal < 0.95 -> 0.2 + Math.random() * (0.3 - 0.2)   // 15%
            randomVal < 0.97 -> 0.3 + Math.random() * (0.34 - 0.3)  // 2%
            randomVal < 0.98 -> 0.35 + Math.random() * (0.5 - 0.35) // 1%
            randomVal < 0.985 -> 0.5 + Math.random() * (1.0 - 0.5)  // 0.5%
            randomVal < 0.989 -> 1.0 + Math.random() * (1.1 - 1.0)  // 0.4%
            randomVal < 0.990 -> 15.0                              // 0.1%
            else -> 0.02 + Math.random() * (0.1 - 0.02)             // Fallback for remaining 1%
        }
        val roundedReward = Math.round(reward * 100.0) / 100.0
        _cryptoWalletUsdt.value += roundedReward
        _walletTransactions.value = _walletTransactions.value + "Daily Check-in Reward: +$$roundedReward USDT"
        
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
        _lastCheckInDate.value = today
        return roundedReward
    }

    // Global Error and Toasts
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun triggerError(msg: String) {
        _errorMessage.value = msg
    }

    fun clearError() {
        _errorMessage.value = null
    }

    // Skeleton loading indicator for products
    private val _isProductsLoading = MutableStateFlow(true)
    val isProductsLoading = _isProductsLoading.asStateFlow()

    fun refreshProducts() {
        viewModelScope.launch(exceptionHandler) {
            _isProductsLoading.value = true
            kotlinx.coroutines.delay(1200)
            _isProductsLoading.value = false
        }
    }

    // Real-Time Price Comparison module
    private val _priceComparisonState = MutableStateFlow<Map<String, Double>?>(null)
    val priceComparisonState = _priceComparisonState.asStateFlow()

    fun getBestPrice(product: ProductEntity): Double {
        // Logic: if bestPrice is set, use it. Otherwise, compare with retailPrice and offer a discount.
        return if (product.bestPrice > 0.0) product.bestPrice else product.retailPrice * 0.95
    }

    private val _isComparingPrice = MutableStateFlow(false)
    val isComparingPrice = _isComparingPrice.asStateFlow()

    fun loadPriceComparison(productName: String) {
        viewModelScope.launch(exceptionHandler) {
            _isComparingPrice.value = true
            _priceComparisonState.value = null
            addToSearchHistory("Product Lookup", productName, "Scraped live comparative rates")
            try {
                kotlinx.coroutines.delay(800)
                val basePrice = when {
                    productName.contains("Honey") || productName.contains("عسل") -> 24.99
                    productName.contains("Coffee") || productName.contains("قهوة") -> 12.50
                    productName.contains("Milk") || productName.contains("حليب") -> 4.80
                    productName.contains("Eggs") || productName.contains("بيض") -> 19.99
                    productName.contains("Energy") || productName.contains("طاقة") -> 3.99
                    productName.contains("Granola") || productName.contains("شوفان") -> 6.49
                    else -> 15.0
                }
                _priceComparisonState.value = mapOf(
                    "Amazon Prime Store" to basePrice * 1.15,
                    "Carrefour Hypermarket" to basePrice * 1.05,
                    "Local Organic Boutique" to basePrice * 1.25,
                    "Our AI Smart Marketplace" to basePrice
                )
            } catch (e: Exception) {
                // Already handled by exceptionHandler
                _priceComparisonState.value = emptyMap()
            } finally {
                _isComparingPrice.value = false
            }
        }
    }

    // AI-Curated Social Media Trending Feed
    private val _trendingProducts = MutableStateFlow<List<TrendingProduct>>(emptyList())
    val trendingProducts = _trendingProducts.asStateFlow()

    private val _isTrendingLoading = MutableStateFlow(false)
    val isTrendingLoading = _isTrendingLoading.asStateFlow()

    fun fetchAITrendingProducts() {
        viewModelScope.launch {
            _isTrendingLoading.value = true
            try {
                val prompt = "List 3 viral e-commerce products on TikTok and Instagram. Output name, category, price, reason for trend."
                val response = ModelService.generateAiContent(prompt)
                // Since response is a raw text description, we will parse or curate 3 gorgeous high-fidelity trending products
                kotlinx.coroutines.delay(1200)
                _trendingProducts.value = listOf(
                    TrendingProduct(
                        id = 101,
                        name = "Retro Mechanical Cyber Keyboard (كيبورد ميكانيكي سايبر)",
                        category = "Electronics",
                        retailPrice = 49.99,
                        wholesalePrice = 38.00,
                        imageUrl = "https://images.unsplash.com/photo-1587829741301-dc798b83add3?auto=format&fit=crop&q=80&w=400",
                        description = "Ultra-premium clicky cyber aesthetics with dynamic RGB sidelights.",
                        trendPlatform = "TikTok Viral",
                        trendTag = "#MechanicalKeyboard #CyberpunkSetup (14M Views)",
                        aiInsight = "AI Analytics detect a 340% increase in desktop setup engagement this week."
                    ),
                    TrendingProduct(
                        id = 102,
                        name = "Authentic Ceramic Matcha Whisking Set (طقم خفق الماتشا الخزفي)",
                        category = "Wellness / Kitchen",
                        retailPrice = 29.99,
                        wholesalePrice = 22.00,
                        imageUrl = "https://images.unsplash.com/photo-1536256263959-770b48d82b0a?auto=format&fit=crop&q=80&w=400",
                        description = "Hand-crafted matcha tea set including a beautiful ceramic bowl, whisk, and holder.",
                        trendPlatform = "Instagram Reels",
                        trendTag = "#MatchaCeremony #AestheticMorning (8M Reels)",
                        aiInsight = "AI Sentiment indicates high consumer preference for organic wellness rituals."
                    ),
                    TrendingProduct(
                        id = 103,
                        name = "Self-Watering Minimalist Smart Pot (إناء ذكي مائي مبسط)",
                        category = "Home & Garden",
                        retailPrice = 18.50,
                        wholesalePrice = 14.00,
                        imageUrl = "https://images.unsplash.com/photo-1485955900006-10f4d324d411?auto=format&fit=crop&q=80&w=400",
                        description = "Smart reservoir-based plant pot that ensures automated water dosing for 14 days.",
                        trendPlatform = "Pinterest & Twitter",
                        trendTag = "#GreenHome #PlantParent (5M pins)",
                        aiInsight = "AI Market scrapers identified high demand for hassle-free indoor greening products."
                    )
                )
            } catch (e: Exception) {
                _errorMessage.value = "Failed to fetch AI trending indices: ${e.message}"
            } finally {
                _isTrendingLoading.value = false
            }
        }
    }

    // Camera Multimodal Search with Gemini
    private val _cameraSearchState = MutableStateFlow<CameraSearchResult?>(null)
    val cameraSearchState = _cameraSearchState.asStateFlow()

    private val _isCameraSearching = MutableStateFlow(false)
    val isCameraSearching = _isCameraSearching.asStateFlow()

    fun performGeminiCameraSearch(imageName: String, bitmap: android.graphics.Bitmap) {
        viewModelScope.launch {
            _isCameraSearching.value = true
            _cameraSearchState.value = null
            try {
                val prompt = "Identify this product: '$imageName'. Match it with organic mountain honey, supreme Turkish coffee, farm-direct milk, organic premium eggs, cognitive energy, or cocoa almond granola."
                val matchResultText = ModelService.analyzeProductImage(bitmap, prompt)
                kotlinx.coroutines.delay(1500) // Beautiful scanning feel
                
                // Parse the matched item based on result text
                val matchedName = when {
                    matchResultText.contains("Honey") || matchResultText.contains("عسل") -> "Organic Mountain Honey (عسل جبلي طبيعي)"
                    matchResultText.contains("Coffee") || matchResultText.contains("قهوة") -> "Supreme Turkish Coffee Blend (قهوة تركية فاخرة)"
                    matchResultText.contains("Milk") || matchResultText.contains("حليب") -> "Fresh Organic Milk - Farm Direct (حليب مزارع طازج)"
                    matchResultText.contains("Eggs") || matchResultText.contains("بيض") -> "Organic Premium Farm Eggs Carton (30 Pcs)"
                    matchResultText.contains("Energy") || matchResultText.contains("طاقة") -> "Supercharged Cognitive Energy Drink (مشروب الطاقة الذكي)"
                    matchResultText.contains("Granola") || matchResultText.contains("شوفان") -> "Rich Cocoa Almond Granola Bar (ألواح الشوفان بالكاكاو)"
                    else -> "Organic Mountain Honey (عسل جبلي طبيعي)"
                }
                
                _cameraSearchState.value = CameraSearchResult(
                    identifiedProduct = matchedName,
                    confidence = 0.96,
                    rationale = matchResultText
                )
                addToSearchHistory("Visual Search", imageName, "Identified: $matchedName")
            } catch (e: Exception) {
                _errorMessage.value = "Failed to run Camera Visual search: ${e.message}"
            } finally {
                _isCameraSearching.value = false
            }
        }
    }

    fun clearCameraSearch() {
        _cameraSearchState.value = null
    }

    // Auth flows
    private val _verificationStep = MutableStateFlow(0) // 0: Enter details, 1: Verification code
    val verificationStep = _verificationStep.asStateFlow()

    private val _verificationCodeSent = MutableStateFlow<String?>(null)
    val verificationCodeSent = _verificationCodeSent.asStateFlow()

    private val _pendingUserToRegister = MutableStateFlow<UserEntity?>(null)

    // Language State ("ar" for Arabic, "en" for English)
    private val _appLanguage = MutableStateFlow("ar")
    val appLanguage = _appLanguage.asStateFlow()

    // Dark Mode State
    private val _appDarkMode = MutableStateFlow(false)
    val appDarkMode = _appDarkMode.asStateFlow()

    fun toggleDarkMode() {
        _appDarkMode.value = !_appDarkMode.value
    }

    // Google Map Interactive States
    private val _showGoogleMap = MutableStateFlow(false)
    val showGoogleMap = _showGoogleMap.asStateFlow()

    private val _mapTargetAddress = MutableStateFlow("")
    val mapTargetAddress = _mapTargetAddress.asStateFlow()

    fun dismissGoogleMap() {
        _showGoogleMap.value = false
    }

    fun triggerGoogleMap(address: String) {
        _mapTargetAddress.value = address
        _showGoogleMap.value = true
    }

    fun setAppLanguage(lang: String) {
        _appLanguage.value = lang
    }

    fun toggleLanguage() {
        _appLanguage.value = if (_appLanguage.value == "ar") "en" else "ar"
    }

    init {
        // Pre-populate some cool premium products to ensure a beautiful out-of-the-box experience
        viewModelScope.launch {
            _isProductsLoading.value = true
            // Read the table once: collecting the `products` StateFlow would see its initial
            // emptyList() before Room loads and re-seed duplicates on every launch.
            productDao.getAllProducts().first().let { currentList ->
                if (currentList.isEmpty()) {
                    val defaultProducts = listOf(
                        ProductEntity(
                            name = "Organic Mountain Honey (عسل جبلي طبيعي)",
                            category = "Organic Foods",
                            retailPrice = 24.99,
                            wholesalePrice = 18.20,
                            imageUrl = "https://images.unsplash.com/photo-1587049352846-4a222e784d38?auto=format&fit=crop&q=80&w=400",
                            description = "Grade A pure nectar gathered from wild lavender and wildflower fields.",
                            ingredients = "100% natural cold-extracted raw honey, rich in natural pollen particles.",
                            stockQuantity = 120,
                            isRegisteredMerchant = true,
                            merchantName = "Al-Baraka Bee Gardens (Certified)",
                            salesHistory = 450
                        ),
                        ProductEntity(
                            name = "Supreme Turkish Coffee Blend (قهوة تركية فاخرة)",
                            category = "Spiced Coffee & Tea",
                            retailPrice = 12.50,
                            wholesalePrice = 9.00,
                            imageUrl = "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?auto=format&fit=crop&q=80&w=400",
                            description = "Authentic stone-ground dark Arabica infused with organic cardamom and saffron hints.",
                            ingredients = "Grounded Arabica beans, ground cardamom hulls (3%), authentic Iranian saffron threads.",
                            stockQuantity = 85,
                            isRegisteredMerchant = true,
                            merchantName = "Al-Taj Premium Foodstuffs (Certified)",
                            salesHistory = 620
                        ),
                        ProductEntity(
                            name = "Fresh Organic Milk - Farm Direct (حليب مزارع طازج)",
                            category = "Dairy",
                            retailPrice = 4.80,
                            wholesalePrice = 3.50,
                            imageUrl = "https://images.unsplash.com/photo-1550583724-b2692b85b150?auto=format&fit=crop&q=80&w=400",
                            description = "Fresh grass-fed milk delivered daily. Discovered by AI Marketplace Search.",
                            ingredients = "100% whole cow milk pasteurized, enriched with organic Vitamin D.",
                            stockQuantity = 250,
                            isRegisteredMerchant = false, // Virtual Merchant!
                            merchantName = "Scraped Instagram Page Marketplace (@fresh_meadows_milk)",
                            salesHistory = 1100
                        ),
                        ProductEntity(
                            name = "Organic Premium Farm Eggs Carton (30 Pcs)",
                            category = "Dairy",
                            retailPrice = 19.99,
                            wholesalePrice = 15.00,
                            imageUrl = "https://images.unsplash.com/photo-1506976785307-8732e854ad03?auto=format&fit=crop&q=80&w=400",
                            description = "Daily collected cage-free golden yolk large organic eggs in strong cartons.",
                            ingredients = "Fresh raw organic shell eggs.",
                            stockQuantity = 150,
                            isRegisteredMerchant = true,
                            merchantName = "FreshFarm Co. (Certified)",
                            salesHistory = 920
                        ),
                        ProductEntity(
                            name = "Supercharged Cognitive Energy Drink (مشروب الطاقة الذكي)",
                            category = "Health Drinks",
                            retailPrice = 3.99,
                            wholesalePrice = 2.90,
                            imageUrl = "https://images.unsplash.com/photo-1622483767028-3f66f32aef97?auto=format&fit=crop&q=80&w=400",
                            description = "Zero sugar natural focus booster with organic ginseng. Found via TikTok Ads scrape.",
                            ingredients = "Carbonated spring water, Ginseng extract, Vitamin B6 & B12, organic green coffee bean extract.",
                            stockQuantity = 8, // Low Stock for Merchant Alerts!
                            isRegisteredMerchant = false, // Virtual Merchant!
                            merchantName = "TikTok Ad Shop Scraped Supplier (@nexus_energy_co)",
                            salesHistory = 3400
                        ),
                        ProductEntity(
                            name = "Rich Cocoa Almond Granola Bar (ألواح الشوفان بالكاكاو)",
                            category = "Snacks",
                            retailPrice = 6.49,
                            wholesalePrice = 4.80,
                            imageUrl = "https://images.unsplash.com/photo-1568254183919-78a4f43a2877?auto=format&fit=crop&q=80&w=400",
                            description = "Hand-mixed whole oat granola clusters with high-grade almond slices and dark cocoa flakes.",
                            ingredients = "Oat flakes, honey, cacao butter, dark chocolate chips (sugar, chocolate liquor, cocoa butter), organic almond slivers.",
                            stockQuantity = 95,
                            isRegisteredMerchant = true,
                            merchantName = "Al-Baraka Bee Gardens (Certified)",
                            salesHistory = 270
                        ),
                        ProductEntity(
                            name = "Classic Fit Premium Linen Shirt (قميص كتان فاخر)",
                            category = "Men's Clothing",
                            retailPrice = 39.99,
                            wholesalePrice = 28.50,
                            imageUrl = "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?auto=format&fit=crop&q=80&w=400",
                            description = "Breathable pure linen shirt, perfect for warm summer days and casual business wear.",
                            ingredients = "100% Organic Linen, naturally dyed fibers, pearl buttons.",
                            stockQuantity = 60,
                            isRegisteredMerchant = true,
                            merchantName = "Modern Fit Apparel Co.",
                            salesHistory = 140
                        ),
                        ProductEntity(
                            name = "Elegant Floral Summer Dress (فستان صيفي أنيق)",
                            category = "Women's Clothing",
                            retailPrice = 49.99,
                            wholesalePrice = 35.00,
                            imageUrl = "https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?auto=format&fit=crop&q=80&w=400",
                            description = "Flowing mid-length floral dress made from sustainable viscose blend.",
                            ingredients = "85% Viscose, 15% Linen floral weave.",
                            stockQuantity = 45,
                            isRegisteredMerchant = true,
                            merchantName = "Chic Silhouette Boutique",
                            salesHistory = 190
                        ),
                        ProductEntity(
                            name = "UltraLight Breathable Running Shoes (حذاء رياضي مريح)",
                            category = "Shoes & Footwear",
                            retailPrice = 59.99,
                            wholesalePrice = 42.00,
                            imageUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&q=80&w=400",
                            description = "Ergonomic shock-absorbing sports sneakers with knit mesh upper.",
                            ingredients = "Recycled polyester knit upper, EVA foam comfort midsole, high-traction rubber outsole.",
                            stockQuantity = 70,
                            isRegisteredMerchant = true,
                            merchantName = "ActiveWalk Athletic Co.",
                            salesHistory = 310
                        ),
                        ProductEntity(
                            name = "Royal Oud & Saffron Intense (عطر العود الملكي)",
                            category = "Perfumes & Fragrances",
                            retailPrice = 89.99,
                            wholesalePrice = 65.00,
                            imageUrl = "https://images.unsplash.com/photo-1541643600914-78b084683601?auto=format&fit=crop&q=80&w=400",
                            description = "Majestic blends of premium dark agarwood, warm amber, and pure saffron threads.",
                            ingredients = "Agarwood (Oud) extract, Iranian saffron, Spanish jasmine, crystalline gray amber.",
                            stockQuantity = 30,
                            isRegisteredMerchant = true,
                            merchantName = "Scent of Arabia Perfumeries",
                            salesHistory = 420
                        ),
                        ProductEntity(
                            name = "SmartWatch Pro Series 9 (ساعة ذكية رياضية)",
                            category = "Electronics & Mobiles",
                            retailPrice = 199.99,
                            wholesalePrice = 145.00,
                            imageUrl = "https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?auto=format&fit=crop&q=80&w=400",
                            description = "Full active AMOLED display with continuous heart rate and GPS route tracking.",
                            ingredients = "Aero-grade aluminum shell, Gorilla Glass DX, sweat-resistant fluoroelastomer strap.",
                            stockQuantity = 25,
                            isRegisteredMerchant = true,
                            merchantName = "Nexus Tech Labs Inc.",
                            salesHistory = 580
                        ),
                        ProductEntity(
                            name = "Modern Ceramic Accent Vase (فازة سيراميك عصرية)",
                            category = "Furniture & Decor",
                            retailPrice = 29.99,
                            wholesalePrice = 21.00,
                            imageUrl = "https://images.unsplash.com/photo-1578500494198-246f612d3b3d?auto=format&fit=crop&q=80&w=400",
                            description = "Minimalist Nordic-style matte white ceramic vase for pampas grass.",
                            ingredients = "100% fine clay ceramic, dual-fired matte protective glaze coating.",
                            stockQuantity = 50,
                            isRegisteredMerchant = true,
                            merchantName = "Nordic Home Elegance",
                            salesHistory = 230
                        )
                    )
                    productDao.insertAllProducts(defaultProducts)
                }
            }
        }
        viewModelScope.launch {
            kotlinx.coroutines.delay(1500)
            _isProductsLoading.value = false
        }
    }

    fun initiateRegister(name: String, phoneNumber: String, email: String, role: String) {
        viewModelScope.launch {
            val code = (100000..999999).random().toString()
            _verificationCodeSent.value = code
            _pendingUserToRegister.value = UserEntity(
                name = name,
                phoneNumber = phoneNumber,
                email = email,
                role = role,
                permissionGranted = true // auto opt-in for behavioral analytics by default
            )
            _verificationStep.value = 1
            Log.d("AUTH", "SMS / Email confirmation code sent to user: $code")
        }
    }

    fun confirmCode(enteredCode: String): Boolean {
        if (enteredCode == _verificationCodeSent.value) {
            val user = _pendingUserToRegister.value
            if (user != null) {
                viewModelScope.launch {
                    val newUserId = userDao.insertUser(user)
                    val registeredUser = user.copy(id = newUserId.toInt())
                    _currentUser.value = registeredUser
                    _currentRole.value = registeredUser.role
                    _verificationStep.value = 0
                    updateSavedProducts(registeredUser.id)
                }
                return true
            }
        }
        return false
    }

    fun login(emailOrPhone: String, password: String): Boolean {
        val isAdminIdentity = emailOrPhone.trim().equals(ADMIN_EMAIL, ignoreCase = true) || emailOrPhone.trim().lowercase() == "admin"
        val adminPassword = BuildConfig.ADMIN_PASSWORD
        val adminLoginEnabled = adminPassword.isNotEmpty() && adminPassword != "MY_ADMIN_PASSWORD"
        if (isAdminIdentity && !(adminLoginEnabled && password == adminPassword)) {
            // Never fall through to the generic lookup below, which would log in as the Admin row without a password.
            return false
        }
        if (isAdminIdentity) {
            viewModelScope.launch {
                var adminUser = userDao.getUserByEmail(ADMIN_EMAIL)
                if (adminUser == null) {
                    val newAdmin = UserEntity(
                        name = "admin",
                        phoneNumber = "0590000000",
                        email = ADMIN_EMAIL,
                        role = "Admin",
                        permissionGranted = true
                    )
                    val id = userDao.insertUser(newAdmin)
                    adminUser = newAdmin.copy(id = id.toInt())
                } else if (adminUser.role != "Admin" || adminUser.name != "admin") {
                    val updatedAdmin = adminUser.copy(name = "admin", role = "Admin")
                    userDao.insertUser(updatedAdmin)
                    adminUser = updatedAdmin
                }
                _currentUser.value = adminUser
                _currentRole.value = "Admin"
                _verificationStep.value = 0
                updateSavedProducts(adminUser.id)
            }
            return true
        } else {
            viewModelScope.launch {
                val existing = userDao.getUserByEmailOrPhone(emailOrPhone.trim(), emailOrPhone.trim())
                if (existing != null) {
                    _currentUser.value = existing
                    _currentRole.value = existing.role
                    _verificationStep.value = 0
                    updateSavedProducts(existing.id)
                } else {
                    val isEmail = emailOrPhone.contains("@")
                    val newUser = UserEntity(
                        name = if (isEmail) emailOrPhone.split("@").firstOrNull() ?: "User" else "User_$emailOrPhone",
                        phoneNumber = if (isEmail) "" else emailOrPhone,
                        email = if (isEmail) emailOrPhone else "",
                        role = "Customer",
                        permissionGranted = true
                    )
                    val id = userDao.insertUser(newUser)
                    val registered = newUser.copy(id = id.toInt())
                    _currentUser.value = registered
                    _currentRole.value = "Customer"
                    _verificationStep.value = 0
                    updateSavedProducts(registered.id)
                }
            }
            return true
        }
    }

    fun loginWithGoogleSimulated(name: String, email: String, role: String) {
        viewModelScope.launch {
            var existing = userDao.getUserByEmail(email.trim())
            if (existing == null) {
                val newUser = UserEntity(
                    name = name,
                    phoneNumber = "",
                    email = email,
                    role = role,
                    permissionGranted = true
                )
                val id = userDao.insertUser(newUser)
                existing = newUser.copy(id = id.toInt())
            }
            _currentUser.value = existing
            _currentRole.value = existing.role
            _verificationStep.value = 0
            updateSavedProducts(existing.id)
        }
    }

    fun logout() {
        _currentUser.value = null
        _currentRole.value = "Customer"
        _verificationStep.value = 0
        savedProductsJob?.cancel()
        savedProductsJob = null
        _savedProducts.value = emptyList()
        clearAiResponse()
    }

    fun switchRole(role: String) {
        _currentRole.value = role
        clearAiResponse()
    }

    fun grantPermission(granted: Boolean) {
        val user = _currentUser.value
        if (user != null) {
            viewModelScope.launch {
                userDao.updatePermission(user.id, granted)
                _currentUser.value = user.copy(permissionGranted = granted)
            }
        }
    }

    fun completeOnboarding(interests: Set<String>) {
        _chosenInterests.value = interests
        _onboardingCompleted.value = true
        queryGeminiRecommendations(interests)
    }

    fun queryGeminiRecommendations(interests: Set<String>) {
        if (interests.isEmpty()) {
            _geminiRecommendedCategories.value = emptyList()
            return
        }
        viewModelScope.launch {
            _isGeminiRecommending.value = true
            try {
                val prompt = """
                    The user has selected the following core interest categories: ${interests.joinToString(", ")}.
                    We have these available retail categories in our marketplace app:
                    - Organic Foods
                    - Spiced Coffee & Tea
                    - Dairy
                    - Health Drinks
                    - Snacks
                    - Men's Clothing
                    - Women's Clothing
                    - Shoes & Footwear
                    - Perfumes & Fragrances
                    - Electronics & Mobiles
                    - Furniture & Decor
                    
                    Please recommend the top 3 categories that best match the user's interests from the available categories list above.
                    Return ONLY a comma-separated list of the recommended category names exactly as spelled above. Do not include any other text, markdown, or bullet points.
                """.trimIndent()
                val response = com.example.data.ModelService.generateAiContent(prompt)
                android.util.Log.d("MarketViewModel", "Gemini recommendations response: ${response}")
                val recommended = response.split(",")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                if (recommended.isNotEmpty()) {
                    _geminiRecommendedCategories.value = recommended
                } else {
                    _geminiRecommendedCategories.value = interests.toList()
                }
            } catch (e: Exception) {
                android.util.Log.e("MarketViewModel", "Error querying Gemini recommendations: ${e.message}", e)
                _geminiRecommendedCategories.value = interests.toList()
            } finally {
                _isGeminiRecommending.value = false
            }
        }
    }

    fun scanGalleryAndFiles() {
        viewModelScope.launch {
            _isScanningGallery.value = true
            kotlinx.coroutines.delay(2000)
            _aiExtractedInterests.value = setOf("Gemini", "AI Chatbot", "Computer Vision")
            _chosenInterests.value = _chosenInterests.value + setOf("Gemini", "AI Chatbot", "Computer Vision")
            _isScanningGallery.value = false
        }
    }

    fun scanOtherInstalledStores() {
        viewModelScope.launch {
            _scannedStores.value = listOf("Talabat", "Amazon", "AliExpress", "eBay")
            _chosenInterests.value = _chosenInterests.value + setOf("Sweets & Bakery", "Electricals", "Restaurants & Food")
        }
    }

    fun sendDirectMessage(senderRole: String, senderName: String, receiverRole: String, content: String) {
        val nextId = (_directMessages.value.maxOfOrNull { it.id } ?: 0) + 1
        val newMsg = DirectMessage(nextId, senderRole, senderName, receiverRole, content)
        _directMessages.value = _directMessages.value + newMsg

        // Smart reply simulation from the receiver role
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            val replyContent = when (receiverRole) {
                "Merchant" -> {
                    val replies = listOf(
                        "Thank you for contacting us! We have received your query and will check stock quantities immediately. 📦",
                        "Our products are 100% natural and certified organic. Feel free to complete your order! 🌾",
                        "Yes, we can prepare custom gift packaging for this specific order. 🏺"
                    )
                    replies.random()
                }
                "Delivery" -> {
                    val replies = listOf(
                        "I'm on my way to pick up the shipment! 🚚",
                        "Yes, I will deliver it fresh and within the selected time window. 🗺️",
                        "Arriving in approximately 10 minutes to your current location class!"
                    )
                    replies.random()
                }
                "Admin" -> {
                    val replies = listOf(
                        "Platform ticket opened. Our AI Arbitrator has validated your account credentials. ⚙️",
                        "Your transaction was processed safely. Everything looks perfect on our database ledger!",
                        "Refund processed securely back to your selected payment source."
                    )
                    replies.random()
                }
                else -> {
                    "Hello! Received your message. Let's complete our deal. ✔"
                }
            }
            val replyId = nextId + 1
            val replyMsg = DirectMessage(replyId, receiverRole, if (receiverRole == "Merchant") "Al-Baraka Honey Shop" else if (receiverRole == "Delivery") "Samer (Express Driver)" else "Platform Coordinator", senderRole, replyContent)
            _directMessages.value = _directMessages.value + replyMsg
        }
    }

    // Crypto transaction helpers
    fun sendCrypto(token: String, address: String, amount: Double): Boolean {
        if (address.isBlank() || amount <= 0.0) return false
        when (token) {
            "MTC" -> {
                if (_cryptoWalletMtc.value < amount) return false
                _cryptoWalletMtc.value -= amount
            }
            "BTC" -> {
                if (_cryptoWalletBtc.value < amount) return false
                _cryptoWalletBtc.value -= amount
            }
            "ETH" -> {
                if (_cryptoWalletEth.value < amount) return false
                _cryptoWalletEth.value -= amount
            }
            "USDT" -> {
                if (_cryptoWalletUsdt.value < amount) return false
                _cryptoWalletUsdt.value -= amount
            }
            "AIGH" -> {
                if (_cryptoWalletAigh.value < amount) return false
                _cryptoWalletAigh.value -= amount
            }
            else -> return false
        }
        _walletTransactions.value = _walletTransactions.value + "Sent -$amount $token to ${address.take(8)}..."
        return true
    }

    fun receiveCryptoSimulated(token: String, amount: Double) {
        when (token) {
            "MTC" -> _cryptoWalletMtc.value += amount
            "BTC" -> _cryptoWalletBtc.value += amount
            "ETH" -> _cryptoWalletEth.value += amount
            "USDT" -> _cryptoWalletUsdt.value += amount
            "AIGH" -> _cryptoWalletAigh.value += amount
        }
        _walletTransactions.value = _walletTransactions.value + "Received +$amount $token from External Scan"
    }

    fun payWithCrypto(amountInUsd: Double): Boolean {
        // Convert USD to MARKET Coin (1 MTC = 0.10 USD, so 1 USD = 10 MTC)
        val amountInMtc = amountInUsd * 10.0
        if (_cryptoWalletMtc.value >= amountInMtc) {
            _cryptoWalletMtc.value -= amountInMtc
            _walletTransactions.value = _walletTransactions.value + "Paid -$amountInMtc MTC for Cart Checkout"
            return true
        }
        return false
    }

    private var savedProductsJob: Job? = null

    private fun updateSavedProducts(userId: Int) {
        savedProductsJob?.cancel()
        savedProductsJob = viewModelScope.launch {
            savedProductDao.getSavedProductsForUser(userId).collect {
                _savedProducts.value = it
            }
        }
    }

    // Saving and Bookmarking Products
    fun toggleSaveProduct(productId: Int) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val isAlreadySaved = _savedProducts.value.any { it.id == productId }
            if (isAlreadySaved) {
                savedProductDao.deleteSavedProduct(user.id, productId)
            } else {
                savedProductDao.insertSavedProduct(SavedProductEntity(userId = user.id, productId = productId))
            }
        }
    }

    // Placing customer orders
    fun placeOrder(product: ProductEntity, quantity: Int, orderType: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val pricePerUnit = if (orderType == "Wholesale") product.wholesalePrice else product.retailPrice
            val total = pricePerUnit * quantity
            val newOrder = OrderEntity(
                customerId = user.id,
                customerName = user.name,
                productId = product.id,
                productName = product.name,
                quantity = quantity,
                totalPrice = total,
                orderType = orderType,
                merchantName = product.merchantName,
                status = "Pending",
                routeAddress = "Avenue 4, Block 12, Area " + (1..9).random() // random location
            )
            orderDao.insertOrder(newOrder)
            clearAiResponse()
        }
    }

    fun updateOrderStatus(orderId: Int, status: String) {
        viewModelScope.launch {
            orderDao.updateOrderStatus(orderId, status)
        }
    }

    fun assignDriverToOrder(orderId: Int, driverId: Int) {
        viewModelScope.launch {
            orderDao.assignOrderDriver(orderId, driverId, "Out For Delivery")
        }
    }

    fun addNewProduct(name: String, category: String, retailPrice: Double, wholesalePrice: Double, description: String, ingredients: String, isRegistered: Boolean, merchantName: String) {
        viewModelScope.launch {
            val newProd = ProductEntity(
                name = name,
                category = category,
                retailPrice = retailPrice,
                wholesalePrice = wholesalePrice,
                imageUrl = "https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&q=80&w=400",
                description = description,
                ingredients = ingredients,
                isRegisteredMerchant = isRegistered,
                merchantName = merchantName,
                stockQuantity = 100
            )
            productDao.insertProduct(newProd)
        }
    }

    fun clearAiResponse() {
        _aiResponse.value = null
    }

    // AI CALL CORES

    /**
     * Customer Feature 1.1: Visual Product Ordering
     */
    fun performAiVisualOrder(sampleProductName: String) {
        addToSearchHistory("Visual Search", sampleProductName, "AI image lookup analysis")
        executeAiTask(
            "Visual Product Ordering",
            """
            Analyze the following visual product photograph metadata search: '$sampleProductName'.
            Act as an E-commerce AI model that matches scraped social pages and websites with registered stores.
            Find nearest merchant options, prioritize registered merchants, give distance, and display retail vs carton/wholesale availability.
            Return an elegant report in Arabic & English.
            """.trimIndent()
        )
    }

    /**
     * Customer Feature 1.2: Price comparison & Deep Search
     */
    fun performAiPriceComparison(productName: String) {
        addToSearchHistory("Product Lookup", productName, "AI Global Market & Deep Search")
        executeAiTask(
            "AI Deep Web & Social Search",
            """
            Search and compare real-time prices for product: '$productName'.
            If the merchant or official distributor is not registered in our app, act as an advanced AI Agent.
            Search in nearby physical markets, competing apps, and scrape social media (Facebook, Instagram, TikTok) to find the absolute lowest price and best offers.
            Evaluate distance, shipping costs, and time to deliver. Recommend the most optimal place and price to buy it.
            Formatting output: Display a brief table structure or comparison list. Use Arabic & English.
            """.trimIndent()
        )
    }

    /**
     * Customer Feature 1.3: Product insights
     */
    fun performAiProductInsights(productName: String, ingredients: String) {
        executeAiTask(
            "Product Insights",
            """
            Analyze materials/ingredients: '$ingredients' of product: '$productName'.
            Report health benefits, potential risks, allergy warnings, nutritional suggestions, and uses based on official health resources.
            Arabic and English. Keep it structured.
            """.trimIndent()
        )
    }

    /**
     * Customer Feature 1.5: Smart Budget Shopping plan
     */
    fun performAiBudgetShoppingPlan(budget: Double, preferences: String) {
        executeAiTask(
            "Smart Budget Plan",
            """
            Budget: $budget USD.
            Preferences/items requested: ${if (preferences.isEmpty()) "Basic household groceries" else preferences}.
            Construct an optimized healthy smart buying strategy. Identify specific combinations of registered retail items and bulk carton options that minimize total expenditure to maximize fuel, shipping, and purchase savings.
            """.trimIndent()
        )
    }

    /**
     * Merchant Feature 2.1: Excel bulk upload simulated column auto-alignment and corrections
     */
    fun performMerchantExcelAutoMap(mockExcelColumns: String) {
        executeAiTask(
            "Excel Column Mapping & Auto-Correction",
            """
            The merchant uploaded a bulk spreadsheet with columns: [$mockExcelColumns].
            Auto-map these to standardized system schema properties: `title`, `price_retail`, `price_wholesale`, `category`, `stock`.
            Identify spelling errors, correct format mismatches, and explain how the AI mapped the headers automatically.
            """.trimIndent()
        )
    }

    /**
     * Merchant Feature 2.5: Generate daily promotion design campaign
     */
    fun generateMerchantDailyOfferAd(productName: String, merchantName: String) {
        executeAiTask(
            "Daily Content Ad Campaign Generator",
            """
            Product: '$productName'
            Merchant: '$merchantName'
            Act as a Creative Social Media AI Copywriter. Generate copy write-ups, daily discount taglines, and write a high-fidelity visual layout plan for a promotional banner overlay carrying the merchant logo.
            """.trimIndent()
        )
    }

    /**
     * Merchant Feature 2.6: AI Price Benchmarking against market and wholesale
     */
    fun performMerchantPriceBenchmarking(productName: String, retailPrice: Double, wholesalePrice: Double) {
        executeAiTask(
            "Price Benchmark Analysis",
            """
            Analyze merchant product: '$productName'.
            My Current Retail: $retailPrice | Wholesale: $wholesalePrice.
            Compare these rates with typical market standards, wholesale agencies, and competitors. Tell the merchant if they are overcharging or underpricing, and suggests margins to boost sales.
            """.trimIndent()
        )
    }

    /**
     * Delivery Feature 3.1: Route optimization
     */
    fun performDriverRouteOptimization(deliveryAddresses: String) {
        triggerGoogleMap(deliveryAddresses)
        executeAiTask(
            "Route & Fuel Optimization",
            """
            Addresses list: [$deliveryAddresses].
            Calculate the mathematically shortest route matrix to deliver all parcels sequentially based on active locations.
            Factor in weather patterns, fuel-saving throttle suggestions, and list order-by-order directions.
            """.trimIndent()
        )
    }

    /**
     * Admin Feature 4.2: Automated Dispute & Resolution Analyzer
     */
    fun performAdminDisputeResolution(orderId: Int, customerMsg: String, merchantMsg: String, reason: String) {
        executeAiTask(
            "Automated Dispute Resolution Propose",
            """
            Review Dispute in Order #$orderId (Reason: '$reason').
            Customer Statement: "$customerMsg"
            Merchant Statement: "$merchantMsg"
            Act as an automated platform arbitrator. Provide sentiment analysis of dialogue logs, declare fault percentages, suggest an exact fair settlement amount, and write instructions to resolve the conflict neutrally.
            """.trimIndent()
        )
    }

    /**
     * Admin Feature 4.4: Dynamic commission optimizer
     */
    /**
     * Builds the JSON snapshot imported by the web dashboard (dashboard/index.html).
     * Bank cards, cart items and search history are deliberately left out.
     */
    suspend fun buildDashboardExport(): String {
        val users = userDao.getAllUsers().first().map { u ->
            org.json.JSONObject()
                .put("id", u.id).put("name", u.name).put("phoneNumber", u.phoneNumber).put("email", u.email)
                .put("role", u.role).put("permissionGranted", u.permissionGranted)
                .put("loyaltyPoints", u.loyaltyPoints).put("createdAt", u.createdAt)
        }
        val products = productDao.getAllProducts().first().map { p ->
            org.json.JSONObject()
                .put("id", p.id).put("name", p.name).put("category", p.category)
                .put("retailPrice", p.retailPrice).put("wholesalePrice", p.wholesalePrice)
                .put("stockQuantity", p.stockQuantity).put("isRegisteredMerchant", p.isRegisteredMerchant)
                .put("merchantName", p.merchantName).put("salesHistory", p.salesHistory)
        }
        val orders = orderDao.getAllOrders().first().map { o ->
            org.json.JSONObject()
                .put("id", o.id).put("customerName", o.customerName).put("productName", o.productName)
                .put("quantity", o.quantity).put("totalPrice", o.totalPrice).put("orderType", o.orderType)
                .put("merchantName", o.merchantName).put("status", o.status).put("createdAt", o.createdAt)
        }
        val reports = chatReportDao.getAllChatReports().first().map { r ->
            org.json.JSONObject()
                .put("id", r.id).put("orderId", r.orderId).put("customerMessage", r.customerMessage)
                .put("merchantMessage", r.merchantMessage).put("disputeReason", r.disputeReason)
                .put("resolvedTip", r.resolvedTip)
        }
        return org.json.JSONObject()
            .put("format", "amer-store-export")
            .put("version", 1)
            .put("exportedAt", System.currentTimeMillis())
            .put("users", org.json.JSONArray(users))
            .put("products", org.json.JSONArray(products))
            .put("orders", org.json.JSONArray(orders))
            .put("chatReports", org.json.JSONArray(reports))
            .toString()
    }

    fun performAdminCommissionOptimization() {
        executeAiTask(
            "Commission Multi-Param Optimization",
            """
            Analyze market demand, active density, seasons, and peak rush loads.
            Propose optimized, dynamic platform commission schedules (e.g. standard product categories vs premium prompt delivery) to balance merchant retention and system profitability.
            """.trimIndent()
        )
    }

    private fun executeAiTask(title: String, prompt: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            _aiResponse.value = "Generating smart AI outcomes for '$title' using Gemini..."
            try {
                val response = ModelService.generateAiContent(prompt)
                _aiResponse.value = response
            } catch (e: Exception) {
                _aiResponse.value = "Failed to run AI operation: ${e.message}"
            } finally {
                _isAiLoading.value = false
            }
        }
    }

    fun reorder(order: OrderEntity) {
        viewModelScope.launch {
            val product = productDao.getProductByIdSuspend(order.productId)
            if (product != null) {
                addProductToCart(product, order.quantity, order.orderType)
            }
        }
    }

    fun checkAndAddBadges(user: UserEntity) {
        viewModelScope.launch {
            var newBadges = user.badges.split(",").filter { it.isNotEmpty() }.toMutableSet()
            
            // Frequent Shopper
            val orderCount = orderDao.getOrdersForCustomer(user.id).first().size
            if (orderCount >= 5 && "Frequent Shopper" !in newBadges) {
                newBadges.add("Frequent Shopper")
            }
            
            if (newBadges.joinToString(",") != user.badges) {
                val updatedUser = user.copy(badges = newBadges.joinToString(","))
                userDao.updateUser(updatedUser)
                _currentUser.value = updatedUser
            }
        }
    }

    fun addProductToCart(product: ProductEntity, quantity: Int, orderType: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val existing = cartItemDao.getCartItemSpecific(user.id, product.id, orderType)
            if (existing != null) {
                existing.quantity += quantity
                cartItemDao.updateCartItem(existing)
            } else {
                val price = if (orderType == "Wholesale") product.wholesalePrice else product.retailPrice
                cartItemDao.insertCartItem(
                    CartItemEntity(
                        userId = user.id,
                        productId = product.id,
                        productName = product.name,
                        imageUrl = product.imageUrl,
                        price = price,
                        quantity = quantity,
                        orderType = orderType
                    )
                )
            }
        }
    }

    fun deleteCartItem(itemId: Int) {
        viewModelScope.launch {
            cartItemDao.deleteCartItemById(itemId)
        }
    }

    fun clearCart() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            cartItemDao.clearCart(user.id)
        }
    }

    fun checkoutCart(selectedCardNumber: String? = null) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val items = cartItems.value
            if (items.isEmpty()) return@launch
            
            var totalSpent = 0.0
            for (item in items) {
                totalSpent += item.price * item.quantity
                val newOrder = OrderEntity(
                    customerId = user.id,
                    customerName = user.name,
                    productId = item.productId,
                    productName = item.productName,
                    quantity = item.quantity,
                    totalPrice = item.price * item.quantity,
                    orderType = item.orderType,
                    merchantName = "AI Smart Marketplace",
                    status = "Pending",
                    routeAddress = "Avenue " + (1..10).random() + ", Block " + (1..20).random() + ", Area " + (1..9).random()
                )
                orderDao.insertOrder(newOrder)

                // Track and store checked-out items for expiry tracking and carbon calculation
                val nameLower = item.productName.lowercase()
                val isOrganic = nameLower.contains("organic") || nameLower.contains("green") || nameLower.contains("fresh") || nameLower.contains("natural") || nameLower.contains("strawberry") || nameLower.contains("milk") || nameLower.contains("honey")
                if (isOrganic) {
                    val shelfLifeDays = when {
                        nameLower.contains("milk") || nameLower.contains("dairy") -> 7
                        nameLower.contains("strawberry") || nameLower.contains("fruit") || nameLower.contains("berry") -> 5
                        nameLower.contains("honey") || nameLower.contains("pantry") -> 60
                        else -> 10
                    }
                    val expiryTime = System.currentTimeMillis() + (shelfLifeDays * 24L * 60L * 60L * 1000L)
                    val storedItem = StoredOrganicItemEntity(
                        userId = user.id,
                        productName = item.productName,
                        purchaseDate = System.currentTimeMillis(),
                        expiryDate = expiryTime,
                        quantity = item.quantity,
                        isNotified = false
                    )
                    storedOrganicItemDao.insertStoredItem(storedItem)
                }
            }
            
            val pointsToAdd = totalSpent.toInt()
            val updatedUser = user.copy(loyaltyPoints = user.loyaltyPoints + pointsToAdd)
            userDao.updateUser(updatedUser)
            _currentUser.value = updatedUser
            
            cartItemDao.clearCart(user.id)
            
            if (selectedCardNumber != null) {
                _aiResponse.value = "🛒 Checkout succeeded using linked Card ****${selectedCardNumber.takeLast(4)}! Orders placed in live tracking ledger."
            } else {
                _aiResponse.value = "🛒 Checkout succeeded via Cash on Delivery! Orders placed in live tracking ledger."
            }
        }
    }

    fun linkBankCard(cardNumber: String, cardHolder: String, expiryDate: String, cvv: String, isPrimary: Boolean = false) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val card = BankCardEntity(
                cardNumber = cardNumber,
                userId = user.id,
                cardHolder = cardHolder,
                expiryDate = expiryDate,
                cvv = cvv,
                isPrimary = isPrimary
            )
            bankCardDao.insertBankCard(card)
        }
    }

    fun addManualStoredOrganicItem(productName: String, expiryDays: Int) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val expiryTime = System.currentTimeMillis() + (expiryDays * 24L * 60L * 60L * 1000L)
            val newItem = StoredOrganicItemEntity(
                userId = user.id,
                productName = productName,
                purchaseDate = System.currentTimeMillis(),
                expiryDate = expiryTime,
                quantity = 1,
                isNotified = false
            )
            storedOrganicItemDao.insertStoredItem(newItem)
        }
    }

    fun deleteStoredItem(id: Int) {
        viewModelScope.launch {
            storedOrganicItemDao.deleteStoredItem(id)
        }
    }

    fun clearAllStoredItems() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            storedOrganicItemDao.clearStoredItems(user.id)
        }
    }

    fun checkExpiringOrganicItems(context: android.content.Context) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            storedOrganicItemDao.getStoredItems(user.id).firstOrNull()?.forEach { item ->
                if (!item.isNotified) {
                    val timeLeftMs = item.expiryDate - System.currentTimeMillis()
                    val twoDaysMs = 2L * 24 * 60 * 60 * 1000
                    if (timeLeftMs <= twoDaysMs) {
                        val daysLeft = (timeLeftMs / (24 * 60 * 60 * 1000)).toInt()
                        val title = "Organic Item Expiring Soon! ⏰"
                        val message = if (daysLeft > 0) {
                            "Your stored '${item.productName}' expires in $daysLeft days. Consume it fresh!"
                        } else {
                            "Your stored '${item.productName}' has reached its expiry date today!"
                        }
                        
                        sendLocalPushNotification(context, title, message)
                        storedOrganicItemDao.markNotified(item.id)
                    }
                }
            }
        }
    }

    private fun sendLocalPushNotification(context: android.content.Context, title: String, message: String) {
        try {
            val channelId = "organic_expiry_alerts"
            val notificationManager = context.getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val channel = android.app.NotificationChannel(
                    channelId,
                    "Organic Freshness Alerts",
                    android.app.NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Alerts you when your stored organic products are nearing expiry."
                }
                notificationManager.createNotificationChannel(channel)
            }
            
            val builder = androidx.core.app.NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                
            notificationManager.notify((System.currentTimeMillis() % 100000).toInt(), builder.build())
        } catch (e: Exception) {
            Log.e("MarketViewModel", "Failed to send local push notification", e)
        }
    }

    fun updateCartItemQuantity(item: CartItemEntity, increment: Boolean) {
        viewModelScope.launch {
            val newQty = if (increment) item.quantity + 1 else item.quantity - 1
            if (newQty <= 0) {
                cartItemDao.deleteCartItemById(item.id)
            } else {
                // Copy instead of mutating: the item belongs to the current cartItems list, and mutating it
                // makes Room's next emission compare equal so the UI never shows the new quantity.
                cartItemDao.updateCartItem(item.copy(quantity = newQty))
            }
        }
    }

    fun deleteBankCard(cardNumber: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            bankCardDao.deleteBankCard(cardNumber, user.id)
        }
    }

    // Wishlist (Session-based, local state)
    private val _wishlist = MutableStateFlow<List<TrendingProduct>>(emptyList())
    val wishlist = _wishlist.asStateFlow()

    fun toggleWishlist(tp: TrendingProduct) {
        val currentList = _wishlist.value
        if (currentList.any { it.id == tp.id }) {
            _wishlist.value = currentList.filterNot { it.id == tp.id }
        } else {
            _wishlist.value = currentList + tp
        }
    }

    // Search History State
    private val _searchHistory = MutableStateFlow<List<SearchHistoryEntry>>(emptyList())
    val searchHistory = _searchHistory.asStateFlow()

    val recentSearchQueries = searchQueryDao.getRecentSearchQueries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addToSearchHistory(type: String, query: String, details: String = "") {
        val newEntry = SearchHistoryEntry(type = type, query = query, details = details)
        val filtered = _searchHistory.value.filterNot { it.query.equals(query, ignoreCase = true) && it.type == type }
        _searchHistory.value = (listOf(newEntry) + filtered).take(15) // Limit to last 15 searches
        
        viewModelScope.launch(exceptionHandler) {
            searchQueryDao.insertSearchQuery(com.example.data.SearchQueryEntity(query = query))
        }
    }

    fun clearSearchHistory() {
        _searchHistory.value = emptyList()
        viewModelScope.launch(exceptionHandler) {
            searchQueryDao.clearAll()
        }
    }

    fun deleteSearchQuery(query: String) {
        viewModelScope.launch(exceptionHandler) {
            searchQueryDao.deleteQuery(query)
        }
    }

    // Currency Converter State
    private val _selectedCurrency = MutableStateFlow("JOD") // "JOD" is the primary default currency
    val selectedCurrency = _selectedCurrency.asStateFlow()

    val currencyRates = mapOf(
        "JOD" to 0.71,   // Jordanian Dinar (Primary)
        "USD" to 1.0,    // US Dollar
        "SAR" to 3.75,   // Saudi Riyal
        "AED" to 3.67,   // UAE Dirham
        "QAR" to 3.64,   // Qatari Riyal
        "KWD" to 0.31,   // Kuwaiti Dinar
        "BHD" to 0.38,   // Bahraini Dinar
        "OMR" to 0.39,   // Omani Rial
        "EGP" to 47.50,  // Egyptian Pound
        "JPY" to 158.0,  // Japanese Yen
        "CNY" to 7.25,   // Chinese Yuan
        "INR" to 83.5,   // Indian Rupee
        "ZAR" to 18.2,   // South African Rand
        "NGN" to 1480.0, // Nigerian Naira
        "EUR" to 0.92,   // Euro
        "GBP" to 0.79,   // British Pound
        "CHF" to 0.89,   // Swiss Franc
        "CAD" to 1.37,   // Canadian Dollar
        "BRL" to 5.45,   // Brazilian Real
        "ARS" to 905.0   // Argentine Peso
    )

    val currencySymbols = mapOf(
        "JOD" to "JOD",
        "USD" to "$",
        "SAR" to "SAR",
        "AED" to "AED",
        "QAR" to "QAR",
        "KWD" to "KWD",
        "BHD" to "BHD",
        "OMR" to "OMR",
        "EGP" to "EGP",
        "JPY" to "¥",
        "CNY" to "¥",
        "INR" to "₹",
        "ZAR" to "R",
        "NGN" to "₦",
        "EUR" to "€",
        "GBP" to "£",
        "CHF" to "CHF",
        "CAD" to "C$",
        "BRL" to "R$",
        "ARS" to "ARS$"
    )

    val currencySymbolsAr = mapOf(
        "JOD" to "د.أ",
        "USD" to "دولار",
        "SAR" to "ر.س",
        "AED" to "د.إ",
        "QAR" to "ر.ق",
        "KWD" to "د.ك",
        "BHD" to "د.ب",
        "OMR" to "ر.ع",
        "EGP" to "ج.م",
        "JPY" to "ين",
        "CNY" to "يوان",
        "INR" to "روبية",
        "ZAR" to "راند",
        "NGN" to "نايرا",
        "EUR" to "يورو",
        "GBP" to "جنيه",
        "CHF" to "فرنك",
        "CAD" to "د.كندي",
        "BRL" to "ريال برازيلي",
        "ARS" to "بيزو"
    )

    fun setCurrency(currency: String) {
        if (currencyRates.containsKey(currency)) {
            _selectedCurrency.value = currency
        }
    }

    fun formatPrice(priceUsd: Double, lang: String): String {
        val rate = currencyRates[_selectedCurrency.value] ?: 1.0
        val converted = priceUsd * rate
        val symbol = if (lang == "ar") {
            currencySymbolsAr[_selectedCurrency.value] ?: _selectedCurrency.value
        } else {
            currencySymbols[_selectedCurrency.value] ?: _selectedCurrency.value
        }
        return String.format(java.util.Locale.US, "%.2f %s", converted, symbol)
    }

    // --- NEW ADVANCED METHODS ---
    // Chatbot Logic
    fun sendMessageToChatbot(role: String, content: String) {
        val historyFlow = when (role) {
            "Customer" -> _customerChatHistory
            "Merchant" -> _merchantChatHistory
            "Delivery" -> _deliveryChatHistory
            else -> _adminChatHistory
        }
        val currentHistory = historyFlow.value
        val userMsg = ChatMessage(content = content, isUser = true)
        historyFlow.value = currentHistory + userMsg

        viewModelScope.launch {
            _isChatbotLoading.value = true
            try {
                val systemPrompt = when (role) {
                    "Customer" -> {
                        val cartList = cartItems.value
                        val organicItems = cartList.filter {
                            it.productName.lowercase().contains("organic") || it.productName.contains("عضوي")
                        }
                        val cartContext = if (organicItems.isNotEmpty()) {
                            "The user currently has these organic products in their shopping cart: " +
                            organicItems.joinToString { "${it.productName} (Qty: ${it.quantity})" } + 
                            ". Proactively provide real-time nutritional advice and healthy meal pairing suggestions or recipes using these organic items. Focus on organic eating benefits, health, and wellness."
                        } else {
                            "The user's cart has no organic items currently. Politely suggest they add some of our premium organic products like Organic Mountain Honey, Fresh Organic Milk, or Organic Premium Eggs to receive real-time nutritional analysis and meal pairings!"
                        }
                        "You are an intelligent, helpful AI Grocery Shopper and Personal Assistant named AI Store Assistant. Your goal is to help the customer search for products, find wholesale vs retail deals, organize their grocery list, and answer order/delivery questions. Always use a friendly, helpful, modern conversational tone in Arabic/English depending on input. $cartContext"
                    }
                    "Merchant" -> "You are a professional AI Store Operations Specialist and Pricing Coach. Your goal is to help merchants optimize their inventories, calculate profit margins, analyze sales velocities of slow/fast items, and draft creative advertising text prompts. Always use a professional, business-focused tone in Arabic/English depending on input."
                    "Delivery" -> "You are an AI Logistic Dispatcher and Route Optimization Copilot. Your goal is to help delivery drivers locate optimized route paths, avoid congested zones, calculate fuel consumption, and handle customer delivery disputes safely. Always use a clear, logistics-oriented tone."
                    else -> "You are the AI Platform Operations Chief. Your goal is to help system administrators analyze the behavioral performance of merchants and drivers, check system transaction metrics, inspect active Firestore records, and settle dispute resolution claims between merchants and customers. Always use a highly precise, administrative, analytical tone."
                }
                
                val contextBuilder = StringBuilder()
                contextBuilder.append("System Instruction: ").append(systemPrompt).append("\n\n")
                val lastMessages = currentHistory.takeLast(6)
                lastMessages.forEach { msg ->
                    val prefix = if (msg.isUser) "User" else "Assistant"
                    contextBuilder.append("$prefix: ").append(msg.content).append("\n")
                }
                contextBuilder.append("User: ").append(content).append("\nAssistant:")

                val model = if (_isHighThinkingEnabled.value) "gemini-3.1-pro-preview" else "gemini-3.5-flash"
                val response = ModelService.generateAiWithModel(
                    prompt = contextBuilder.toString(),
                    model = model,
                    thinkingLevel = if (_isHighThinkingEnabled.value) "high" else null
                )

                historyFlow.value = historyFlow.value + ChatMessage(content = response, isUser = false)
            } catch (e: Exception) {
                historyFlow.value = historyFlow.value + ChatMessage(content = "Error calling chatbot service: ${e.message}", isUser = false)
            } finally {
                _isChatbotLoading.value = false
            }
        }
    }

    // Voice Call Logic
    fun startGeminiVoiceCall() {
        _isVoiceCallActive.value = true
        _voiceCallStatus.value = "Calling Gemini Live..."
        _voiceTranscriptionResult.value = ""
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            _voiceCallStatus.value = "Connected"
        }
    }

    fun stopGeminiVoiceCall() {
        _isVoiceCallActive.value = false
        _voiceCallStatus.value = "Idle"
    }

    fun setHighThinkingEnabled(enabled: Boolean) {
        _isHighThinkingEnabled.value = enabled
    }

    fun sendVoiceCommandToGemini(audioText: String) {
        viewModelScope.launch {
            _voiceCallStatus.value = "Transcribing with Gemini 3.5 Flash..."
            kotlinx.coroutines.delay(1200)
            _voiceTranscriptionResult.value = audioText
            _voiceCallStatus.value = "Gemini Processing Order..."
            
            val systemInstruction = "You are a voice assistant that parses customer orders. Extract the grocery items they want to buy."
            try {
                val response = ModelService.generateAiWithModel(
                    prompt = audioText,
                    model = "gemini-3.5-flash",
                    systemInstruction = systemInstruction
                )
                
                _voiceCallStatus.value = "Adding matched items to Cart..."
                kotlinx.coroutines.delay(800)
                
                val textLower = audioText.lowercase()
                var addedCount = 0
                val allProd = products.value
                for (p in allProd) {
                    if (textLower.contains(p.name.lowercase()) || (p.category != null && textLower.contains(p.category.lowercase()))) {
                        addProductToCart(p, 1, "Retail")
                        addedCount++
                        if (addedCount >= 2) break
                    }
                }
                
                if (addedCount == 0 && allProd.isNotEmpty()) {
                    addProductToCart(allProd.first(), 1, "Retail")
                }
                
                _voiceCallStatus.value = "Order Processed Successfully!"
            } catch (e: Exception) {
                _voiceCallStatus.value = "Connected"
            }
        }
    }

    // AI Media Studio Logic
    fun generateVideoWithVeo(prompt: String, aspect: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            _generatedVideoUrl.value = null
            try {
                kotlinx.coroutines.delay(2000)
                _generatedVideoUrl.value = "veo_simulated_video"
            } catch (e: Exception) {
                triggerError("Veo Video generation failed: ${e.message}")
            } finally {
                _isAiLoading.value = false
            }
        }
    }

    fun generateImageWithBanana(prompt: String, resolution: String, ratio: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            _generatedImageUrl.value = null
            try {
                kotlinx.coroutines.delay(1500)
                _generatedImageUrl.value = "banana_simulated_image"
            } catch (e: Exception) {
                triggerError("Image generation failed: ${e.message}")
            } finally {
                _isAiLoading.value = false
            }
        }
    }

    fun publishCampaign(title: String, desc: String, mediaType: String) {
        _customPromoTitle.value = title
        _customPromoDesc.value = desc
        _customPromoMediaType.value = mediaType
    }

    fun clearCampaign() {
        _customPromoTitle.value = null
        _customPromoDesc.value = null
        _customPromoMediaType.value = null
    }

    // Dynamic Theme Mode Explicit Set
    fun setThemeMode(mode: String) {
        if (mode == "light") {
            _appDarkMode.value = false
        } else {
            _appDarkMode.value = true
        }
    }

    // Sale Notifications state
    private val _saleNotifications = MutableStateFlow<List<SaleNotification>>(emptyList())
    val saleNotifications = _saleNotifications.asStateFlow()

    fun markNotificationAsRead(id: String) {
        _saleNotifications.value = _saleNotifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    fun clearAllNotifications() {
        _saleNotifications.value = emptyList()
    }

    fun triggerSaleSimulation(context: android.content.Context) {
        viewModelScope.launch {
            val wishlistItems = _savedProducts.value
            val userId = _currentUser.value?.id ?: 0
            
            val ordersList = orders.firstOrNull() ?: emptyList()
            val myPastOrders = ordersList.filter { it.customerId == userId }
            val allProducts = products.firstOrNull() ?: emptyList()
            
            if (wishlistItems.isEmpty() && myPastOrders.isEmpty()) {
                if (allProducts.isNotEmpty()) {
                    val randomProducts = allProducts.shuffled().take(2)
                    randomProducts.forEach { prod ->
                        val oldP = prod.retailPrice
                        val discount = (10..30).random() / 100.0
                        val newP = (oldP * (1.0 - discount) * 100).toInt() / 100.0
                        val notif = SaleNotification(
                            productId = prod.id,
                            productName = prod.name,
                            oldPrice = oldP,
                            newPrice = newP,
                            isFromWishlist = false,
                            isFromPurchaseHistory = false
                        )
                        _saleNotifications.value = listOf(notif) + _saleNotifications.value
                        sendSystemNotification(context, prod.name, oldP, newP)
                    }
                }
                return@launch
            }
            
            wishlistItems.forEach { prod ->
                val oldP = prod.retailPrice
                val discount = (15..35).random() / 100.0
                val newP = (oldP * (1.0 - discount) * 100).toInt() / 100.0
                val notif = SaleNotification(
                    productId = prod.id,
                    productName = prod.name,
                    oldPrice = oldP,
                    newPrice = newP,
                    isFromWishlist = true,
                    isFromPurchaseHistory = false
                )
                _saleNotifications.value = listOf(notif) + _saleNotifications.value
                sendSystemNotification(context, "${prod.name} (Wishlist)", oldP, newP)
            }
            
            myPastOrders.forEach { order ->
                val matchingProduct = allProducts.find { it.id == order.productId } ?: return@forEach
                if (wishlistItems.any { it.id == matchingProduct.id }) return@forEach
                
                val oldP = matchingProduct.retailPrice
                val discount = (15..35).random() / 100.0
                val newP = (oldP * (1.0 - discount) * 100).toInt() / 100.0
                val notif = SaleNotification(
                    productId = matchingProduct.id,
                    productName = matchingProduct.name,
                    oldPrice = oldP,
                    newPrice = newP,
                    isFromWishlist = false,
                    isFromPurchaseHistory = true
                )
                _saleNotifications.value = listOf(notif) + _saleNotifications.value
                sendSystemNotification(context, "${matchingProduct.name} (Previously Purchased)", oldP, newP)
            }
        }
    }

    private fun sendSystemNotification(context: android.content.Context, productName: String, oldPrice: Double, newPrice: Double) {
        try {
            val channelId = "sale_alerts_channel"
            val notificationId = (1000..9999).random()
            val notificationManager = context.getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val channel = android.app.NotificationChannel(
                    channelId,
                    "Sale Alerts",
                    android.app.NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Alerts when saved or purchased items go on sale"
                }
                notificationManager.createNotificationChannel(channel)
            }
            
            val text = "Price drop! $productName is now $${newPrice} (was $${oldPrice})!"
            val textAr = "انخفاض في السعر! المنتج $productName أصبح بسعر $${newPrice} بدلاً من $${oldPrice}!"
            val displayMsg = if (_appLanguage.value == "ar") textAr else text
            
            val builder = androidx.core.app.NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(if (_appLanguage.value == "ar") "⚡ تنبيه تخفيضات ذكي" else "⚡ AI Store Sale Alert")
                .setContentText(displayMsg)
                .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                
            notificationManager.notify(notificationId, builder.build())
        } catch (e: Exception) {
            Log.e("MarketViewModel", "Failed to send native notification", e)
        }
    }

    // AI Shopping Recommendations
    private val _aiShoppingRecommendations = MutableStateFlow<List<AIProductRecommendation>>(emptyList())
    val aiShoppingRecommendations = _aiShoppingRecommendations.asStateFlow()

    private val _isAiRecommendingShopping = MutableStateFlow(false)
    val isAiRecommendingShopping = _isAiRecommendingShopping.asStateFlow()

    fun generateAiShoppingRecommendations() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            _isAiRecommendingShopping.value = true
            try {
                val ordersList = orders.firstOrNull() ?: emptyList()
                val myPastOrders = ordersList.filter { it.customerId == user.id }
                val pastProductsStr = myPastOrders.joinToString(", ") { "${it.productName} (Qty: ${it.quantity})" }
                
                val prompt = """
                    You are an AI-driven e-commerce product recommendation engine for an Organic Grocery Store.
                    The user has a purchase history containing these items: $pastProductsStr.
                    Please analyze this purchase history and suggest 3 relevant organic items they would love from our catalog:
                    - Organic Mountain Honey
                    - Supreme Turkish Coffee Blend
                    - Fresh Organic Milk - Farm Direct
                    - Organic Premium Farm Eggs Carton
                    - Rich Cocoa Almond Granola Bar
                    
                    Return the recommendations ONLY as a raw JSON array of objects. Do not wrap in markdown ```json or other formatting.
                    The objects must have exactly these keys:
                    - "id": a random integer from 100 to 999
                    - "name": recommended product name from the list above or custom relevant organic product name
                    - "category": the product's category
                    - "price": price value (e.g., 12.5)
                    - "reason": a short, personalized 1-sentence explanation of why we recommend it based on their specific past purchase history.
                    
                    Example output format:
                    [{"id": 105, "name": "Organic Mountain Honey", "category": "Organic Foods", "price": 14.99, "reason": "Since you purchased eggs and breakfast products, we suggest our mountain honey for a perfect organic breakfast!"}]
                """.trimIndent()
                
                val response = com.example.data.ModelService.generateAiContent(prompt).trim()
                Log.d("MarketViewModel", "AI recommendations raw response: $response")
                
                val parsedList = mutableListOf<AIProductRecommendation>()
                try {
                    val cleanResponse = response.replace("```json", "").replace("```", "").trim()
                    val jsonArray = org.json.JSONArray(cleanResponse)
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        parsedList.add(
                            AIProductRecommendation(
                                id = obj.optInt("id", (100..999).random()),
                                name = obj.optString("name"),
                                category = obj.optString("category"),
                                price = obj.optDouble("price", 9.99),
                                reason = obj.optString("reason"),
                                imageUrl = getProductPlaceholderImage(obj.optString("name"))
                            )
                        )
                    }
                } catch (jsonEx: Exception) {
                    Log.e("MarketViewModel", "Failed to parse JSON, using fallback generator", jsonEx)
                    parsedList.addAll(getFallbackShoppingRecommendations(myPastOrders))
                }
                _aiShoppingRecommendations.value = parsedList
            } catch (e: Exception) {
                Log.e("MarketViewModel", "Error in AI recommendation engine", e)
                _aiShoppingRecommendations.value = getFallbackShoppingRecommendations(emptyList())
            } finally {
                _isAiRecommendingShopping.value = false
            }
        }
    }

    private fun getProductPlaceholderImage(name: String): String {
        return when {
            name.contains("Honey") || name.contains("عسل") -> "https://images.unsplash.com/photo-1587049352846-4a222e784d38?auto=format&fit=crop&q=80&w=400"
            name.contains("Coffee") || name.contains("قهوة") -> "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?auto=format&fit=crop&q=80&w=400"
            name.contains("Milk") || name.contains("حليب") -> "https://images.unsplash.com/photo-1550583724-b2692b85b150?auto=format&fit=crop&q=80&w=400"
            name.contains("Eggs") || name.contains("بيض") -> "https://images.unsplash.com/photo-1516448620398-c5f44bf9f441?auto=format&fit=crop&q=80&w=400"
            else -> "https://images.unsplash.com/photo-1568254183919-78a4f43a2877?auto=format&fit=crop&q=80&w=400"
        }
    }

    private fun getFallbackShoppingRecommendations(pastOrders: List<OrderEntity>): List<AIProductRecommendation> {
        val list = mutableListOf<AIProductRecommendation>()
        if (pastOrders.isNotEmpty()) {
            list.add(
                AIProductRecommendation(
                    id = 301,
                    name = "Organic Mountain Honey (عسل جبلي طبيعي)",
                    category = "Organic Foods",
                    price = 15.50,
                    reason = "We recommend this as a pure, organic sweetener that perfectly complements your previous purchases!",
                    imageUrl = getProductPlaceholderImage("Honey")
                )
            )
            list.add(
                AIProductRecommendation(
                    id = 302,
                    name = "Supreme Turkish Coffee Blend (قهوة تركية فاخرة)",
                    category = "Spiced Coffee & Tea",
                    price = 8.90,
                    reason = "Based on your active order logs, we suggest our premium spiced coffee for your morning routine.",
                    imageUrl = getProductPlaceholderImage("Coffee")
                )
            )
        } else {
            list.add(
                AIProductRecommendation(
                    id = 301,
                    name = "Organic Mountain Honey (عسل جبلي طبيعي)",
                    category = "Organic Foods",
                    price = 15.50,
                    reason = "Highly trending! Pure organic honey harvested from high-altitude local natural apiaries.",
                    imageUrl = getProductPlaceholderImage("Honey")
                )
            )
            list.add(
                AIProductRecommendation(
                    id = 303,
                    name = "Fresh Organic Milk - Farm Direct (حليب مزارع طازج)",
                    category = "Dairy",
                    price = 4.25,
                    reason = "Fresh pasture-fed milk delivered direct from the farm within 6 hours of milking.",
                    imageUrl = getProductPlaceholderImage("Milk")
                )
            )
        }
        return list
    }

    // Real-Time Price Comparison State
    private val _detailedPriceComparisons = MutableStateFlow<Map<Int, List<PriceComparison>>>(emptyMap())
    val detailedPriceComparisons = _detailedPriceComparisons.asStateFlow()

    private val _isComparingDetailedPrice = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val isComparingDetailedPrice = _isComparingDetailedPrice.asStateFlow()

    fun fetchDetailedPriceComparison(productId: Int, productName: String) {
        viewModelScope.launch {
            _isComparingDetailedPrice.value = _isComparingDetailedPrice.value + (productId to true)
            try {
                val prompt = """
                    You are a real-time price comparison bot for e-commerce groceries.
                    We want to find prices for this product: "$productName".
                    Please compare prices across these online retailers:
                    1. Amazon Organic Shop
                    2. Whole Foods Market
                    3. iHerb Organic
                    4. Local Smart-Store
                    
                    Return ONLY a JSON array of objects. Do not include markdown or other texts.
                    Each object must have exactly these keys:
                    - "retailerName": name of the retailer
                    - "price": compared price in USD (should range close to standard market value)
                    - "shippingDays": shipping speed in days (e.g. 1, 2, 3)
                    - "isBestDeal": boolean (set true ONLY for the lowest price)
                    
                    Example:
                    [{"retailerName": "Amazon Organic Shop", "price": 12.99, "shippingDays": 2, "isBestDeal": false}]
                """.trimIndent()
                
                val response = com.example.data.ModelService.generateAiContent(prompt).trim()
                Log.d("MarketViewModel", "Price comparison raw response: $response")
                
                val compList = mutableListOf<PriceComparison>()
                try {
                    val cleanResponse = response.replace("```json", "").replace("```", "").trim()
                    val jsonArray = org.json.JSONArray(cleanResponse)
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        compList.add(
                            PriceComparison(
                                retailerName = obj.optString("retailerName"),
                                price = obj.optDouble("price", 10.0),
                                shippingDays = obj.optInt("shippingDays", 2),
                                isBestDeal = obj.optBoolean("isBestDeal", false)
                            )
                        )
                    }
                } catch (jsonEx: Exception) {
                    Log.e("MarketViewModel", "Failed to parse comparison JSON, using high-fidelity fallback", jsonEx)
                    compList.addAll(getFallbackPriceComparisons(productName))
                }
                _detailedPriceComparisons.value = _detailedPriceComparisons.value + (productId to compList)
            } catch (e: Exception) {
                Log.e("MarketViewModel", "Price comparison error", e)
                _detailedPriceComparisons.value = _detailedPriceComparisons.value + (productId to getFallbackPriceComparisons(productName))
            } finally {
                _isComparingDetailedPrice.value = _isComparingDetailedPrice.value + (productId to false)
            }
        }
    }

    private fun getFallbackPriceComparisons(productName: String): List<PriceComparison> {
        val baseP = when {
            productName.contains("Honey") || productName.contains("عسل") -> 15.50
            productName.contains("Coffee") || productName.contains("قهوة") -> 8.90
            productName.contains("Milk") || productName.contains("حليب") -> 4.25
            productName.contains("Eggs") || productName.contains("بيض") -> 6.50
            else -> 10.00
        }
        return listOf(
            PriceComparison("Amazon Organic Shop", (baseP * 1.05 * 100).toInt() / 100.0, 2, false),
            PriceComparison("Whole Foods Market", (baseP * 0.95 * 100).toInt() / 100.0, 1, true),
            PriceComparison("iHerb Organic", (baseP * 1.12 * 100).toInt() / 100.0, 3, false),
            PriceComparison("Local Smart-Store", (baseP * 1.00 * 100).toInt() / 100.0, 1, false)
        )
    }

    // --- FEATURE 1: RECEIPT SCANNING ---
    private val _isScanningReceipt = MutableStateFlow(false)
    val isScanningReceipt = _isScanningReceipt.asStateFlow()

    private val _scannedReceiptResult = MutableStateFlow<ReceiptScanResult?>(null)
    val scannedReceiptResult = _scannedReceiptResult.asStateFlow()

    fun scanReceiptImage(bitmap: android.graphics.Bitmap, presetName: String? = null) {
        viewModelScope.launch {
            _isScanningReceipt.value = true
            _scannedReceiptResult.value = null
            try {
                val response = if (presetName != null) {
                    presetName
                } else {
                    try {
                        ModelService.analyzeReceipt(bitmap)
                    } catch (e: Exception) {
                        "Organic Market"
                    }
                }
                
                kotlinx.coroutines.delay(2000) // Aesthetic visual progress
                
                val parsedResult = when {
                    response.contains("Honey") || response.contains("عسل") || response.contains("Organic Market") || response.contains("عسل جبلي") -> {
                        ReceiptScanResult(
                            merchantName = "Organic Green Market",
                            items = listOf(
                                ScannedReceiptItem("Organic Mountain Honey (عسل جبلي طبيعي)", 1, 14.50),
                                ScannedReceiptItem("Fresh Organic Milk - Farm Direct (حليب مزارع طازج)", 2, 4.20)
                            ),
                            totalAmount = 22.90
                        )
                    }
                    response.contains("Eggs") || response.contains("بيض") || response.contains("Healthy Breakfast") || response.contains("بيض بلدي") -> {
                        ReceiptScanResult(
                            merchantName = "Healthy Earth Co",
                            items = listOf(
                                ScannedReceiptItem("Organic Premium Farm Eggs Carton (30 Pcs)", 1, 9.99),
                                ScannedReceiptItem("Rich Cocoa Almond Granola Bar (ألواح الشوفان بالكاكاو)", 3, 2.50)
                            ),
                            totalAmount = 17.49
                        )
                    }
                    response.contains("Energy") || response.contains("طاقة") || response.contains("Energy Boost") || response.contains("مشروب الطاقة") -> {
                        ReceiptScanResult(
                            merchantName = "Hyper Market",
                            items = listOf(
                                ScannedReceiptItem("Supercharged Cognitive Energy Drink (مشروب الطاقة الذكي)", 4, 3.50)
                            ),
                            totalAmount = 14.00
                        )
                    }
                    else -> {
                        ReceiptScanResult(
                            merchantName = "Local Organic Grocery",
                            items = listOf(
                                ScannedReceiptItem("Organic Mountain Honey (عسل جبلي طبيعي)", 1, 14.50),
                                ScannedReceiptItem("Rich Cocoa Almond Granola Bar (ألواح الشوفان بالكاكاو)", 1, 2.50)
                            ),
                            totalAmount = 17.00
                        )
                    }
                }
                
                _scannedReceiptResult.value = parsedResult
                
                // Automatically add to purchase history (as OrderEntity with "Delivered" status)
                val user = _currentUser.value
                if (user != null) {
                    parsedResult.items.forEach { item ->
                        val allProd = productDao.getAllProducts().first()
                        val matchingProduct = allProd.find { it.name.lowercase().contains(item.productName.lowercase()) || item.productName.lowercase().contains(it.name.lowercase()) }
                        val productId = matchingProduct?.id ?: (allProd.firstOrNull()?.id ?: 1)
                        
                        val order = OrderEntity(
                            customerId = user.id,
                            customerName = user.name,
                            productId = productId,
                            productName = item.productName,
                            quantity = item.quantity,
                            totalPrice = item.price * item.quantity,
                            orderType = "Retail",
                            merchantName = parsedResult.merchantName,
                            status = "Delivered",
                            createdAt = System.currentTimeMillis()
                        )
                        orderDao.insertOrder(order)
                    }
                }
            } catch (e: Exception) {
                Log.e("ReceiptScan", "Error scanning receipt", e)
            } finally {
                _isScanningReceipt.value = false
            }
        }
    }

    fun clearReceiptScan() {
        _scannedReceiptResult.value = null
    }

    // --- FEATURE 2: ORGANIC MONTHLY SPENDING COMPARISON CHART DATA ---
    val organicSpendingComparison = combine(orders, products, _currentUser) { ordersList, productsList, user ->
        if (user == null) {
            SpendingComparison(0.0, 0.0, 0.0, false, emptyList())
        } else {
            val userOrders = ordersList.filter { it.customerId == user.id && it.status == "Delivered" }
            val now = System.currentTimeMillis()
            val oneMonthMs = 30L * 24L * 60L * 60L * 1000L
            val currentMonthStart = now - oneMonthMs
            val previousMonthStart = now - (2 * oneMonthMs)
            
            val currentMonthOrders = userOrders.filter { it.createdAt >= currentMonthStart }
            val previousMonthOrders = userOrders.filter { it.createdAt in previousMonthStart until currentMonthStart }
            
            fun isOrganic(name: String): Boolean {
                val lower = name.lowercase()
                return lower.contains("organic") || lower.contains("عضوي") || lower.contains("طبيعي") || lower.contains("fresh") || lower.contains("عسل") || lower.contains("حليب") || lower.contains("بيض")
            }
            
            val currentOrganicOrders = currentMonthOrders.filter { isOrganic(it.productName) }
            val previousOrganicOrders = previousMonthOrders.filter { isOrganic(it.productName) }
            
            var currentSpend = currentOrganicOrders.sumOf { it.totalPrice }
            var previousSpend = previousOrganicOrders.sumOf { it.totalPrice }
            
            // Add fallback historical mock data so it looks populated on first launch
            if (currentSpend == 0.0 && previousSpend == 0.0) {
                currentSpend = 48.50
                previousSpend = 35.00
            }
            
            val difference = if (previousSpend > 0) {
                ((currentSpend - previousSpend) / previousSpend) * 100.0
            } else if (currentSpend > 0) {
                100.0
            } else {
                0.0
            }
            
            val breakdown = if (currentOrganicOrders.isEmpty()) {
                listOf(
                    OrganicSpendBreakdown("Organic Mountain Honey (عسل جبلي طبيعي)", 29.00),
                    OrganicSpendBreakdown("Fresh Organic Milk - Farm Direct (حليب مزارع طازج)", 12.50),
                    OrganicSpendBreakdown("Organic Premium Farm Eggs Carton (30 Pcs)", 7.00)
                )
            } else {
                currentOrganicOrders.groupBy { it.productName }
                    .map { (name, list) -> OrganicSpendBreakdown(name, list.sumOf { it.totalPrice }) }
                    .sortedByDescending { it.amount }
            }
            
            SpendingComparison(
                currentMonthOrganicSpend = (currentSpend * 100).toInt() / 100.0,
                previousMonthOrganicSpend = (previousSpend * 100).toInt() / 100.0,
                differencePercentage = (difference * 10).toInt() / 10.0,
                hasIncrease = currentSpend > previousSpend,
                breakDownItems = breakdown
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SpendingComparison(48.50, 35.00, 38.6, true, emptyList()))

    // --- FEATURE 3: VOICE-TO-TEXT SEARCH & QUICK ADD ---
    private val _voiceSearchQuery = MutableStateFlow("")
    val voiceSearchQuery = _voiceSearchQuery.asStateFlow()

    fun updateVoiceSearchQuery(query: String) {
        _voiceSearchQuery.value = query
        addToSearchHistory("Product Lookup", query, "Parsed from Voice Command")
    }

    fun clearVoiceSearchQuery() {
        _voiceSearchQuery.value = ""
    }

    private val _isVoiceListening = MutableStateFlow(false)
    val isVoiceListening = _isVoiceListening.asStateFlow()

    private val _voiceCommandResultText = MutableStateFlow("")
    val voiceCommandResultText = _voiceCommandResultText.asStateFlow()

    private val _voiceParsedAction = MutableStateFlow<String?>(null) // "ADDED_TO_CART", "SEARCHED", "ERROR"
    val voiceParsedAction = _voiceParsedAction.asStateFlow()

    fun startVoiceListening() {
        _isVoiceListening.value = true
        _voiceCommandResultText.value = "Listening..."
        _voiceParsedAction.value = null
    }

    fun stopVoiceListeningAndProcess(spokenText: String) {
        _isVoiceListening.value = false
        _voiceCommandResultText.value = spokenText
        processVoiceNaturalLanguage(spokenText)
    }

    private fun processVoiceNaturalLanguage(command: String) {
        viewModelScope.launch {
            _voiceCommandResultText.value = "Processing Command with Gemini..."
            try {
                val systemInstruction = """
                    You are an intelligent natural language voice parser for our organic grocery shop.
                    Analyze what the customer said: "$command".
                    Determine their action: either 'ADD_TO_CART' or 'SEARCH'.
                    Identify the key product mentioned.
                    Match with available items: "Organic Mountain Honey", "Supreme Turkish Coffee Blend", "Fresh Organic Milk - Farm Direct", "Organic Premium Farm Eggs Carton", "Supercharged Cognitive Energy Drink", "Rich Cocoa Almond Granola Bar".
                    Respond in strict JSON format:
                    {
                      "action": "ADD_TO_CART" or "SEARCH",
                      "query": "exact matched product name or general keyword",
                      "quantity": integer
                    }
                """.trimIndent()
                
                val response = ModelService.generateAiWithModel(
                    prompt = command,
                    model = "gemini-3.5-flash",
                    systemInstruction = systemInstruction
                )
                
                var action = "SEARCH"
                var query = command
                var quantity = 1
                
                try {
                    val cleanResponse = response.replace("```json", "").replace("```", "").trim()
                    val json = org.json.JSONObject(cleanResponse)
                    action = json.optString("action", "SEARCH")
                    query = json.optString("query", command)
                    quantity = json.optInt("quantity", 1)
                } catch (e: Exception) {
                    val commandLower = command.lowercase()
                    if (commandLower.contains("add") || commandLower.contains("buy") || commandLower.contains("أريد شراء") || commandLower.contains("أضف")) {
                        action = "ADD_TO_CART"
                    }
                    query = when {
                        commandLower.contains("honey") || commandLower.contains("عسل") -> "Organic Mountain Honey (عسل جبلي طبيعي)"
                        commandLower.contains("coffee") || commandLower.contains("قهوة") -> "Supreme Turkish Coffee Blend (قهوة تركية فاخرة)"
                        commandLower.contains("milk") || commandLower.contains("حليب") -> "Fresh Organic Milk - Farm Direct (حليب مزارع طازج)"
                        commandLower.contains("eggs") || commandLower.contains("بيض") -> "Organic Premium Farm Eggs Carton (30 Pcs)"
                        commandLower.contains("energy") || commandLower.contains("طاقة") -> "Supercharged Cognitive Energy Drink (مشروب الطاقة الذكي)"
                        commandLower.contains("granola") || commandLower.contains("شوفان") -> "Rich Cocoa Almond Granola Bar (ألواح الشوفان بالكاكاو)"
                        else -> command
                    }
                }
                
                if (action == "ADD_TO_CART") {
                    val allProd = productDao.getAllProducts().first()
                    val matchingProduct = allProd.find { it.name.lowercase().contains(query.lowercase()) || query.lowercase().contains(it.name.lowercase()) }
                    if (matchingProduct != null) {
                        addProductToCart(matchingProduct, quantity, "Retail")
                        _voiceCommandResultText.value = "Added $quantity x ${matchingProduct.name} to your Cart!"
                        _voiceParsedAction.value = "ADDED_TO_CART"
                    } else {
                        updateVoiceSearchQuery(query)
                        _voiceCommandResultText.value = "Searching for: $query"
                        _voiceParsedAction.value = "SEARCHED"
                    }
                } else {
                    updateVoiceSearchQuery(query)
                    _voiceCommandResultText.value = "Searching for: $query"
                    _voiceParsedAction.value = "SEARCHED"
                }
            } catch (e: Exception) {
                Log.e("VoiceNLP", "Error parsing voice command", e)
                _voiceCommandResultText.value = "Sorry, couldn't process command. Searching for '$command' instead."
                updateVoiceSearchQuery(command)
                _voiceParsedAction.value = "SEARCHED"
            }
        }
    }

    // --- FEATURE 4: REAL-TIME FAMILY SHOPPING LIST ---
    val familyShoppingList = familyShoppingListDao.getAllFamilyItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addFamilyShoppingItem(productName: String, category: String, priority: String, quantity: Int = 1) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val item = FamilyShoppingListEntity(
                productName = productName,
                addedBy = user.name,
                category = category,
                priority = priority,
                quantity = quantity,
                isChecked = false
            )
            familyShoppingListDao.insertFamilyItem(item)
        }
    }

    fun toggleFamilyItemChecked(id: Int, isChecked: Boolean) {
        viewModelScope.launch {
            familyShoppingListDao.updateFamilyItemStatus(id, isChecked)
        }
    }

    fun deleteFamilyItem(id: Int) {
        viewModelScope.launch {
            familyShoppingListDao.deleteFamilyItem(id)
        }
    }

    fun clearFamilyShoppingList() {
        viewModelScope.launch {
            familyShoppingListDao.clearAllFamilyItems()
        }
    }
}

// ChatMessage definition
data class ChatMessage(
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

// Data structures for trending and camera search
data class TrendingProduct(
    val id: Int,
    val name: String,
    val category: String,
    val retailPrice: Double,
    val wholesalePrice: Double,
    val imageUrl: String,
    val description: String,
    val trendPlatform: String,
    val trendTag: String,
    val aiInsight: String
)

data class CameraSearchResult(
    val identifiedProduct: String,
    val confidence: Double,
    val rationale: String
)

data class SearchHistoryEntry(
    val type: String, // "Visual Search" or "Product Lookup"
    val query: String,
    val timestamp: Long = System.currentTimeMillis(),
    val details: String = ""
)

data class DirectMessage(
    val id: Int,
    val senderRole: String, // "Customer", "Merchant", "Delivery", "Admin"
    val senderName: String,
    val receiverRole: String, // "Customer", "Merchant", "Delivery", "Admin"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class DeliveryLocation(
    val id: String = java.util.UUID.randomUUID().toString(),
    val label: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val isPrimary: Boolean = false
)

data class SaleNotification(
    val id: String = java.util.UUID.randomUUID().toString(),
    val productId: Int,
    val productName: String,
    val oldPrice: Double,
    val newPrice: Double,
    val isFromWishlist: Boolean,
    val isFromPurchaseHistory: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    var isRead: Boolean = false
)

data class AIProductRecommendation(
    val id: Int,
    val name: String,
    val category: String,
    val price: Double,
    val reason: String,
    val imageUrl: String
)

data class PriceComparison(
    val retailerName: String,
    val price: Double,
    val shippingDays: Int,
    val isBestDeal: Boolean,
    val logoUrl: String = ""
)

// Data models for receipt scanner, budget comparisons, and voice commands
data class ReceiptScanResult(
    val items: List<ScannedReceiptItem>,
    val totalAmount: Double,
    val merchantName: String
)

data class ScannedReceiptItem(
    val productName: String,
    val quantity: Int,
    val price: Double
)

data class SpendingComparison(
    val currentMonthOrganicSpend: Double,
    val previousMonthOrganicSpend: Double,
    val differencePercentage: Double,
    val hasIncrease: Boolean,
    val breakDownItems: List<OrganicSpendBreakdown>
)

data class OrganicSpendBreakdown(
    val productName: String,
    val amount: Double
)

