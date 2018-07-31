package com.clouway.bankapp.core

import java.time.LocalDateTime
import java.util.*

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
interface SessionRepository {

    fun registerSession(session: Session)
    fun refreshSession(session: Session)
    fun terminateSession(sessionId: String)
    fun deleteSessionsExpiringBefore(date: LocalDateTime)
    fun getSessionAvailableAt(sessionId: String, date: LocalDateTime): Optional<Session>
    fun getActiveSessionsCount(): Int
}