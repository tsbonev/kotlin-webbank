package com.clouway.bankapp.adapter.web

import com.clouway.bankapp.core.Session
import com.clouway.bankapp.core.SessionNotFoundException
import com.clouway.bankapp.core.SessionRepository
import org.eclipse.jetty.http.HttpStatus
import spark.Response
import java.time.Instant
import java.util.*

/**
 * @author tsbonev@gmail.com
 */
class SessionController(private val sessionRepo: SessionRepository) {

    private val timeToExpire = 600000L

    fun doGet(sessionId: String, res: Response): Session{
        val possibleSession = sessionRepo
                .getSessionAvailableAt(sessionId, Date.from(Instant.now()))
        return if(possibleSession.isPresent){
            res.status(HttpStatus.FOUND_302)
            possibleSession.get()
        }else{
            res.status(HttpStatus.NOT_FOUND_404)
            throw SessionNotFoundException()
        }
    }

    fun doPost(sessionId: String, userId: Long, res: Response) {

        sessionRepo.registerSession(Session(userId,
                sessionId,
                Date.from(Instant.now().plusSeconds(timeToExpire))))
        res.status(HttpStatus.CREATED_201)

    }

}