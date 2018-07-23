package com.clouway.bankapp.adapter.web

import com.clouway.bankapp.core.User
import com.clouway.bankapp.core.UserRegistrationRequest
import com.clouway.bankapp.core.UserRepository
import spark.Request
import spark.Response
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import java.util.*

/**
 * @author tsbonev@gmail.com
 */
class LoginController(private val userRepo: UserRepository,
                      private val transformer: JsonTransformer) {

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

        val request = transformer.from(req.body(), UserRegistrationRequest::class.java)

        val actualUser = userRepo.getByUsername(request.username)
        return if(actualUser.isPresent && actualUser.get().password == request.password){

            return res.status(200)

        }else{
            res.status(400)
        }
    }

}