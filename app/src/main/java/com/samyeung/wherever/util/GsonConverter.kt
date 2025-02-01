package com.samyeung.wherever.util

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.samyeung.wherever.cst.APIMapping
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okio.Buffer
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.Serializable
import java.lang.reflect.Type
import java.nio.charset.Charset

val UTF_8 = Charset.forName("UTF-8")

class GsonStatus : Serializable {
    @SerializedName("time")
    val time:Long = -1
    @SerializedName("code")
    val code: Int? =  null
    @SerializedName("message")
    val message: String? = null

    @Transient
    private val result: String? = null
    val isRequestSuccess: Boolean
        get() = code == APIMapping.CODE_SUCCESS
}

class GsonConverter private constructor(private val gson: Gson?, private val isMultiPart: Boolean = false) :
    Converter.Factory() {

    init {
        if (gson == null) throw NullPointerException("gson == null")
    }


    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        val adapter = gson!!.getAdapter(TypeToken.get(type!!))
        return GsonResponseBodyConverter(gson, adapter as TypeAdapter<*>)


    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody>? {
        val adapter = gson!!.getAdapter(TypeToken.get(type!!))
        if (this.isMultiPart) {
            return null
        } else {
            return GsonJsonBodyConverter(gson, adapter as TypeAdapter<*>)
        }
    }

    companion object {

        @JvmOverloads
        fun create(gson: Gson = Gson(),isMultiPart: Boolean): GsonConverter {
            return GsonConverter(gson,isMultiPart)
        }
    }
}

class GsonResponseBodyConverter<T>(private val gson: Gson, private val adapter: TypeAdapter<T>) :
    Converter<ResponseBody, T> {

    @Throws(IOException::class)
    override fun convert(value: ResponseBody): T {
        val response = value.string()
        val httpStatus = gson.fromJson(response, GsonStatus::class.java)
        //LoginManager.setUpServerTime(httpStatus.time)
        if (!httpStatus.isRequestSuccess) {
            value.close()
            throw ErrorException(httpStatus.code!!, httpStatus.message.toString())
        }

        val contentType = value.contentType()
        val charset = if (contentType != null) contentType.charset(UTF_8) else UTF_8
        val inputStream = ByteArrayInputStream(response.toByteArray())
        val reader = InputStreamReader(inputStream, charset!!)
        val jsonReader = gson.newJsonReader(reader)
        try {
            return adapter.read(jsonReader)
        } finally {
            value.close()
        }
    }
}

private class GsonJsonBodyConverter<T>(private val gson: Gson, private val adapter: TypeAdapter<T>) :
    Converter<T, RequestBody> {

    @Throws(IOException::class)
    override fun convert(value: T): RequestBody {
        val buffer = Buffer()
        val writer = OutputStreamWriter(buffer.outputStream(), UTF_8)
        val jsonWriter = gson.newJsonWriter(writer)
        adapter.write(jsonWriter, value)
        jsonWriter.close()
        return RequestBody.create(MEDIA_TYPE, buffer.readByteString())
    }

    companion object {
        val MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8")
    }
}

data class ErrorException(val retcode: Int, val resmsg: String) : RuntimeException(resmsg) {
    override fun toString(): String {
        return "Code: ${retcode}, Message: ${resmsg}"
    }
}