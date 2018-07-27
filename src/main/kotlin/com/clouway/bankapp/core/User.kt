package com.clouway.bankapp.core

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
data class User (val id: Long, val username: String, val password: String){
    fun toMap(): LinkedHashMap<String, Any>{
        return linkedMapOf(
                "id" to this.id,
                "username" to this.username,
                "password" to ""
        )
    }
}