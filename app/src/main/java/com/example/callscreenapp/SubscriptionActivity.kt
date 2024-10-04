package com.example.callscreenapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintSet
import com.example.callscreenapp.databinding.ActivitySubBinding
import com.example.callscreenapp.firebase.displaySub
import com.example.callscreenapp.ui.activity.MainActivity
import com.example.callscreenapp.utils.Constants
import com.example.callscreenapp.utils.PRODUCT_ID
import com.example.callscreenapp.utils.PreferencesManager
import com.example.callscreenapp.utils.showStrikeThrough
import java.text.NumberFormat
import java.util.Currency
import java.util.Date


class SubscriptionActivity : BaseActivity() {
    private var binding: ActivitySubBinding? = null
    private val constraintSet = ConstraintSet()
    private var product_id = PRODUCT_ID.NONE
    private var isRestore: Boolean = false
    private var isBuying: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObserve()
        binding = ActivitySubBinding.inflate(layoutInflater)
        setContentView(binding?.getRoot())
        setStatusBarAndNavigationBar(R.color.setup_bg, R.color.setup_bg)

        binding?.viewContainer?.let { ly -> constraintSet.clone(ly)}

        binding?.btnYearly?.setOnClickListener {
            clickOps1()
        }

        binding?.btnWeekly?.setOnClickListener {
            clickOps2()
        }

        binding?.btnLifetime?.setOnClickListener {
            clickOps3()
        }

        binding?.btnTryTrial?.setOnClickListener{
            buySubAndIAP()
        }

        binding?.btnX?.setOnClickListener { turnOffSub() }
        binding?.btnRestore?.setOnClickListener {
            isRestore = true
            checkSub(false)
        }

        binding?.btnTerm?.setOnClickListener { // go to TermOfUseActivity
            val intent = Intent(
                this@SubscriptionActivity,
                TermsOfUseActivity::class.java
            )
            startActivity(intent)
        }
        binding?.btnPP?.setOnClickListener { // go to PrivacyPolicyActivity
            val intent = Intent(
                this@SubscriptionActivity,
                PrivacyPolicyActivity::class.java
            )
            startActivity(intent)
        }

        billingClientLifecycle.productPurchase.observe(this){
            val purchaseInfo = it.first()
            PreferencesManager.purchaseAndRestoreSuccess()
            if (purchaseInfo.product == PRODUCT_ID.IAP_LIFE_TIME.stringValue) {
                PreferencesManager.purchaseLifetime()
            }
            showAlertDialogPurchasedSuccess(Date())
        }
        displaySub.observe(this){
            displayUIFromS(it)
        }
        displaySub.value?.let { displayUIFromS(it) }
        binding?.tvSubYearPriceSale?.showStrikeThrough(true)
    }

    fun displayUIFromS(it: Int){
        if(it == 0){
            clickOps1()
            binding?.btnYearly?.visibility = View.VISIBLE
        }else{
            clickOps2()
            binding?.btnYearly?.visibility = View.INVISIBLE
        }
    }

    fun initObserve(){
        var sub_text_des: String = getString(R.string.sub_text_des)

        billingClientLifecycle.weekSubProductWithProductDetails.observe(this){
            val skuDetailWeekly = it.subscriptionOfferDetails[0].pricingPhases[0]
            val week_price = skuDetailWeekly?.formattedPrice
                ?: "$5.99"
            val year_sale_price: Double = week_price.getAmount().toDouble() * 52
            val price_currency_code = skuDetailWeekly?.priceCurrencyCode ?: "EN"

            val format: NumberFormat = NumberFormat.getCurrencyInstance()
            format.setMaximumFractionDigits(0)
            format.setCurrency(Currency.getInstance(price_currency_code))

            val year_sale_price_format = format.format(year_sale_price)

            binding?.tvSubYearPriceSale?.text = "$year_sale_price_format/year"
            binding?.tvSubWeekPrice?.text = "$week_price/week"
            sub_text_des = sub_text_des.replace("week_price", week_price)
            binding?.tvDes?.text = sub_text_des
        }

        billingClientLifecycle.yearSubProductWithProductDetails.observe(this){
            val year_price : String = it.subscriptionOfferDetails?.firstNotNullOf { detail ->
                detail.pricingPhases[0].formattedPrice?.let { formatPrice ->
                    if (formatPrice.lowercase() != "free") {
                        formatPrice
                    } else {
                        null//"$49.99"
                    }
                }
            }.toString()
            binding?.tvSubYearPrice?.text = "$year_price/year"
            sub_text_des = sub_text_des.replace("year_price", year_price)
            binding?.tvDes?.text = sub_text_des
        }

        billingClientLifecycle.onetimeSubProductWithProductDetails.observe(this) {
            binding?.tvSubLifetimePrice?.text =
                it.oneTimePurchaseOfferFormattedPrice ?: "$69.99"
        }
    }

    fun String.getAmount(): String {
        return substring(indexOfFirst { it.isDigit() }, indexOfLast { it.isDigit() } + 1)
            .filter { it.isDigit() || it == '.' }
    }
    private fun clickOps1(){
        product_id = PRODUCT_ID.SUB_YEARLY
        binding?.btnYearlyBg?.setImageResource(R.drawable.btn_sub_hover_bg)
        binding?.btnWeeklyBg?.setImageResource(R.drawable.btn_sub_normal_bg)
        binding?.btnLifetimeBg?.setImageResource(R.drawable.btn_sub_normal_bg)
        binding?.btnTryTrial?.setImageResource(R.drawable.btnstartfreetrial)
    }

    private fun clickOps2(){
        product_id = PRODUCT_ID.SUB_WEEKLY
        binding?.btnYearlyBg?.setImageResource(R.drawable.btn_sub_normal_bg)
        binding?.btnWeeklyBg?.setImageResource(R.drawable.btn_sub_hover_bg)
        binding?.btnLifetimeBg?.setImageResource(R.drawable.btn_sub_normal_bg)
        binding?.btnTryTrial?.setImageResource(R.drawable.btnsubscribenox)
    }

    private fun clickOps3(){
        product_id = PRODUCT_ID.IAP_LIFE_TIME
        binding?.btnYearlyBg?.setImageResource(R.drawable.btn_sub_normal_bg)
        binding?.btnWeeklyBg?.setImageResource(R.drawable.btn_sub_normal_bg)
        binding?.btnLifetimeBg?.setImageResource(R.drawable.btn_sub_hover_bg)
        binding?.btnTryTrial?.setImageResource(R.drawable.btnsubscribenox)
    }

    private fun buySubAndIAP(){
        Constants.productIdBuy = product_id.stringValue
        when (product_id) {
            PRODUCT_ID.IAP_LIFE_TIME -> {
                billingClientLifecycle.purchase(this@SubscriptionActivity, product_id.stringValue)
            }

            PRODUCT_ID.SUB_WEEKLY -> {
                billingClientLifecycle.subscribe(this@SubscriptionActivity, product_id.stringValue)
            }

            PRODUCT_ID.SUB_YEARLY -> {
                billingClientLifecycle.subscribe(this@SubscriptionActivity, product_id.stringValue)
            }

            else -> {}
        }
    }

    override fun checkSubSuccessfully(isLoadAds: Boolean) {
        if(!isRestore) return
        isRestore = false
        runOnUiThread {
            val alert_title = if(PreferencesManager.checkSUB().isNullOrEmpty())
                "Nothing to restore"
            else
                "All purchased is restored"
            val alert_message = if(PreferencesManager.checkSUB().isNullOrEmpty())
                "You have never made a payment before, nothing will be restored"
            else
                null
            AlertDialog.Builder(this@SubscriptionActivity)
                .setTitle(alert_title)
                .setMessage(alert_message)
                .setCancelable(false)
                .setPositiveButton(
                    "OK"
                ) { dialog, which ->
                    if(!PreferencesManager.checkSUB().isNullOrEmpty()){
                        turnOffSub()
                    }
                }
                .create()
                .show()
        }
    }

    private fun showAlertDialogPurchasedSuccess(purchaseTime: Date) {
        AlertDialog.Builder(this)
            .setTitle("Product is purchased")
            .setMessage("Product is valid until $purchaseTime")
            .setCancelable(false)
            .setPositiveButton(
                "OK"
            ) { dialog, which -> turnOffSub() }
            .create()
            .show()
    }

    private fun turnOffSub() {
        if(!PreferencesManager.isShowOnBoard()){
            PreferencesManager.saveShowOnBoard(true)
            // reload main activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

//            overridePendingTransition(R.anim.fade_out, R.anim.fade_in)
        }

        finish()
    }

    fun onProductPurchaseFail() {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage("Your order could not be processed. Please try again!")
            .setCancelable(false)
            .setPositiveButton(
                "OK"
            ) { dialog, which -> }
            .create()
            .show()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
//            takeFullScreen();
        }
    }

    // Toast message
    fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
