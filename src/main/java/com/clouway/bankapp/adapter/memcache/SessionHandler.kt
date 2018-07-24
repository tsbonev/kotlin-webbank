package com.clouway.bankapp.adapter.memcache

import com.clouway.bankapp.core.Session

/**
 * @author tsbonev@gmail.com
 */
interface SessionHandler {

    fun getSessionById(sessionId: String): Session
    fun saveSessionInCache(session: Session)
}