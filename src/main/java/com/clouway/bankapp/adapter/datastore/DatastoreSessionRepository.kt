package com.clouway.bankapp.adapter.datastore

import com.clouway.bankapp.core.Session
import com.clouway.bankapp.core.SessionRepository
import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.datastore.FetchOptions.Builder.withLimit
import com.google.appengine.api.datastore.KeyFactory
import com.google.appengine.api.datastore.Query
import java.time.Instant
import java.util.*

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class DatastoreSessionRepository(private val provider: ServiceProvider,
                                 private val limit: Int = 100) : SessionRepository {


    private val sessionRefreshTime = 86400L

    private val sessionEntityMapper = object: EntityMapper<Session>{
        override fun map(obj: Session): Entity {
            val entity = Entity("Session", obj.sessionId)

            entity.setProperty("userId", obj.userId)
            entity.setProperty("expiresOn", obj.expiresOn)
            entity.setProperty("isAuthenticated", obj.isAuthenticated)

            return entity
        }
    }

    private val sessionRowMapper = object: RowMapper<Session>{
        override fun map(entity: Entity): Session{
            return Session(
                    entity.properties["userId"].toString().toLong(),
                    entity.key.toString(),
                    convertToDate(entity.properties["expiresOn"]),
                    entity.properties["isAuthenticated"].toString().toBoolean()
            )
        }
    }

    private fun getSessionEntityList(date: Date): List<Entity>{
        return provider.get()
                .prepare(Query("Session")
                        .setFilter(greaterThanFilter("expiresOn", date)))
                .asList(withLimit(limit))
    }

    private fun convertToDate(obj: Any?): Date{

        if(obj is Date){
            return obj
        }
        return Date.from(Instant.now())
    }

    private fun greaterThanFilter(param: String, value: Any): Query.Filter{
        return Query.FilterPredicate(param,
                Query.FilterOperator.GREATER_THAN, value)
    }

    override fun registerSession(session: Session) {
        provider.get().put(sessionEntityMapper.map(session))
    }

    override fun refreshSession(session: Session) {
        val key = KeyFactory.createKey("Session", session.sessionId)
        val sessionEntity = provider.get().get(key)
        sessionEntity.setProperty("expiresOn", Date.from(Instant.now()
                .plusSeconds(sessionRefreshTime)))

        provider.get().put(sessionEntity)

    }

    override fun terminateSession(sessionId: String) {
        val key = KeyFactory.createKey("Session", sessionId)
        provider.get().delete(key)
    }

    override fun deleteSessionsExpiringBefore(date: Date) {
        val sessionEntityList = getSessionEntityList(date)

        for(session in sessionEntityList){
            provider.get().delete(session.key)
        }

    }

    override fun getSessionAvailableAt(sessionId: String, date: Date): Optional<Session> {

        val sessionEntity = getSessionEntityList(date)

        return if(sessionEntity.isEmpty()){
            Optional.empty()
        }
        else{
            Optional.of(sessionRowMapper.map(sessionEntity.first()))
        }

    }

    override fun getActiveSessionsCount(): Int {

        return provider.get()
                .prepare(Query("Session").setKeysOnly()
                        .setFilter(greaterThanFilter("expiresOn", Date.from(Instant.now()))))
                .asList(withLimit(limit)).size

    }


}