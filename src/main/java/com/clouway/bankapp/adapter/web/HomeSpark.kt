package com.clouway.bankapp.adapter.web

import com.clouway.bankapp.adapter.web.helper.ThymeleafEngine
import spark.ModelAndView
import spark.Request
import spark.Response

class HomeSpark(private val engine: ThymeleafEngine = ThymeleafEngine()) {


    fun doGet(req: Request, res: Response): ModelAndView {
        val params = HashMap<String, Any>()
        params["name"] = req.params(":name")
        return ModelAndView(params, "hello")
    }

}