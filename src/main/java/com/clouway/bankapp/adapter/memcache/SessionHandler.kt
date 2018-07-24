package com.clouway.bankapp.adapter.memcache

import com.clouway.bankapp.core.Session
import com.clouway.bankapp.core.User

/**
 * @author tsbonev@gmail.com
 */
interface SessionHandler {

    fun getSessionById(sessionId: String): Pair<Session, User>?
    fun saveSessionInCache(session: Session, user: User)
    fun terminateSession(sessionId: String)
}