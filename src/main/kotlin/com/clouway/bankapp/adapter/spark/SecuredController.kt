package com.clouway.bankapp.adapter.spark

import com.clouway.bankapp.core.SessionNotFoundException
import com.clouway.bankapp.core.security.SessionHandler
import org.eclipse.jetty.http.HttpStatus
import spark.Request
import spark.Response
import spark.Route
import java.time.Instant

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class SecuredController(private val controller: SecureController,
                        private val sessionLoader: SessionHandler) : Route {

    override fun handle(request: Request, response: Response): Any? {
        return try {
            val session = sessionLoader.getSessionById(request.headers("SID"))
            return controller.handle(request, response, session)
        } catch (e: SessionNotFoundException) {
            response.status(HttpStatus.UNAUTHORIZED_401)
        }

    }
}