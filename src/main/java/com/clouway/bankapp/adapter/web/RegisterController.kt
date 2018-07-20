package com.clouway.bankapp.adapter.web

import com.clouway.bankapp.core.UserRegistrationRequest
import com.clouway.bankapp.core.UserRepository
import spark.Request
import spark.Response

/**
 * @author tsbonev@gmail.com
 */
class RegisterController(private val userRepo: UserRepository,
                         private val transformer: JsonTransformer) {

    fun doPost(req: Request, res: Response){
        userRepo.registerIfNotExists(transformer.from(req.body(), UserRegistrationRequest::class.java))
        res.status(200)
    }

}