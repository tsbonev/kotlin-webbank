package com.clouway.bankapp.core

import java.sql.Timestamp

data class Session(val userId: Int,
                   val sessionId: String,
                   val expiresOn: Timestamp? = null,
                   val isAuthenticated: Boolean = false)
