package com.clouway.bankapp.core

import java.util.*
import kotlin.collections.LinkedHashMap

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
data class Session(val userId: Long,
                   val sessionId: String,
                   val expiresOn: Date,
                   val isAuthenticated: Boolean = false){

    fun toMap(): LinkedHashMap<String, Any>{
        return linkedMapOf(
                "sessionId" to this.sessionId,
                "userId" to this.userId,
                "expiresOn" to this.expiresOn,
                "isAuthenticated" to this.isAuthenticated
        )
    }

}
