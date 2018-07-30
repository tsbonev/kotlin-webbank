package com.clouway.bankapp.adapter.gae.datastore

import com.clouway.bankapp.core.Session
import com.clouway.bankapp.core.SessionRepository
import com.google.appengine.api.datastore.*
import com.google.appengine.api.datastore.FetchOptions.Builder.withLimit
import java.time.Instant
import java.util.*

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class DatastoreSessionRepository(private val limit: Int = 100,
                                 private val sessionRefreshTime: Long = 86400L) : SessionRepository {

    private val service: DatastoreService
            get() = DatastoreServiceFactory.getDatastoreService()

    private val sessionEntityMapper = object: EntityMapper<Session> {
        override fun map(obj: Session): Entity {
            val entity = Entity("Session", obj.sessionId)

            entity.setProperty("userId", obj.userId)
            entity.setProperty("username", obj.username)
            entity.setProperty("expiresOn", obj.expiresOn)
            entity.setProperty("isAuthenticated", obj.isAuthenticated)

            return entity
        }
    }

    private val sessionRowMapper = object: RowMapper<Session> {
        override fun map(entity: Entity): Session{
            return Session(
                    entity.properties["userId"] as Long,
                    entity.key.name,
                    entity.properties["expiresOn"] as Date,
                    entity.properties["username"] as String,
                    entity.properties["isAuthenticated"] as Boolean
            )
        }
    }

    private fun getSessionEntityList(date: Date): List<Entity>{
        return service
                .prepare(Query("Session")
                        .setFilter(greaterThanFilter("expiresOn", date)))
                .asList(withLimit(limit))
    }

    private fun greaterThanFilter(param: String, value: Any): Query.Filter{
        return Query.FilterPredicate(param,
                Query.FilterOperator.GREATER_THAN, value)
    }

    override fun registerSession(session: Session) {
        service.put(sessionEntityMapper.map(session))
    }

    override fun refreshSession(session: Session) {
        val key = KeyFactory.createKey("Session", session.sessionId)
        val sessionEntity = service.get(key)
        sessionEntity.setProperty("expiresOn", Date.from(Instant.now()
                .plusSeconds(sessionRefreshTime)))

        service.put(sessionEntity)

    }

    override fun terminateSession(sessionId: String) {
        val key = KeyFactory.createKey("Session", sessionId)
        service.delete(key)
    }

    override fun deleteSessionsExpiringBefore(date: Date) {
        val sessionEntityList = getSessionEntityList(date)

        for(session in sessionEntityList){
            service.delete(session.key)
        }

    }

    override fun getSessionAvailableAt(sessionId: String, date: Date): Optional<Session> {

        val sessionKey = KeyFactory.createKey("Session", sessionId)

        return try{
            val sessionEntity = service.get(sessionKey)
            val sessionExpirationDate = sessionEntity.properties["expiresOn"] as Date

            if(sessionExpirationDate.before(date)){
                Optional.empty()
            }
            else{
                Optional.of(sessionRowMapper.map(sessionEntity))
            }
        }catch (e: EntityNotFoundException){
            Optional.empty()
        }


    }

    override fun getActiveSessionsCount(): Int {

        return service
                .prepare(Query("Session").setKeysOnly()
                        .setFilter(greaterThanFilter("expiresOn", Date.from(Instant.now()))))
                .asList(withLimit(limit)).size

    }
}