package com.clouway.bankapp.adapter.gae.datastore

import com.clouway.bankapp.core.*
import com.clouway.bankapp.core.security.SessionHandler
import java.time.LocalDateTime

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class DatastoreSessionHandler(private val sessions: SessionRepository,
                              private val getCurrentTime: () -> LocalDateTime = {
                                  LocalDateTime.now()
                              }) : SessionHandler {

    override fun getSessionById(sessionId: String): Session {

        val possibleSession = sessions.getSessionAvailableAt(sessionId, getCurrentTime())
        return if(possibleSession.isPresent){
            sessions.refreshSession(possibleSession.get())
            possibleSession.get()
        }else{
            throw SessionNotFoundException()
        }
    }

    override fun saveSession(session: Session) {
        sessions.registerSession(session)
    }

    override fun terminateSession(sessionId: String) {
        sessions.terminateSession(sessionId)
    }
}