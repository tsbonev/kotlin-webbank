package com.clouway.bankapp.adapter.gae.memcache

import com.clouway.bankapp.adapter.gae.get
import com.clouway.bankapp.adapter.gae.putJson
import com.clouway.bankapp.core.JsonTransformerWrapper
import com.clouway.bankapp.core.Session
import com.clouway.bankapp.core.security.SessionHandler
import com.google.appengine.api.memcache.MemcacheService
import com.google.appengine.api.memcache.MemcacheServiceFactory

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class MemcacheSessionHandler(private val origin: SessionHandler,
                             private val transformer: JsonTransformerWrapper) : SessionHandler {

    private val service: MemcacheService
        get() = MemcacheServiceFactory.getMemcacheService()

    override fun getSessionById(sessionId: String): Session {

        val cachedSession = service.get("sid_$sessionId", Session::class.java, transformer)
        return if (cachedSession.isPresent) {
            cachedSession.get()
        } else {
            val persistentSession = origin.getSessionById(sessionId)
            saveSessionInCache(persistentSession)
            persistentSession
        }

    }

    override fun saveSession(session: Session) {
        service.putJson("sid_${session.sessionId}", session, transformer)

        origin.saveSession(session)
    }

    override fun terminateSession(sessionId: String) {
        service.delete("sid_$sessionId")

        origin.terminateSession(sessionId)
    }

    private fun saveSessionInCache(session: Session) {
        service.putJson("sid_${session.sessionId}", session, transformer)
    }
}