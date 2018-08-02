package com.clouway.bankapp.adapter.spark

import com.google.appengine.api.taskqueue.Queue
import com.google.appengine.api.taskqueue.QueueFactory
import com.google.appengine.api.taskqueue.TaskOptions
import org.eclipse.jetty.http.HttpStatus
import spark.Request
import spark.Response
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class InternalMailController : Controller {
    override fun handle(request: Request, response: Response): Any? {


        val queue = QueueFactory.getQueue("mailing-queue")
        queue.add(TaskOptions.Builder.withUrl("/mail")
                .param("email", request.queryParams("email")))

        return HttpStatus.ACCEPTED_202

        /*val url = "http://localhost:8080/mail"
        val urlQuery = "email=${request.queryParams("email")}"
        val obj = URL(url)
        val con = obj.openConnection() as HttpURLConnection
        con.requestMethod = "POST"
        con.doOutput = true

        val wr = DataOutputStream(con.outputStream)
        wr.writeBytes(urlQuery)
        wr.flush()
        wr.close()

        return con.responseMessage + " " + con.responseCode*/
    }
}