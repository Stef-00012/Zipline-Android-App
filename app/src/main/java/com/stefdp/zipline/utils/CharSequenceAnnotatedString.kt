package com.stefdp.zipline.utils

import androidx.compose.ui.text.AnnotatedString

fun CharSequence.toAnnotatedString(): AnnotatedString {
    return this as? AnnotatedString ?: AnnotatedString(this.toString())
}