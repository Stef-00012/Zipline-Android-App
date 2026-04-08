package com.stefdp.zipline.utils

fun camelCaseToHumanReadable(string: String): String {
    val regex = Regex("""(?<=[A-Z])(?=[A-Z][a-z])|(?<=[^A-Z])(?=[A-Z])|(?<=[A-Za-z])(?=[^A-Za-z])""")

    return string.replace(regex, " ")
        .replaceFirstChar { it.uppercase() }
}