package com.clouway.bankapp.adapter.memcache

import com.clouway.bankapp.core.Session
import com.clouway.bankapp.core.SessionNotFoundException
import com.clouway.bankapp.core.User
import java.util.*
import kotlin.collections.LinkedHashMap

/**
 * @author tsbonev@gmail.com
 */
class MemcacheSessionHandler(private val provider: CacheServiceProvider) : SessionHandler {

    private fun mapToSession(map: LinkedHashMap<*, *>): Session {
        return Session(
                map["userId"] as Long,
                map["sessionId"] as String,
                map["expiresOn"] as Date,
                map["isAuthenticated"] as Boolean
        )
    }

    private fun mapToUser(map: LinkedHashMap<*, *>): User {
        return User(
                map["id"] as Long,
                map["username"] as String,
                ""
        )
    }

    override fun getSessionById(sessionId: String): Pair<Session, User>? {
        val cachedSession = provider.service.get(sessionId)

        return if (cachedSession is Pair<*, *>) {
            val sessionMap = cachedSession.first as LinkedHashMap<*, *>
            val userMap = cachedSession.second as LinkedHashMap<*, *>
            Pair(mapToSession(sessionMap), mapToUser(userMap))
        }else{
            return null
        }

    }

    override fun saveSessionInCache(session: Session, user: User) {
        provider.service.put(session.sessionId, Pair<Any, Any>(session.toMap(), user.toMap()))
    }

    override fun terminateSession(sessionId: String) {
        provider.service.delete(sessionId)
    }


}