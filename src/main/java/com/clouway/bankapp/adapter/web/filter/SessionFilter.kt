package com.clouway.bankapp.adapter.web.filter

import com.clouway.bankapp.adapter.memcache.SessionHandler
import com.clouway.bankapp.core.*
import org.eclipse.jetty.http.HttpStatus
import spark.Filter
import spark.Request
import spark.Response
import java.time.Instant
import java.util.*

/**
 * @author tsbonev@gmail.com
 */
class SessionFilter(private val sessionHandler: SessionHandler,
                    private val sessionRepo: SessionRepository,
                    private val userRepo: UserRepository,
                    val userContext: ThreadLocal<User> = ThreadLocal(),
                    val sessionContext: ThreadLocal<Session> = ThreadLocal()) : Filter {

    fun isLoggedIn(): Boolean{
       return userContext.get() != null && sessionContext.get() != null
    }

    fun logOut(){
        userContext.remove()
        sessionContext.remove()
    }

    private fun setUpSessionContext(session: Session){

        sessionContext.set(session)
        val possibleUser = userRepo.getById(session.userId)
        if(!possibleUser.isPresent) {
            userContext.remove()
            sessionContext.remove()
            throw SessionNotFoundException()
        }
        userContext.set(userRepo.getById(session.userId).get())
    }

    override fun handle(req: Request, res: Response) {
        val sessionId = req.cookie("SID")
        if(sessionId != null){
            try{
                val possibleSession = sessionHandler.getSessionById(sessionId)
                setUpSessionContext(possibleSession)
                res.status(HttpStatus.FOUND_302)
            }catch (e: SessionNotFoundException){
                try{
                    val datasourceSession = sessionRepo
                            .getSessionAvailableAt(sessionId, Date.from(Instant.now()))
                    if(!datasourceSession.isPresent) throw SessionNotFoundException()

                    setUpSessionContext(datasourceSession.get())
                    res.status(HttpStatus.FOUND_302)
                }catch (e: SessionNotFoundException){
                    res.status(HttpStatus.UNAUTHORIZED_401)
                }
            }
        }

    }

}