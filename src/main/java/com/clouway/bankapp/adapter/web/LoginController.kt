package com.clouway.bankapp.adapter.web

import com.clouway.bankapp.core.*
import org.eclipse.jetty.http.HttpStatus
import spark.Request
import spark.Response
import java.time.Instant
import java.util.*

/**
 * @author tsbonev@gmail.com
 */
class LoginController(private val userRepo: UserRepository,
                      private val sessionRepo: SessionRepository,
                      private val transformer: JsonTransformer,
                      private val sessionLifetime: Long = 600000,
                      private val getExpirationDate: () -> Date = {
                               Date.from(Instant.now().plusSeconds(sessionLifetime))
                           }) {

    fun doPost(req: Request, res: Response) {

        val request = transformer.from(req.body(), UserRegistrationRequest::class.java)

        val actualUser = userRepo.getByUsername(request.username)

        if(actualUser.isPresent){
            
            val user = actualUser.get()

            if(user.password != request.password){
                res.status(HttpStatus.UNAUTHORIZED_401)
                return
            }

            sessionRepo.registerSession(Session(
                    user.id,
                    req.cookie("SID"),
                    getExpirationDate(),
                    true
            ))

            res.status(HttpStatus.OK_200)
        }else{
            res.status(HttpStatus.UNAUTHORIZED_401)
        }
    }

}