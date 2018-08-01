package com.clouway.bankapp.adapter.spark

import com.clouway.bankapp.core.JsonTransformerWrapper
import com.clouway.bankapp.core.UserAlreadyExistsException
import com.clouway.bankapp.core.UserRegistrationRequest
import com.clouway.bankapp.core.UserRepository
import com.google.appengine.api.taskqueue.QueueFactory
import com.google.appengine.api.taskqueue.TaskOptions
import com.google.apphosting.api.ApiProxy
import org.eclipse.jetty.http.HttpStatus
import spark.Request
import spark.Response

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class RegisterController(private val userRepo: UserRepository,
                         private val transformer: JsonTransformerWrapper
) : Controller {

    private fun sendMailTo(email: String){
        try{
            val queue = QueueFactory.getQueue("mailing-que")
            queue.add(TaskOptions.Builder.withUrl("/worker").param("key", email))
        }catch (e: ApiProxy.CallNotFoundException){

        }
    }

    override fun handle(request: Request, response: Response): Any? {
        return try{
            val user = userRepo.
                    registerIfNotExists(transformer.fromJson(request.body(), UserRegistrationRequest::class.java))
            response.status(HttpStatus.CREATED_201)
            sendMailTo(user.username)
        }catch (e: UserAlreadyExistsException){
            response.status(HttpStatus.BAD_REQUEST_400)
        }
    }

}