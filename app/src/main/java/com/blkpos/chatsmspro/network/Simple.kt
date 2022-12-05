package com.blkpos.chatsmspro.network

import retrofit2.*
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class Simple<R>(private val call: Call<R>) {
    fun run(responseHandler: (R?, Throwable?) -> Unit) {
        // run in the same thread
        try {
            // call and handle response
            val response = call.execute()
            handleResponse(response, responseHandler)

        } catch (t: IOException) {
            responseHandler(null, t)
        }
    }

    fun process(responseHandler: (R?, Throwable?) -> Unit) {
        // define callback
        val callback = object : Callback<R> {

            override fun onFailure(call: Call<R>?, t: Throwable?) =
                responseHandler(null, t)

            override fun onResponse(call: Call<R>?, r: Response<R>?) =
                handleResponse(r, responseHandler)
        }

        // enqueue network call
        call.enqueue(callback)

    }

    fun cancel(){
        call.cancel()
    }

    private fun handleResponse(response: Response<R>?, handler: (R?, Throwable?) -> Unit) {
        if (response?.isSuccessful == true) {
            handler(response.body(), null)
        } else {
            if (response?.code() in 400..511)
                handler(null, HttpException(response))

            else handler(response?.body(), null)

        }
    }
}
class SimpleCallAdapter<R>(private val responseType: Type): CallAdapter<R, Any> {

    override fun responseType(): Type = responseType

    override fun adapt(call: Call<R>): Any = Simple(call)
}

class SimpleCallAdapterFactory private constructor() : CallAdapter.Factory() {

    override fun get(returnType: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): CallAdapter<*, *>? {
        return returnType?.let {
            return try {
                // get enclosing type
                val enclosingType = (it as ParameterizedType)

                // ensure enclosing type is 'Simple'
                if (enclosingType.rawType != Simple::class.java)
                    null
                else {
                    val type = enclosingType.actualTypeArguments[0]
                    SimpleCallAdapter<Any>(type)
                }
            } catch (ex: ClassCastException) {
                null
            }
        }
    }

    companion object {
        @JvmStatic
        fun create() = SimpleCallAdapterFactory()
    }

}