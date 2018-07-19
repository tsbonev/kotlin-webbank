package com.clouway.bankapp.core

import java.sql.Timestamp
import java.util.*

interface SessionRepository {

    fun registerSession(session: Session)
    fun refreshSession(session: Session)
    fun terminateSession(sessionId: String)
    fun deleteSessionsExpiringAfter(timestamp: Timestamp)
    fun getSessionAvailableAt(sessionId: String, timestamp: Timestamp): Optional<Session>
    fun getActiveSessionsCount(): Int

}