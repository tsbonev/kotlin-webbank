package com.clouway.bankapp.adapter.spark

import com.google.appengine.repackaged.com.google.gson.GsonBuilder
import spark.ResponseTransformer

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class JsonTransformer : ResponseTransformer {

    private val gsonBuilder = GsonBuilder().setPrettyPrinting()

    private val gson = gsonBuilder.create()

    override fun render(model: Any): String {
        return gson.toJson(model)
    }

    fun <T> from(json: String?, clazz: Class<T>): T {
        return gson.fromJson<T>(json, clazz)
    }

}