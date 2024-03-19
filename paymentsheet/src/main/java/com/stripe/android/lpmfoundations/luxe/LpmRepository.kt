package com.stripe.android.lpmfoundations.luxe

import android.content.res.Resources
import com.stripe.android.model.StripeIntent
import com.stripe.android.ui.core.elements.LpmSerializer
import com.stripe.android.ui.core.elements.SharedDataSpec
import java.io.InputStream
import javax.inject.Inject

/**
 * This class is responsible for loading the LPM UI Specification for all LPMs, and returning
 * a particular requested LPM.
 *
 * This is not injected as a singleton because when the activity is killed
 * the FormViewModel and SheetViewModel don't share the Dagger graph and the
 * repository is not a singleton.  Additionally every time you create a new
 * form view model a new repository is created and thus needs to be initialized.
 */
internal class LpmRepository(
    private val arguments: LpmRepositoryArguments,
) {
    @Inject
    constructor(resources: Resources) : this(
        arguments = LpmRepositoryArguments(resources),
    )

    fun getSharedDataSpecs(
        stripeIntent: StripeIntent,
        serverLpmSpecs: String?,
    ): Result {
        val expectedLpms = stripeIntent.paymentMethodTypes
        var failedToParseServerResponse = false

        val sharedDataSpecs: MutableList<SharedDataSpec> = mutableListOf()

        if (!serverLpmSpecs.isNullOrEmpty()) {
            val deserializationResult = LpmSerializer.deserializeList(serverLpmSpecs)
            failedToParseServerResponse = deserializationResult.isFailure
            sharedDataSpecs += deserializationResult.getOrElse { emptyList() }
        }

        // If the server does not return specs, or they are not parsed successfully
        // we will use the LPM on disk if found
        val sharedDataSpecTypes = sharedDataSpecs.map { it.type }.toSet()
        val lpmsNotParsedFromServerSpec = expectedLpms.filter { lpm ->
            lpm !in sharedDataSpecTypes
        }

        if (lpmsNotParsedFromServerSpec.isNotEmpty()) {
            sharedDataSpecs += readFromDisk().filter { it.type in lpmsNotParsedFromServerSpec }
        }

        return Result(
            sharedDataSpecs = sharedDataSpecs,
            failedToParseServerResponse = failedToParseServerResponse,
        )
    }

    private fun readFromDisk(): List<SharedDataSpec> {
        return parseLpms(arguments.resources.assets?.open("lpms.json"))
    }

    private fun parseLpms(inputStream: InputStream?): List<SharedDataSpec> {
        return getJsonStringFromInputStream(inputStream)?.let { string ->
            LpmSerializer.deserializeList(string).getOrElse { emptyList() }
        }.orEmpty()
    }

    private fun getJsonStringFromInputStream(inputStream: InputStream?) =
        inputStream?.bufferedReader().use { it?.readText() }

    data class LpmRepositoryArguments(
        val resources: Resources,
    )

    data class Result(
        val sharedDataSpecs: List<SharedDataSpec>,
        val failedToParseServerResponse: Boolean,
    )
}
