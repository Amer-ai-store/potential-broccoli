package com.example.data

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

/**
 * Mirrors the store tables to Cloud Firestore for the live store dashboard (dashboard/index.html).
 * Stays off unless the FIREBASE_* keys are set in .env. Bank cards, cart items and search
 * history are never sent. Document fields match the dashboard's "amer-store-export" format.
 */
object FirestoreSync {
    private const val TAG = "FirestoreSync"
    private const val APP_NAME = "amer-live-store"
    private const val PLACEHOLDER_PREFIX = "MY_FIREBASE_"

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    @Volatile private var started = false

    fun isConfigured(): Boolean =
        listOf(BuildConfig.FIREBASE_API_KEY, BuildConfig.FIREBASE_APP_ID, BuildConfig.FIREBASE_PROJECT_ID)
            .all { it.isNotBlank() && !it.startsWith(PLACEHOLDER_PREFIX) }

    @Synchronized
    fun start(context: Context, db: AppDatabase) {
        if (started) return
        if (!isConfigured()) {
            Log.i(TAG, "Firebase keys are not set; live store sync is off.")
            return
        }
        val app = try {
            FirebaseApp.getApps(context).firstOrNull { it.name == APP_NAME }
                ?: FirebaseApp.initializeApp(
                    context.applicationContext,
                    FirebaseOptions.Builder()
                        .setApiKey(BuildConfig.FIREBASE_API_KEY)
                        .setApplicationId(BuildConfig.FIREBASE_APP_ID)
                        .setProjectId(BuildConfig.FIREBASE_PROJECT_ID)
                        .build(),
                    APP_NAME
                )
        } catch (e: Exception) {
            Log.e(TAG, "Firebase initialization failed; live store sync is off.", e)
            return
        }
        started = true
        val firestore = FirebaseFirestore.getInstance(app)
        val auth = FirebaseAuth.getInstance(app)
        // Firestore rules only accept writes from a signed-in app; anonymous auth is enough for that.
        if (auth.currentUser != null) {
            mirrorAll(firestore, db)
        } else {
            auth.signInAnonymously()
                .addOnSuccessListener { mirrorAll(firestore, db) }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Anonymous sign-in failed. Enable the Anonymous provider in Firebase Authentication.", e)
                    started = false
                }
        }
    }

    private fun mirrorAll(firestore: FirebaseFirestore, db: AppDatabase) {
        mirror(firestore, "users", db.userDao().getAllUsers()) { u ->
            u.id.toString() to mapOf(
                "id" to u.id, "name" to u.name, "phoneNumber" to u.phoneNumber, "email" to u.email,
                "role" to u.role, "permissionGranted" to u.permissionGranted,
                "loyaltyPoints" to u.loyaltyPoints, "createdAt" to u.createdAt
            )
        }
        mirror(firestore, "products", db.productDao().getAllProducts()) { p ->
            p.id.toString() to mapOf(
                "id" to p.id, "name" to p.name, "category" to p.category,
                "retailPrice" to p.retailPrice, "wholesalePrice" to p.wholesalePrice,
                "stockQuantity" to p.stockQuantity, "isRegisteredMerchant" to p.isRegisteredMerchant,
                "merchantName" to p.merchantName, "salesHistory" to p.salesHistory
            )
        }
        mirror(firestore, "orders", db.orderDao().getAllOrders()) { o ->
            o.id.toString() to mapOf(
                "id" to o.id, "customerName" to o.customerName, "productName" to o.productName,
                "quantity" to o.quantity, "totalPrice" to o.totalPrice, "orderType" to o.orderType,
                "merchantName" to o.merchantName, "status" to o.status, "createdAt" to o.createdAt
            )
        }
        mirror(firestore, "chat_reports", db.chatReportDao().getAllChatReports()) { r ->
            r.id.toString() to mapOf(
                "id" to r.id, "orderId" to r.orderId, "customerMessage" to r.customerMessage,
                "merchantMessage" to r.merchantMessage, "disputeReason" to r.disputeReason,
                "resolvedTip" to r.resolvedTip
            )
        }
    }

    /** Writes only documents that changed since the last push, and deletes rows removed while the app runs. */
    @OptIn(FlowPreview::class)
    private fun <T> mirror(
        firestore: FirebaseFirestore,
        collection: String,
        source: Flow<List<T>>,
        toDoc: (T) -> Pair<String, Map<String, Any>>
    ) {
        scope.launch {
            var pushed = emptyMap<String, Map<String, Any>>()
            source.debounce(1500).collect { rows ->
                val docs = rows.associate(toDoc)
                val writes: List<Pair<String, Map<String, Any>?>> =
                    docs.filter { (id, data) -> pushed[id] != data }.map { it.key to it.value } +
                        (pushed.keys - docs.keys).map { it to null }
                // Firestore batches hold at most 500 operations.
                writes.chunked(450).forEach { chunk ->
                    val batch = firestore.batch()
                    for ((id, data) in chunk) {
                        val ref = firestore.collection(collection).document(id)
                        if (data == null) batch.delete(ref) else batch.set(ref, data)
                    }
                    batch.commit().addOnFailureListener { e -> Log.e(TAG, "Sync of $collection failed", e) }
                }
                pushed = docs
            }
        }
    }
}
