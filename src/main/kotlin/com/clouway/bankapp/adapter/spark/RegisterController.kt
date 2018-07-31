package com.clouway.bankapp.adapter.spark

import com.clouway.bankapp.core.JsonTransformerWrapper
import com.clouway.bankapp.core.UserAlreadyExistsException
import com.clouway.bankapp.core.UserRegistrationRequest
import com.clouway.bankapp.core.UserRepository
import org.eclipse.jetty.http.HttpStatus
import spark.Request
import spark.Response

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class RegisterController(private val userRepo: UserRepository,
                         private val transformer: JsonTransformerWrapper) : Controller {
    override fun handle(request: Request, response: Response): Any? {
        return try{
            userRepo.registerIfNotExists(transformer.fromJson(request.body(), UserRegistrationRequest::class.java))
            response.status(HttpStatus.CREATED_201)
        }catch (e: UserAlreadyExistsException){
            response.status(HttpStatus.BAD_REQUEST_400)
        }
    }

}