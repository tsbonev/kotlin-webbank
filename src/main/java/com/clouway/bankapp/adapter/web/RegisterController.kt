package com.clouway.bankapp.adapter.web

import com.clouway.bankapp.core.UserAlreadyExistsException
import com.clouway.bankapp.core.UserRegistrationRequest
import com.clouway.bankapp.core.UserRepository
import org.eclipse.jetty.http.HttpStatus
import spark.Request
import spark.Response

/**
 * @author tsbonev@gmail.com
 */
class RegisterController(private val userRepo: UserRepository,
                         private val transformer: JsonTransformer) {

    fun doPost(req: Request, res: Response){
        try{
            userRepo.registerIfNotExists(transformer.from(req.body(), UserRegistrationRequest::class.java))
            res.status(HttpStatus.CREATED_201)
        }catch (e: UserAlreadyExistsException){
            res.status(HttpStatus.BAD_REQUEST_400)
        }
    }

}