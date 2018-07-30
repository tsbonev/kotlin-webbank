package com.clouway.bankapp.adapter.gae.memcache

import com.clouway.bankapp.core.Session
import com.clouway.bankapp.core.SessionNotFoundException
import com.clouway.bankapp.core.security.SessionHandler
import com.google.appengine.api.memcache.MemcacheService
import com.google.appengine.api.memcache.MemcacheServiceFactory
import java.util.*
import kotlin.collections.LinkedHashMap

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class MemcacheSessionHandler(private val origin: SessionHandler) : SessionHandler {

    private val service: MemcacheService
        get() = MemcacheServiceFactory.getMemcacheService()

    private fun mapToSession(map: LinkedHashMap<*, *>): Session {
        return Session(
                map["userId"] as Long,
                map["sessionId"] as String,
                map["expiresOn"] as Date,
                map["username"] as String,
                map["isAuthenticated"] as Boolean
        )
    }

    override fun getSessionById(sessionId: String): Session {
        val cachedSession = service.get(sessionId)

        return if (cachedSession is LinkedHashMap<*, *>) {
            mapToSession(cachedSession)
        } else {
            val persistentSession = origin.getSessionById(sessionId)
            saveSessionInCache(persistentSession)
            persistentSession
        }

    }

    override fun saveSession(session: Session) {
        service.put(session.sessionId, session.toMap())

        origin.saveSession(session)
    }

    override fun terminateSession(sessionId: String) {
        service.delete(sessionId)

        origin.terminateSession(sessionId)
    }

    private fun saveSessionInCache(session: Session) {
        service.put(session.sessionId, session.toMap())
    }
}