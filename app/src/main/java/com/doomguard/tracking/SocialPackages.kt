package com.doomguard.tracking

object SocialPackages {
    val labels: Map<String, String> = mapOf(
        "com.instagram.android" to "Instagram",
        "com.zhiliaoapp.musically" to "TikTok",
        "com.ss.android.ugc.trill" to "TikTok",
        "com.google.android.youtube" to "YouTube",
        "com.facebook.katana" to "Facebook",
        "com.facebook.orca" to "Messenger",
        "com.twitter.android" to "X/Twitter",
        "com.x.android" to "X",
        "com.snapchat.android" to "Snapchat",
    )

    fun isSocial(pkg: String): Boolean = pkg in labels || pkg.startsWith("com.instagram") || pkg.contains("tiktok", ignoreCase = true)
}
