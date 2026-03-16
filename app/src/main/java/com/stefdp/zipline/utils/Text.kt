package com.stefdp.zipline.utils

fun camelCaseToHumanReadable(s: String): String {
    val regex = Regex("""(?<=[A-Z])(?=[A-Z][a-z])|(?<=[^A-Z])(?=[A-Z])|(?<=[A-Za-z])(?=[^A-Za-z])""")

    return s.replace(regex, " ")
        .replaceFirstChar { it.uppercase() }
}