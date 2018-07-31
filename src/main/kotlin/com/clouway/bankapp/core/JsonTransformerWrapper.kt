package com.clouway.bankapp.core

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
interface JsonTransformerWrapper {

    fun toJson(any: Any): String
    fun <T> fromJson(string: String, typeOfT: Class<T>): T
}