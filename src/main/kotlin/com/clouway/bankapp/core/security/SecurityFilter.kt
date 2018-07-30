package com.clouway.bankapp.core.security

import spark.Filter
import spark.Request
import spark.Response
import java.util.*

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class SecurityFilter(private val sessionAge: Int = 600000) : Filter {

    /**
     * Adds a cookie to the user's browser.
     *
     * @param res response
     */
    private fun addCookie(res: Response) {
        val UUIDValue = UUID.randomUUID().toString()
        res.cookie("/", "SID", UUIDValue, sessionAge, false, false)
    }

    override fun handle(req: Request, res: Response) {
        if(req.cookie("SID") == null) {
            addCookie(res)
        }
    }

}