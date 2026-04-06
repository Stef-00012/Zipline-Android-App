package com.stefdp.zipline.utils

val DecimalRegex = Regex("""^(\d)+\.?(\d?)+$""")
val NumberRegex = Regex("""^(\d)*$""")
val DomainRegex = Regex("""^https?://([a-z0-9-]+\.)+[a-z]{2,}$""")