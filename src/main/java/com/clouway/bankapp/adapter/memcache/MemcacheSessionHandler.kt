package com.clouway.bankapp.adapter.memcache

import com.clouway.bankapp.core.Session
import com.clouway.bankapp.core.SessionNotFoundException
import java.util.*

/**
 * @author tsbonev@gmail.com
 */
class MemcacheSessionHandler(private val provider: ServiceProvider) : SessionHandler {

    private fun mapToSession(map: LinkedHashMap<*, *>): Session{
        return Session(
                map["userId"] as Long,
                map["sessionId"] as String,
                map["expiresOn"] as Date,
                map["isAuthenticated"] as Boolean
        )
    }

    override fun getSessionById(sessionId: String): Session {
        val cachedSession = provider.service.get(sessionId)
        if(cachedSession != null){
            val sessionMap = cachedSession as LinkedHashMap<*, *>
            return mapToSession(sessionMap)
        }
        throw SessionNotFoundException()
    }

    override fun saveSessionInCache(session: Session) {
        provider.service.put(session.sessionId, session.toMap())
    }


}