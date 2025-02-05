package com.stripe.android.paymentsheet.utils

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.stripe.android.customersheet.CustomerAdapter
import com.stripe.android.customersheet.CustomerEphemeralKey
import com.stripe.android.customersheet.CustomerSheet
import com.stripe.android.customersheet.CustomerSheetResultCallback
import com.stripe.android.customersheet.ExperimentalCustomerSheetApi
import com.stripe.android.customersheet.rememberCustomerSheet

@OptIn(ExperimentalCustomerSheetApi::class)
internal class CustomerSheetTestFactory(
    private val integrationType: IntegrationType,
    private val customerSheetTestType: CustomerSheetTestType,
    private val configuration: CustomerSheet.Configuration,
    private val resultCallback: CustomerSheetResultCallback,
) {

    fun make(activity: ComponentActivity): CustomerSheet {
        return when (integrationType) {
            IntegrationType.Activity -> forActivity(activity)
            IntegrationType.Compose -> forCompose(activity)
        }.apply {
            configure(configuration = configuration)
        }
    }

    private fun forActivity(
        activity: ComponentActivity
    ): CustomerSheet {
        return CustomerSheet.create(
            activity = activity,
            customerAdapter = createCustomerAdapter(customerSheetTestType, activity),
            callback = resultCallback,
        )
    }

    private fun forCompose(
        activity: ComponentActivity
    ): CustomerSheet {
        lateinit var customerSheet: CustomerSheet

        activity.setContent {
            customerSheet = rememberCustomerSheet(
                customerAdapter = createCustomerAdapter(customerSheetTestType, activity),
                callback = resultCallback,
            )
        }

        return customerSheet
    }

    private fun createCustomerAdapter(
        customerSheetTestType: CustomerSheetTestType,
        activity: ComponentActivity
    ): CustomerAdapter {
        return CustomerAdapter.create(
            context = activity,
            customerEphemeralKeyProvider = {
                CustomerAdapter.Result.success(
                    CustomerEphemeralKey(
                        customerId = "cus_1",
                        ephemeralKey = "ek_test_123"
                    )
                )
            },
            setupIntentClientSecretProvider = when (customerSheetTestType) {
                CustomerSheetTestType.AttachToCustomer -> null
                CustomerSheetTestType.AttachToSetupIntent -> {
                    { CustomerAdapter.Result.success("seti_12345_secret_12345") }
                }
            }
        )
    }
}
