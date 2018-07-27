package com.clouway.bankapp.core.security

import com.clouway.bankapp.adapter.gae.memcache.SessionHandler
import com.clouway.bankapp.core.*
import org.eclipse.jetty.http.HttpStatus
import spark.Filter
import spark.Request
import spark.Response
import java.time.Instant
import java.time.LocalDate
import java.util.*

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class SecurityFilter(private val sessionHandler: SessionHandler,
                     private val sessionRepo: SessionRepository,
                     private val userRepo: UserRepository,
                     private val sessionAge: Int = 600000,
                     private val getCurrentTime: () -> Date = {
                        Date.from(Instant.now())
                    }) : Filter {

    fun getUserContext(sessionId: String): User{
        val sessionContext = sessionHandler.getSessionById(sessionId)

        return sessionContext?.second ?: throw SessionNotFoundException()
    }

    fun getSessionContext(sessionId: String): Session{

        val sessionContext = sessionHandler.getSessionById(sessionId)

        return sessionContext?.first ?: throw SessionNotFoundException()
    }

    fun logOut(sessionId: String) {
        sessionRepo.terminateSession(sessionId)
        sessionHandler.terminateSession(sessionId)
    }

    fun isLoggedIn(sessionId: String): Boolean {

        return if(sessionHandler.getSessionById(sessionId) != null){
            true
        }else{
            val sessionInDb = sessionRepo.getSessionAvailableAt(sessionId, getCurrentTime())
            validateDbSession(sessionInDb)
        }
    }

    private fun validateDbSession(possibleSession: Optional<Session>): Boolean{
        if (!possibleSession.isPresent) {
            return false
        }

        val foundSession = possibleSession.get()
        val possibleUser = userRepo.getById(foundSession.userId)

        if(!possibleUser.isPresent){
            sessionRepo.terminateSession(foundSession.sessionId)
            return false
        }

        val foundUser = possibleUser.get()
        setUpSessionCache(foundSession, foundUser)
        sessionRepo.refreshSession(foundSession)
        return true
    }

    /**
     * Adds a cookie to the user's browser.
     *
     * @param res response
     */
    private fun addCookie(res: Response) {
        val UUIDValue = UUID.randomUUID().toString()
        res.cookie("/", "SID", UUIDValue, sessionAge, false, true)
    }

    private fun setUpSessionCache(session: Session, user: User) {
        sessionHandler.saveSessionInCache(session, user)
    }

    override fun handle(req: Request, res: Response) {

        if(req.cookie("SID") == null) {
            addCookie(res)
        }

        val sessionId = req.cookie("SID").toString()

        if(isLoggedIn(sessionId)) res.status(HttpStatus.OK_200)
        else res.status(HttpStatus.NOT_FOUND_404)

    }

}