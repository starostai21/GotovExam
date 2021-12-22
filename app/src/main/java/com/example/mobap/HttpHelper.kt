package com.example.mobap

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.net.ssl.HttpsURLConnection

object HttpHelper {

    private const val GET : String = "GET"
    private const val POST : String = "POST"

    private fun getCharSet(url: String): String{
        val obj = URL(url)
        var con: HttpURLConnection = if(url.startsWith("https:", true))
            obj.openConnection() as HttpsURLConnection
        else
            obj.openConnection() as HttpURLConnection

        con.requestMethod = "HEAD"
        val contentType = con.contentType
        return if(contentType.contains("Windows-1251", true))
            "Windows-1251"
        else
            "UTF-8"
    }
    fun requestPOST(
        url: String,
        postData: JSONObject? = null,
        headers: Map<String, String>?,
        callback: (result: String?, error: String, code: Int)->Unit
    ) {
        Thread( Runnable {
            var error = ""
            var result: String? = null
            var code: Int = -1
            try {
                val charset = getCharSet(url)

                val urlURL = URL(url)
                val conn: HttpURLConnection = if (url.startsWith("https:", true))
                    urlURL.openConnection() as HttpsURLConnection
                else
                    urlURL.openConnection() as HttpURLConnection

                // если задан тип контента application/json, то на выход пишу как есть
                var contentTypeJson = false
                if(headers!=null){
                    for((key, value) in headers){
                     //   if(key.lowercase()=="content-type" && value.startsWith("application/json"))
                            contentTypeJson = true
                        conn.setRequestProperty(key, value)
                    }
                }

                conn.readTimeout = 10000
                conn.connectTimeout = 10000
                conn.requestMethod = POST
                conn.doInput = true
                conn.doOutput = true
                val os: OutputStream = conn.outputStream

                if (postData != null) {
                    val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
                    var content = ""
                    content = if(contentTypeJson)
                        postData.toString()
                    else
                        encodeParams(postData)?:""
                    writer.write(content)
                    writer.flush()
                    writer.close()
                }

                os.close()
                code = conn.responseCode // To Check for 200
                if (code == HttpsURLConnection.HTTP_OK) {
                    val `in` = BufferedReader(InputStreamReader(conn.inputStream, charset))
                    val sb = StringBuffer("")
                    var line: String? = ""
                    while (`in`.readLine().also { line = it } != null) {
                        sb.append(line)
                        break
                    }
                    `in`.close()
                    result = sb.toString()
                }
                else {
                    error = "Response code ${code}"
                }
            }
            catch (e: Exception) {
                error = e.message.toString()
            }
            callback.invoke(result, error, code)
        }).start()
    }

    fun getImage(url: String, callback: (result: Bitmap?, error: String)->Unit){
        Thread( Runnable {
            var image: Bitmap? = null
            var error = ""
            try {
                val `in` = URL(url).openStream()
                image = BitmapFactory.decodeStream(`in`)
            }
            catch (e: Exception) {
                error = e.message.toString()
            }
            callback.invoke(image, error)
        }).start()
    }

    fun requestGET(
        r_url: String,
        headers: Map<String, String>?,
        callback: (result: String?, error: String, code: Int)->Unit
    ) {
        Thread( Runnable {
            var error = ""
            var result: String? = null
            var code: Int = -1
            try {
                val charset = getCharSet(r_url)

                val obj = URL(r_url)

                val con: HttpURLConnection = if(r_url.startsWith("https:", true))
                    obj.openConnection() as HttpsURLConnection
                else
                    obj.openConnection() as HttpURLConnection

                if(headers!=null){
                    for((key, value) in headers){
                        con.setRequestProperty(key, value)
                    }
                }

                con.requestMethod = GET
                code = con.responseCode

                result = if (code == HttpURLConnection.HTTP_OK) { // connection ok
                    val `in` =
                        BufferedReader(InputStreamReader(con.inputStream, charset))
                    var inputLine: String?
                    val response = StringBuffer()
                    while (`in`.readLine().also { inputLine = it } != null) {
                        response.append(inputLine)
                    }
                    `in`.close()
                    response.toString()
                } else {
                    null
                }
            }
            catch (e: Exception){
                error = e.message.toString()
            }

            callback.invoke(result, error, code)
        }).start()
    }

    @Throws(IOException::class)
    private fun encodeParams(params: JSONObject): String? {
        val result = StringBuilder()
        var first = true
        val itr = params.keys()
        while (itr.hasNext()) {
            val key = itr.next()
            val value = params[key]
            if (first) first = false else result.append("&")
            result.append(URLEncoder.encode(key, "UTF-8"))
            result.append("=")
            result.append(URLEncoder.encode(value.toString(), "UTF-8"))
        }
        return result.toString()
    }
}