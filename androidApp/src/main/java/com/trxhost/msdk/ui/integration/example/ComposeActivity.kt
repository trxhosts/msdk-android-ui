package com.trxhost.msdk.ui.integration.example

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paymentpage.msdk.core.domain.entities.payment.Payment
import kotlinx.serialization.json.Json
import com.trxhost.msdk.ui.TrxHostsActionType
import com.trxhost.msdk.ui.TrxHostsAdditionalField
import com.trxhost.msdk.ui.TrxHostsAdditionalFieldType
import com.trxhost.msdk.ui.TrxHostsPaymentInfo
import com.trxhost.msdk.ui.TrxHostsPaymentSDK
import com.trxhost.msdk.ui.TrxHostsRecipientInfo
import com.trxhost.msdk.ui.TrxHostsRecurrentData
import com.trxhost.msdk.ui.TrxHostsScreenDisplayMode
import com.trxhost.msdk.ui.integration.example.utils.CommonUtils
import com.trxhost.msdk.ui.integration.example.utils.SignatureGenerator
import com.trxhost.msdk.ui.paymentOptions

class ComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(text = stringResource(id = R.string.title_activity_compose))
                            }
                        )
                    },
                ) {
                    Content(it)
                    startPaymentPage()
                }
            }
        }
    }

    private fun startPaymentPage() {

        //1. Create TrxHostsPaymentInfo object
        val TrxHostsPaymentInfo = TrxHostsPaymentInfo(
            //required fields
            projectId = BuildConfig.PROJECT_ID, //Unique project Id
            paymentId = CommonUtils.getRandomPaymentId(),
            paymentAmount = 100, //1.00
            paymentCurrency = "USD",
            //optional fields
//            paymentDescription = "Test description",
//            customerId = "12",
//            regionCode = "",
//            token = "",
//            languageCode = "en",
//            receiptData = "",
//            hideSavedWallets = false,
//            forcePaymentMethod = "card",
//            TrxHostsThreeDSecureInfo = TrxHostsThreeDSecureInfo()
        )

        //2. Sign it
        TrxHostsPaymentInfo.signature = SignatureGenerator.generateSignature(
            paramsToSign = TrxHostsPaymentInfo.getParamsForSignature(),
            secret = BuildConfig.PROJECT_SECRET_KEY
        )

        //3. Configure SDK
        val paymentOptions = paymentOptions {
            //Required object for payment
            paymentInfo = TrxHostsPaymentInfo

            //Optional objects for payment
            //TrxHostsActionType.Sale by default
            actionType = TrxHostsActionType.Sale
            //GooglePay options
            isTestEnvironment = true
            merchantId = BuildConfig.GPAY_MERCHANT_ID
            merchantName = "Example Merchant Name"
            additionalFields {
                field {
                    TrxHostsAdditionalField(
                        TrxHostsAdditionalFieldType.CUSTOMER_EMAIL,
                        "mail@mail.com"
                    )
                }
                field {
                    TrxHostsAdditionalField(
                        TrxHostsAdditionalFieldType.CUSTOMER_FIRST_NAME,
                        "firstName"
                    )
                }
            }
            screenDisplayModes {
                mode(TrxHostsScreenDisplayMode.HIDE_SUCCESS_FINAL_SCREEN)
                mode(TrxHostsScreenDisplayMode.HIDE_DECLINE_FINAL_SCREEN)
            }
            recurrentData = TrxHostsRecurrentData()
            recipientInfo = TrxHostsRecipientInfo()

            //Parameter to enable hiding or displaying scanning cards feature
            hideScanningCards = false

            //Custom theme
            isDarkTheme = false
            //Any bitmap image
            logoImage = BitmapFactory.decodeResource(
                resources,
                R.drawable.example_logo
            )
        }

        //4. Create sdk object
        val sdk = TrxHostsPaymentSDK(
            context = applicationContext,
            paymentOptions = paymentOptions,
            mockModeType = TrxHostsPaymentSDK.TrxHostsMockModeType.SUCCESS
        )

        //5. Open it
        startActivityForResult.launch(sdk.intent)
    }

    //6. Handle result
    private val startActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result.data
            when (result.resultCode) {
                TrxHostsPaymentSDK.RESULT_SUCCESS -> {
                    val payment =
                        Json.decodeFromString<Payment?>(
                            data?.getStringExtra(
                                TrxHostsPaymentSDK.EXTRA_PAYMENT
                            ).toString()
                        )
                    when {
                        payment?.token != null -> {
                            Toast.makeText(
                                this,
                                "Tokenization was finished successfully. Your token is ${payment.token}",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.d(
                                "PaymentSDK",
                                "Tokenization was finished successfully. Your token is ${payment.token}"
                            )
                        }

                        else -> {
                            Toast.makeText(
                                this,
                                "Payment was finished successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.d("PaymentSDK", "Payment was finished successfully")
                        }
                    }

                }

                TrxHostsPaymentSDK.RESULT_CANCELLED -> {
                    Toast.makeText(this, "Payment was cancelled", Toast.LENGTH_SHORT).show()
                    Log.d("PaymentSDK", "Payment was cancelled")
                }

                TrxHostsPaymentSDK.RESULT_DECLINE -> {
                    Toast.makeText(this, "Payment was declined", Toast.LENGTH_SHORT).show()
                    Log.d("PaymentSDK", "Payment was declined")
                }

                TrxHostsPaymentSDK.RESULT_ERROR -> {
                    val errorCode = data?.getStringExtra(TrxHostsPaymentSDK.EXTRA_ERROR_CODE)
                    val message = data?.getStringExtra(TrxHostsPaymentSDK.EXTRA_ERROR_MESSAGE)
                    Toast.makeText(
                        this,
                        "Payment was interrupted. See logs",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(
                        "PaymentSDK",
                        "Payment was interrupted. Error code: $errorCode. Message: $message"
                    )
                }
            }
        }
}

@Composable
fun Content(contentPadding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        Text(text = stringResource(id = R.string.compose_integration_example_label))
    }
}