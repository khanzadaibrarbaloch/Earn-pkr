package com.khanzada.pkr_earn

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private var rewardedAd: RewardedAd? = null
    private var loading = false
    private var adUnitId = "ca-app-pub-9075427382575085/1354350740"

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this)
        setContentView(webView)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = false
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()
        webView.addJavascriptInterface(AdMobBridge(), "AndroidAdMob")
        webView.loadUrl("https://pkr-earn-s8ij0e.v2.appdeploy.ai/")

        MobileAds.initialize(this) { preloadRewarded() }
    }

    private fun preloadRewarded() {
        if (loading || rewardedAd != null) return
        loading = true
        RewardedAd.load(this, adUnitId, AdRequest.Builder().build(), object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
                loading = false
                rewardedAd = ad
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                loading = false
                rewardedAd = null
            }
        })
    }

    private fun resolveJs(success: Boolean) {
        val js = "window.__admobResolve && window.__admobResolve(" + if (success) "true" else "false" + ");"
        runOnUiThread { webView.evaluateJavascript(js, null) }
    }

    private inner class AdMobBridge {
        @JavascriptInterface
        fun showRewarded(requestedUnitId: String?) {
            runOnUiThread {
                if (!requestedUnitId.isNullOrBlank()) adUnitId = requestedUnitId
                val ad = rewardedAd
                if (ad == null) {
                    resolveJs(false)
                    preloadRewarded()
                    return@runOnUiThread
                }
                rewardedAd = null
                var earned = false
                ad.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        resolveJs(earned)
                        preloadRewarded()
                    }
                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        resolveJs(false)
                        preloadRewarded()
                    }
                }
                ad.show(this@MainActivity) {
                    earned = true
                }
            }
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }
}
