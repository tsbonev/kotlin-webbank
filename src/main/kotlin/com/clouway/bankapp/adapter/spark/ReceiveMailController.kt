package com.clouway.bankapp.adapter.spark

import spark.Request
import spark.Response
import java.io.UnsupportedEncodingException
import java.util.*
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class ReceiveMailController : Controller {
    override fun handle(request: Request, response: Response): Any? {

        val email = request.params("key")
        val props = Properties()
        val session = Session.getDefaultInstance(props, null)

        try{
            val msg = MimeMessage(session)
            msg.setFrom(InternetAddress("noreply@sacred-union210613.appspot.com", "Sparkbank Admin"))
            msg.setRecipient(Message.RecipientType.TO,
                    InternetAddress(email, "User"))
            msg.subject = "Your account has been registered"
            msg.setText("This email was sent through GAE")
            Transport.send(msg)
        }catch (e: AddressException){
            e.printStackTrace()
        }catch (e: MessagingException){
            e.printStackTrace()
        }catch (e: UnsupportedEncodingException){
            e.printStackTrace()
        }

        return response.redirect("/")
    }
}