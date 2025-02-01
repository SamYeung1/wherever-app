package com.samyeung.wherever.api

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.samyeung.wherever.util.RetrofitInstance
import com.samyeung.wherever.util.helper.ImageUtil
import com.samyeung.wherever.cst.Setting
import com.samyeung.wherever.model.ResponseResult
import com.samyeung.wherever.model.Trace
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import io.reactivex.FlowableEmitter
import java.lang.Exception


open class ImageTraceServiceAdapter(val context: Context) : APIController(context) {
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    private val compositeDisposable = CompositeDisposable()
    private val traceService by lazy {
        RetrofitInstance.createTraceService(context)
    }
    private val traceServiceMultipart by lazy {
        RetrofitInstance.createTraceService(context, true)
    }

    fun getTraces(
        longitude: Double,
        latitude: Double,
        distance: Int,
        startDate: Date,
        endDate: Date,
        userIds: Array<String> = arrayOf(),
        tags:Array<String> = arrayOf()
    ) {
        this.compositeDisposable.add(
            RetrofitInstance.createTraceService(context).getTraces(
                longitude,
                latitude,
                distance,
                simpleDateFormat.format(startDate),
                simpleDateFormat.format(endDate),
                if(userIds.isNotEmpty()) userIds.joinToString(",") else null,
                if(tags.isNotEmpty()) tags.joinToString(",") else null
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onGetTraces(result.result)
                }, { e ->
                    if (canRetry()) {
                        this.getTraces(
                            longitude,
                            latitude,
                            distance,
                            startDate,
                            endDate,
                            userIds,
                            tags
                        )
                    }
                    error(e)
                })
        )
    }

    open fun onGetTraces(traces: Array<Trace>) {

    }

    fun getTrace(id: String) {
        this.compositeDisposable.add(
            RetrofitInstance.createTraceService(context).getTrace(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onGetTrace(result.result)
                }, { e ->
                    error(e)
                })
        )
    }

    open fun onGetTrace(trace: Trace) {

    }

    fun updateTrace(traceId: String, updateImageTraceBody: ImageTraceService.UpdateTraceBody) {
        this.compositeDisposable.add(
            RetrofitInstance.createTraceService(context).updateTrace(traceId, updateImageTraceBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onUpdateTrace(result.result)
                }, { e ->
                    error(e)
                })
        )
    }

    open fun onUpdateTrace(trace: Trace) {

    }

    fun insertTrace(imageTraceBody: ImageTraceService.TraceBody, onUpdating: (Double) -> Unit) {
        val file = File(imageTraceBody.src.path)
        var trace:ResponseResult<Trace?>? = null
        Glide.with(context).asBitmap().load(file).apply(RequestOptions().override(Setting.IMAGE_RESIZE))
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    this@ImageTraceServiceAdapter.compositeDisposable.add(
                        Flowable.create<Double>({ emitter ->
                            try {
                                val compressedFile = ImageUtil.compress(context,resource)
                                val traces = createMultipartBody("src",compressedFile,emitter)

                                val title =
                                    RequestBody.create(MediaType.parse("multipart/form-data"), imageTraceBody.title)
                                val description =
                                    RequestBody.create(
                                        MediaType.parse("multipart/form-data"),
                                        imageTraceBody.description
                                    )
                                val tags =
                                    RequestBody.create(
                                        MediaType.parse("multipart/form-data"),
                                        imageTraceBody.tags.joinToString(",")
                                    )
                                val lat =
                                    RequestBody.create(
                                        MediaType.parse("multipart/form-data"),
                                        imageTraceBody.latitude.toString()
                                    )
                                val lng =
                                    RequestBody.create(
                                        MediaType.parse("multipart/form-data"),
                                        imageTraceBody.longitude.toString()
                                    )
                                trace = RetrofitInstance.createTraceService(context, true).insertTrace(
                                    traces,
                                    title,
                                    description,
                                    tags,
                                    lng,
                                    lat
                                ).blockingFirst()
                                emitter.onComplete()
                            } catch (e: Exception) {
                                emitter.tryOnError(e)
                            }
                        }, BackpressureStrategy.LATEST)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ result ->
                                onUpdating(result)
                            }, { e ->
                                error(e)
                                onInsertImageError()
                            },{ onInsertImage(trace!!.result)})
                    )
                }

            })
    }

    open fun onInsertImage(trace: Trace?) {

    }

    open fun onInsertImageError() {

    }

    fun deleteTrace(id: String) {
        this.compositeDisposable.add(
            RetrofitInstance.createTraceService(context).deleteTrace(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    onDeleteTrace(result.result)
                }, { e ->
                    error(e)
                })
        )
    }

    open fun onDeleteTrace(trace: Trace) {

    }

    private fun createMultipartBody(
        fieldName: String,
        file: File,
        emitter: FlowableEmitter<Double>
    ): Array<MultipartBody.Part> {
        return arrayOf(
            RequestBody.create(MediaType.parse("multipart/form-data"), file).let {
                return@let MultipartBody.Part.createFormData(
                    fieldName,
                    file.name,
                    createCountingRequestBody(file, emitter)
                )
            }
        )
    }

    private fun createCountingRequestBody(file: File, emitter: FlowableEmitter<Double>): RequestBody {
        val requestBody = createRequestBody(file)
        return CountingRequestBody(requestBody, object : CountingRequestBody.Listener {
            override fun onRequestProgress(bytesWritten: Long, contentLength: Long) {
                val progress = (1.0 * bytesWritten) / contentLength
                emitter.onNext(progress * 100)
            }
        })
    }

    // -- 2 --
    private fun createRequestBody(file: File): RequestBody {
        return RequestBody.create(MediaType.parse("multipart/form-data"), file)
    }

    fun onDestroy() {
        compositeDisposable.clear()
    }

}