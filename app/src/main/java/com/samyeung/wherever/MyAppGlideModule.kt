package com.samyeung.wherever

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.load.model.GlideUrl
import okhttp3.OkHttpClient
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.samyeung.wherever.util.helper.palette.PaletteBitmap
import com.samyeung.wherever.util.helper.palette.PaletteBitmapTranscoder
import java.io.InputStream
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager


@GlideModule
class MyAppGlideModule : AppGlideModule() {
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        unsafeOkHttpClient().let {
            registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(it))
        }
        registry.register(Bitmap::class.java, PaletteBitmap::class.java,  PaletteBitmapTranscoder(context,glide.getBitmapPool()))
    }

    fun unsafeOkHttpClient(): OkHttpClient {
        val unsafeTrustManager = createUnsafeTrustManager()
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, arrayOf(unsafeTrustManager), null)
        return OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, unsafeTrustManager)
            .hostnameVerifier { hostName, sslSession -> true }
            .build()
    }

    fun createUnsafeTrustManager(): X509TrustManager {
        return object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun getAcceptedIssuers(): Array<out X509Certificate>? {
                return emptyArray()
            }
        }
    }
}