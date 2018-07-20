package com.clouway.bankapp.core

import java.util.*

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
data class Session(val userId: Long,
                   val sessionId: String,
                   val expiresOn: Date,
                   val isAuthenticated: Boolean = false)
