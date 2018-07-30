package com.clouway.bankapp.core.security

import com.clouway.bankapp.core.Session
import com.clouway.bankapp.core.SessionNotFoundException

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
interface SessionHandler {

    @Throws(SessionNotFoundException::class)
    fun getSessionById(sessionId: String) : Session

    fun saveSession(session: Session)

    fun terminateSession(sessionId: String)

}