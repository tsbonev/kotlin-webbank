package com.clouway.bankapp.core

import java.util.*

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
interface SessionRepository {

    fun registerSession(session: Session)
    fun refreshSession(session: Session)
    fun terminateSession(sessionId: String)
    fun deleteSessionsExpiringBefore(date: Date)
    fun getSessionAvailableAt(sessionId: String, date: Date): Optional<Session>
    fun getActiveSessionsCount(): Int
}