package com.example.callscreenapp.google_iab

import android.app.Activity
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.callscreenapp.R
import com.example.callscreenapp.google_iab.enums.ErrorType
import com.example.callscreenapp.google_iab.enums.ProductType
import com.example.callscreenapp.google_iab.models.BillingResponse
import com.example.callscreenapp.google_iab.models.ProductInfo
import com.example.callscreenapp.google_iab.models.PurchaseInfo
import com.example.callscreenapp.utils.PRODUCT_ID

class BillingClientLifecycle private constructor(
    private val applicationContext: Context
) : BillingEventListener {

    val weekSubProductWithProductDetails = MutableLiveData<ProductInfo>()
    val yearSubProductWithProductDetails = MutableLiveData<ProductInfo>()
    val onetimeSubProductWithProductDetails = MutableLiveData<ProductInfo>()

    val productFetch = MutableLiveData<List<PurchaseInfo>>()
    val productPurchase = MutableLiveData<List<PurchaseInfo>>()

    private val billingConnector: BillingConnector =
        BillingConnector(applicationContext, applicationContext.getString(R.string.license_key))
            .setNonConsumableIds(nonConsumableIds)
            .setSubscriptionIds(subscriptionIds)
            .autoAcknowledge()
            .autoConsume()
            .enableLogging()
            .connect()

    init {
        billingConnector.setBillingEventListener(this)
    }

    fun isReadyBillingConnector(): Boolean{
        return billingConnector.isReady
    }
    fun connectBillingConnector(){
        if(isReadyBillingConnector()) return
        billingConnector.connect()
    }
    fun destroyBillingConnector(){
        billingConnector.release()
    }

    fun fetchSubPurchasedProducts(){
        connectBillingConnector()
        billingConnector.fetchSubPurchasedProducts()
    }

    fun purchase(activity : Activity, productId : String){
        connectBillingConnector()
        billingConnector.purchase(activity, productId)
    }

    fun subscribe(activity : Activity, productId : String){
        connectBillingConnector()
        billingConnector.subscribe(activity, productId)
    }

    fun unsubscribe(activity : Activity, productId : String){
        connectBillingConnector()
        billingConnector.unsubscribe(activity, productId)
    }

    override fun onProductsFetched(productDetails: List<ProductInfo>) {
        productDetails.forEach {
            when(it.product){
                PRODUCT_ID.SUB_WEEKLY.stringValue -> weekSubProductWithProductDetails.postValue(it)
                PRODUCT_ID.SUB_YEARLY.stringValue -> yearSubProductWithProductDetails.postValue(it)
                PRODUCT_ID.IAP_LIFE_TIME.stringValue -> onetimeSubProductWithProductDetails.postValue(it)
            }
        }
    }

    override fun onPurchasedProductsFetched(
        productType: ProductType,
        purchases: List<PurchaseInfo>
    ) {
        productFetch.postValue(purchases)
    }

    override fun onProductsPurchased(purchases: List<PurchaseInfo>) {
        productPurchase.postValue(purchases)
    }

    override fun onPurchaseAcknowledged(purchase: PurchaseInfo) {

    }

    override fun onPurchaseConsumed(purchase: PurchaseInfo) {

    }

    override fun onBillingError(billingConnector: BillingConnector, response: BillingResponse) {
        @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
        when (response.errorType) {
            ErrorType.CLIENT_NOT_READY -> {}
            ErrorType.CLIENT_DISCONNECTED -> {}
            ErrorType.PRODUCT_NOT_EXIST -> {}
            ErrorType.CONSUME_ERROR -> {}
            ErrorType.CONSUME_WARNING -> {}
            ErrorType.ACKNOWLEDGE_ERROR -> {}
            ErrorType.ACKNOWLEDGE_WARNING -> {}
            ErrorType.FETCH_PURCHASED_PRODUCTS_ERROR -> {}
            ErrorType.BILLING_ERROR -> {}
            ErrorType.USER_CANCELED -> {}
            ErrorType.SERVICE_UNAVAILABLE -> {}
            ErrorType.BILLING_UNAVAILABLE -> {}
            ErrorType.ITEM_UNAVAILABLE -> {}
            ErrorType.DEVELOPER_ERROR -> {}
            ErrorType.ERROR -> {}
            ErrorType.ITEM_ALREADY_OWNED -> {}
            ErrorType.ITEM_NOT_OWNED -> {}
        }
    }

    companion object {
        private const val TAG = "BillingLifecycle"
        private const val MAX_RETRY_ATTEMPT = 3

        private val consumableIds = listOf(PRODUCT_ID.IAP_LIFE_TIME.stringValue)
        private val nonConsumableIds = listOf(PRODUCT_ID.IAP_LIFE_TIME.stringValue)
        private val subscriptionIds = listOf(PRODUCT_ID.SUB_WEEKLY.stringValue, PRODUCT_ID.SUB_YEARLY.stringValue)

        @Volatile
        private var INSTANCE: BillingClientLifecycle? = null

        fun getInstance(applicationContext: Context): BillingClientLifecycle =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: BillingClientLifecycle(applicationContext).also { INSTANCE = it }
            }
    }
}