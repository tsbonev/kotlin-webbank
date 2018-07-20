package com.clouway.bankapp.adapter.web

import com.clouway.bankapp.core.User
import com.clouway.bankapp.core.UserRepository
import spark.Request
import spark.Response
import sun.reflect.generics.reflectiveObjects.NotImplementedException

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

}