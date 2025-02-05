package com.stripe.android.paymentsheet.verticalmode

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.stripe.android.paymentsheet.DisplayableSavedPaymentMethod
import com.stripe.android.paymentsheet.ui.RemovePaymentMethodDialogUI
import com.stripe.android.paymentsheet.ui.readNumbersAsIndividualDigits
import com.stripe.android.uicore.strings.resolve

@Composable
internal fun DeleteIcon(
    paymentMethod: DisplayableSavedPaymentMethod,
    deletePaymentMethod: (DisplayableSavedPaymentMethod) -> Unit
) {
    val openRemoveDialog = rememberSaveable { mutableStateOf(false) }
    val paymentMethodId = paymentMethod.paymentMethod.id

    TrailingIcon(
        backgroundColor = Color.Red,
        icon = Icons.Filled.Close,
        modifier = Modifier.testTag("${TEST_TAG_MANAGE_SCREEN_DELETE_ICON}_$paymentMethodId"),
        onClick = {
            openRemoveDialog.value = true
        },
        contentDescription = paymentMethod
            .getRemoveDescription()
            .resolve()
            .readNumbersAsIndividualDigits()
    )

    if (openRemoveDialog.value) {
        RemovePaymentMethodDialogUI(paymentMethod = paymentMethod, onConfirmListener = {
            openRemoveDialog.value = false
            deletePaymentMethod(paymentMethod)
        }, onDismissListener = {
            openRemoveDialog.value = false
        })
    }
}

@Composable
internal fun EditIcon(
    paymentMethod: DisplayableSavedPaymentMethod,
    editPaymentMethod: (DisplayableSavedPaymentMethod) -> Unit,
) {
    val paymentMethodId = paymentMethod.paymentMethod.id

    TrailingIcon(
        backgroundColor = Color.Gray,
        icon = Icons.Filled.Edit,
        modifier = Modifier.testTag("${TEST_TAG_MANAGE_SCREEN_EDIT_ICON}_$paymentMethodId"),
        onClick = { editPaymentMethod(paymentMethod) },
        contentDescription = paymentMethod
            .getModifyDescription()
            .resolve()
            .readNumbersAsIndividualDigits(),
    )
}

@Composable
private fun TrailingIcon(
    backgroundColor: Color,
    icon: ImageVector,
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(CircleShape)
            .size(24.dp)
            .background(backgroundColor)
            .clickable(onClick = onClick),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(12.dp)
                .semantics {
                    this.contentDescription = contentDescription
                },
        )
    }
}

internal const val TEST_TAG_MANAGE_SCREEN_EDIT_ICON = "manage_screen_edit_icon"
internal const val TEST_TAG_MANAGE_SCREEN_DELETE_ICON = "manage_screen_delete_icon"
