package com.hhkbdev.rental.web.rest.errors

import feign.Response
import feign.codec.ErrorDecoder
import feign.codec.StringDecoder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.configurationprocessor.json.JSONException
import org.springframework.boot.configurationprocessor.json.JSONObject
import java.io.IOException


class FeignClientExceptionErrorDecoder : ErrorDecoder {
    private val log: Logger = LoggerFactory.getLogger(javaClass)
    private val stringDecoder: StringDecoder = StringDecoder()

    override fun decode(methodKey: String, response: Response): FeignClientException {
        var message = "Null response body for feign client exception"

        if (response.body() != null) {
            try {
                val jsonObject: JSONObject = JSONObject(response.body().toString())
                message = jsonObject.getString("message")
                message = stringDecoder.decode(response, String::class.java).toString()
            } catch (e: IOException) {
                log.error("Error decoding response body into JSON", e)
            } catch (e: JSONException) {
                log.error("Error decoding response body into JSON", e)
            }
        }

        return FeignClientException(response.status(), message, response.headers())
    }
}