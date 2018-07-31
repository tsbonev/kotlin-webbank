package com.clouway.bankapp.adapter.gae.datastore

import com.clouway.bankapp.adapter.gae.get
import com.clouway.bankapp.adapter.gae.putJson
import com.clouway.bankapp.adapter.gae.toUtilDate
import com.clouway.bankapp.core.*
import com.google.appengine.api.datastore.*
import com.google.appengine.api.datastore.FetchOptions.Builder.withLimit
import java.time.LocalDateTime
import java.util.*

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class DatastoreSessionRepository(private val transformer: JsonTransformerWrapper,
                                 private val limit: Int = 100,
                                 private val instant: LocalDateTime = LocalDateTime.now(),
                                 private val sessionRefreshDays: Long = 10) : SessionRepository {

    private val service: DatastoreService
        get() = DatastoreServiceFactory.getDatastoreService()


    private fun greaterThanFilter(param: String, value: Any): Query.Filter {
        return Query.FilterPredicate(param,
                Query.FilterOperator.GREATER_THAN, value)
    }

    private fun getSessionList(date: LocalDateTime): List<Session> {
        val sessionEntities = service
                .prepare(Query("Session")
                        .setFilter(greaterThanFilter("expiresOn", date.toUtilDate())))
                .asList(withLimit(limit))

        val sessionList = mutableListOf<Session>()

        sessionEntities.forEach {
            sessionList.add(transformer.fromJson(it.properties["content"].toString(), Session::class.java))
        }

        return sessionList
    }

    override fun registerSession(session: Session) {
        val sessionKey = KeyFactory.createKey("Session", session.sessionId)
        try {
            service.get(sessionKey)
            throw UserAlreadyHasSessionException()
        } catch (e: EntityNotFoundException) {
            service.putJson(sessionKey, session, transformer)
        }
    }

    override fun refreshSession(session: Session) {
        val key = KeyFactory.createKey("Session", session.sessionId)
        val possibleSession = service.get(key, Session::class.java, transformer)

        if (possibleSession.isPresent) {
            val refreshedSession = Session(
                    session.userId,
                    session.sessionId,
                    instant.plusDays(sessionRefreshDays),
                    session.username
            )
            service.putJson(key, refreshedSession, transformer)
        } else {
            throw SessionNotFoundException()
        }
    }

    override fun terminateSession(sessionId: String) {
        val key = KeyFactory.createKey("Session", sessionId)
        service.delete(key)
    }

    override fun deleteSessionsExpiringBefore(date: LocalDateTime) {
        val sessionList = getSessionList(date)

        for (session in sessionList) {
            val sessionKey = KeyFactory.createKey("Session", session.sessionId)
            service.delete(sessionKey)
        }
    }

    override fun getSessionAvailableAt(sessionId: String, date: LocalDateTime): Optional<Session> {

        val sessionKey = KeyFactory.createKey("Session", sessionId)

        return try {
            val sessionEntity = service.get(sessionKey)
            val sessionExpirationDate = sessionEntity.properties["expiresOn"] as Date
            if (sessionExpirationDate.before(date.toUtilDate())) return Optional.empty()
            service.get(sessionKey, Session::class.java, transformer)
        } catch (e: EntityNotFoundException) {
            Optional.empty()
        }
    }

    override fun getActiveSessionsCount(): Int {
        return service
                .prepare(Query("Session").setKeysOnly()
                        .setFilter(greaterThanFilter("expiresOn", instant.toUtilDate())))
                .asList(withLimit(limit)).size
    }
}