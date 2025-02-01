package com.samyeung.wherever.api

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.samyeung.wherever.util.RetrofitInstance
import com.samyeung.wherever.util.helper.ImageUtil
import com.samyeung.wherever.cst.Setting
import com.samyeung.wherever.model.Trace
import com.samyeung.wherever.model.UserProfile
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

open class UserServiceAdapter(val context: Context) : APIController(context) {
    private val compositeDisposable = CompositeDisposable()
    private val userService by lazy {
        RetrofitInstance.createUserService(context)
    }
    private val userServiceMultipart by lazy {
        RetrofitInstance.createUserService(context, true)
    }

    fun findUserProfiles(q: String, page: Int, limit: Int = 10) {
        this.compositeDisposable.add(
            RetrofitInstance.createUserService(context).findProfiles(q, page, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onFindProfiles(result.result.data)
                }, { e ->
                    if (canRetry()) {
                        this.findUserProfiles(q, page, limit)
                    }
                    error(e)
                })
        )
    }

    open fun onFindProfiles(userProfiles: Array<UserProfile>) {
    }


    fun getTraces(page: Int, limit: Int = 10, userId: String = "me") {
        this.compositeDisposable.add(
            RetrofitInstance.createUserService(context).getTraces(userId, page, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onGetTraces(result.result.data)
                }, { e ->
                    error(e)
                })
        )
    }

    open fun onGetTraces(traces: Array<Trace>) {

    }

    fun getUserProfile(userId: String) {
        this.compositeDisposable.add(
            RetrofitInstance.createUserService(context).getProfile(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onGetProfile(result.result)
                }, { e ->
                    if (canRetry()) {
                        this.getUserProfile(userId)
                    }
                    error(e)
                })
        )
    }

    open fun onGetProfile(userProfile: UserProfile) {
    }

    fun updateDisplayName(displayName: String) {
        val newProfile = UserService.UserProfileBody(display_name = displayName)
        this.compositeDisposable.add(
            RetrofitInstance.createUserService(context).updateProfile(newProfile)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onUpdateDisplayName()
                }, { e ->
                    if (canRetry()) {
                        this.updateDisplayName(displayName)
                    }
                    error(e)
                })
        )
    }

    open fun onUpdateDisplayName() {

    }

    fun updateAboutMe(aboutMe: String) {
        val newProfile = UserService.UserProfileBody(about_me = aboutMe)
        this.compositeDisposable.add(
            RetrofitInstance.createUserService(context).updateProfile(newProfile)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onUpdateAboutMe()
                }, { e ->
                    if (canRetry()) {
                        this.updateDisplayName(aboutMe)
                    }
                    error(e)
                })
        )
    }

    open fun onUpdateAboutMe() {

    }

    fun updateIcon(imageUri: Uri) {
        val file = File(imageUri.path)
        Glide.with(context).asBitmap().load(file).apply(RequestOptions().override(Setting.IMAGE_ICON_RESIZE))
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val compressedFile = ImageUtil.compress(context, resource)

                    val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), compressedFile)
                    val src = MultipartBody.Part.createFormData("src", compressedFile.name, requestFile)
                    this@UserServiceAdapter.compositeDisposable.add(
                        RetrofitInstance.createUserService(context, true).updateIcon(src)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ result ->
                                onUpdateIcon(result.result)
                            }, { e ->
                                if (canRetry()) {
                                    this@UserServiceAdapter.updateIcon(imageUri)
                                }
                                error(e)
                            })
                    )
                }

            })

    }

    open fun onUpdateIcon(userProfile: UserProfile) {
    }

    fun updateProfileIcon(imageUri: Uri) {
        val file = File(imageUri.path)

        Glide.with(context).asBitmap().load(file).apply(RequestOptions().override(Setting.IMAGE_PROFILE_ICON_RESIZE))
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val compressedFile = ImageUtil.compress(context, resource)

                    val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), compressedFile)
                    val src = MultipartBody.Part.createFormData("src", compressedFile.name, requestFile)
                    this@UserServiceAdapter.compositeDisposable.add(
                        RetrofitInstance.createUserService(context, true).updateProfileIcon(src)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ result ->
                                onUpdateProfileIcon(result.result)
                            }, { e ->
                                if (canRetry()) {
                                    this@UserServiceAdapter.updateProfileIcon(imageUri)
                                }
                                error(e)
                            })
                    )
                }

            })
    }

    open fun onUpdateProfileIcon(userProfile: UserProfile) {
    }

    fun removeIcon() {
        this.compositeDisposable.add(
            RetrofitInstance.createUserService(context).removeIcon()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onRemoveIcon(result.result)
                }, { e ->
                    if (canRetry()) {
                        this.removeIcon()
                    }
                    error(e)
                })
        )
    }

    open fun onRemoveIcon(userProfile: UserProfile) {
    }

    fun removeProfileIcon() {
        this.compositeDisposable.add(
            RetrofitInstance.createUserService(context).removeProfileIcon()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onRemoveProfileIcon(result.result)
                }, { e ->
                    if (canRetry()) {
                        this.removeProfileIcon()
                    }
                    error(e)
                })
        )
    }

    open fun onRemoveProfileIcon(userProfile: UserProfile) {
    }


    fun onDestroy() {
        compositeDisposable.clear()
    }

}