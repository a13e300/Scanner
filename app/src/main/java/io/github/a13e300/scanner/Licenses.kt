package io.github.a13e300.scanner

object Licenses {
    private const val APACHE_2_0 = "Apache License 2.0"
    private const val MIT = "MIT License"
    private const val GPL_3_0 = "GNU General Public License v3.0"
    val ITEMS: List<LicenseItem> = listOf(
        LicenseItem(
            "Zxing - Zxing",
            "https://github.com/zxing/zxing",
            APACHE_2_0
        ),
        LicenseItem(
            "AndroidX - Google",
            "https://source.google.com",
            APACHE_2_0
        ),
        LicenseItem(
            "Kotlin - JetBrains",
            "https://github.com/JetBrains/kotlin",
            APACHE_2_0
        ),
        LicenseItem(
            "material-components-android - Google",
            "https://github.com/material-components/material-components-android",
            APACHE_2_0
        ),
        LicenseItem(
            "RikkaX - RikkaApps",
            "https://github.com/RikkaApps/RikkaX",
            MIT
        ),
        LicenseItem(
            "LibChecker - Absinthe",
            "https://github.com/LibChecker/LibChecker",
            APACHE_2_0
        ),
        LicenseItem(
            "LSPosed - LSPosed",
            "https://github.com/LSPosed/LSPosed",
            GPL_3_0
        ),
        LicenseItem(
            "AndroidImageCropper - CanHub",
            "https://github.com/CanHub/Android-Image-Cropper",
            APACHE_2_0
        ),
        LicenseItem(
            "zxing-android-embedded - journeyapps",
            "https://github.com/journeyapps/zxing-android-embedded",
            APACHE_2_0
        )
    )
    data class LicenseItem(val projectName: String, val projectLink: String?, val license: String)
}