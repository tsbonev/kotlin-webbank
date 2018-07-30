package com.clouway.bankapp.core.security

import com.clouway.bankapp.core.Session
import com.clouway.bankapp.core.SessionNotFoundException
import com.clouway.bankapp.core.UserAlreadyHasSessionException

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class SessionLoader (private val origin: SessionHandler) : SessionHandler {

    override fun getSessionById(sessionId: String): Session {
        return origin.getSessionById(sessionId)
    }

    override fun saveSession(session: Session){
        try{
            origin.getSessionById(session.sessionId)
            throw UserAlreadyHasSessionException()
        }catch (e: SessionNotFoundException){
            origin.saveSession(session)
        }
    }

    override fun terminateSession(sessionId: String){
        origin.terminateSession(sessionId)
    }

}