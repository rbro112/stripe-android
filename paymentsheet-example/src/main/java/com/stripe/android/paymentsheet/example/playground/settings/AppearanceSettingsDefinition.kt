package com.stripe.android.paymentsheet.example.playground.settings

import com.stripe.android.customersheet.CustomerSheet
import com.stripe.android.customersheet.ExperimentalCustomerSheetApi
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.example.playground.PlaygroundState
import com.stripe.android.paymentsheet.example.playground.activity.AppearanceStore

internal object AppearanceSettingsDefinition : PlaygroundSettingDefinition<Unit> {
    override val defaultValue: Unit = Unit

    override fun configure(
        value: Unit,
        configurationBuilder: PaymentSheet.Configuration.Builder,
        playgroundState: PlaygroundState,
        configurationData: PlaygroundSettingDefinition.PaymentSheetConfigurationData
    ) {
        configurationBuilder.appearance(AppearanceStore.state)
    }

    @OptIn(ExperimentalCustomerSheetApi::class)
    override fun configure(
        value: Unit,
        configurationBuilder: CustomerSheet.Configuration.Builder,
        playgroundState: PlaygroundState,
        configurationData: PlaygroundSettingDefinition.CustomerSheetConfigurationData,
    ) {
        configurationBuilder.appearance(AppearanceStore.state)
    }
}
