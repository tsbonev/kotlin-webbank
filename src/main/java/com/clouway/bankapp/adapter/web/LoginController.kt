package com.clouway.bankapp.adapter.web

import com.clouway.bankapp.core.*
import org.eclipse.jetty.http.HttpStatus
import spark.Request
import spark.Response
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import java.time.Instant
import java.util.*

/**
 * @author tsbonev@gmail.com
 */
class LoginController(private val userRepo: UserRepository,
                      private val sessionRepo: SessionRepository,
                      private val transformer: JsonTransformer,
                      private val sessionLifetime: Long = 600000) {

    fun doGet(req: Request, res: Response): User{

        res.type("application/json")
        val user = userRepo.getByUsername(req.params("username"))

        return if(user.isPresent){
            user.get()
        }
        else{
            throw NotImplementedException()
        }
    }

    fun doPost(req: Request, res: Response) {

        println("Login initiated")
        
        val request = transformer.from(req.body(), UserRegistrationRequest::class.java)

        val actualUser = userRepo.getByUsername(request.username)
        if(actualUser.isPresent){
            
            println("User found in db")
            
            val user = actualUser.get()

            if(user.password != request.password){
                res.status(HttpStatus.UNAUTHORIZED_401)
                return
            }

            println("User password is correct")

            sessionRepo.registerSession(Session(
                    user.id,
                    req.cookie("SID"),
                    Date.from(Instant.now().plusSeconds(sessionLifetime)),
                    true
            ))

            println("Session registered in db")

            res.status(HttpStatus.OK_200)
        }else{
            res.status(HttpStatus.UNAUTHORIZED_401)
        }
    }

}