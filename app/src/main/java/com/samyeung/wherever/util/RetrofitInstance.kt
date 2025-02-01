package com.samyeung.wherever.util

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.samyeung.wherever.api.*
import com.samyeung.wherever.util.helper.LanguageManager
import com.samyeung.wherever.cst.APIMapping
import okhttp3.Cache
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit

class RetrofitInstance {
    companion object {
        private fun create(
            context: Context,
            auth: Boolean = false,
            canCache: Boolean = false,
            isMultipart: Boolean = false
        ): Retrofit {
            val dispatcher = Dispatcher()
            dispatcher.maxRequests = 1

            val cacheSize = 10 * 1024 * 1024
            val gson =
                GsonBuilder()
                    .setLenient()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create()
            val cache = Cache(context.cacheDir, cacheSize.toLong())
            val okhttp = OkHttpClient.Builder()
                .cache(cache)
                .connectTimeout(10, TimeUnit.SECONDS)
            if (auth) {
                okhttp.authenticator(OKHttpInterceptor.TokenAuthenticator(context))
            }
            okhttp.addInterceptor(OKHttpInterceptor(context, canCache, LanguageManager.findLanguageByKey(LanguageManager.getLanguageAPI(context))!!.apiLanguage))
            okhttp.dispatcher(dispatcher)
            return Retrofit.Builder()
                .baseUrl(APIMapping.BASE_URL)
                .addConverterFactory(GsonConverter.create(gson, isMultipart))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okhttp.build())
                .build()
        }

        fun createAuthService(context: Context, auth: Boolean = false): AuthService {
            return create(context, auth)
                .create(AuthService::class.java)
        }

        fun createUserService(context: Context, isMultipart: Boolean = false): UserService {
            return create(
                context,
                true,
                isMultipart = isMultipart
            ).create(UserService::class.java)
        }
        fun createEmailService(context: Context): EmailVerificationService {
            return create(
                context,
                true,
                isMultipart = false
            ).create(EmailVerificationService::class.java)
        }

        fun createTraceService(context: Context, isMultipart: Boolean = false): ImageTraceService {
            return create(
                context,
                true,
                isMultipart = isMultipart
            ).create(ImageTraceService::class.java)
        }

        fun createReactionService(context: Context): ReactionService {
            return create(
                context,
                true,
                isMultipart = false
            ).create(ReactionService::class.java)
        }

        fun createRequestService(context: Context): RequestService {
            return create(
                context,
                true,
                isMultipart = false
            ).create(RequestService::class.java)
        }

        fun createUserInboxService(context: Context): UserInboxService {
            return create(
                context,
                true,
                isMultipart = false
            ).create(UserInboxService::class.java)
        }

        fun createCommentService(context: Context): CommentService {
            return create(
                context,
                true,
                isMultipart = false
            ).create(CommentService::class.java)
        }

        fun createFriendService(context: Context): FriendService {
            return create(
                context,
                true,
                isMultipart = false
            ).create(FriendService::class.java)
        }
        fun createFavouriteService(context: Context): FavouriteService {
            return create(
                context,
                true,
                isMultipart = false
            ).create(FavouriteService::class.java)
        }
        fun createTagService(context: Context): TagService {
            return create(
                context,
                true,
                isMultipart = false
            ).create(TagService::class.java)
        }
        fun createHomeService(context: Context): HomeService {
            return create(
                context,
                true,
                isMultipart = false
            ).create(HomeService::class.java)
        }
        fun createAppVersionService(context: Context): AppVersionService {
            return create(
                context,
                true,
                isMultipart = false
            ).create(AppVersionService::class.java)
        }
        fun createReportService(context: Context): ReportService {
            return create(
                context,
                true,
                isMultipart = false
            ).create(ReportService::class.java)
        }
        fun createFeedbackService(context: Context): FeedbackService {
            return create(
                context,
                true,
                isMultipart = false
            ).create(FeedbackService::class.java)
        }
    }
}