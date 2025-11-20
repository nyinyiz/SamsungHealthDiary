/*
 * Copyright (C) 2024 Samsung Electronics Co., Ltd. All rights reserved
 */
package com.samsung.android.health.sdk.sample.healthdiary.utils

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.samsung.android.sdk.health.data.error.ResolvablePlatformException
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun resolveException(exception: Throwable, activity: Activity) {
    if ((exception is ResolvablePlatformException) && exception.hasResolution) {
        exception.resolve(activity)
    }
}

fun formatString(input: Float): String {
    return String.format(Locale.ENGLISH, "%.2f", input)
}

val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy (E)")
    .withZone(ZoneId.systemDefault())
