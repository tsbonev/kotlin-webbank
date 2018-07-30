package com.clouway.bankapp.adapter.spark

import com.clouway.bankapp.core.*
import com.clouway.bankapp.core.security.SessionHandler
import org.eclipse.jetty.http.HttpStatus
import spark.Request
import spark.Response
import java.time.Instant
import java.util.*

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class LoginController(private val userRepo: UserRepository,
                      private val sessionHandler: SessionHandler,
                      private val transformer: JsonTransformer,
                      private val sessionLifetime: Long = 600000,
                      private val getExpirationDate: () -> Date = {
                               Date.from(Instant.now().plusSeconds(sessionLifetime))
                           }) : Controller {

    override fun handle(request: Request, response: Response): Any? {

        val loginRequest = transformer.from(request.body(), UserRegistrationRequest::class.java)

        val actualUser = userRepo.getByUsername(loginRequest.username)

        if(actualUser.isPresent){

            val user = actualUser.get()

            if(user.password != loginRequest.password){
                return response.status(HttpStatus.UNAUTHORIZED_401)
            }

            try{
                sessionHandler.saveSession(Session(
                        user.id,
                        request.cookie("SID").toString(),
                        getExpirationDate(),
                        user.username,
                        true
                ))
            }catch (e: UserAlreadyHasSessionException){}

            return response.status(HttpStatus.OK_200)
        }else{
            return response.status(HttpStatus.UNAUTHORIZED_401)
        }
    }
}