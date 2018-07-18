package com.clouway.bankapp.adapter

import com.clouway.bankapp.adapter.helper.Engine
import spark.ModelAndView
import spark.Request
import spark.Response

class HomeSpark(private val engine: Engine = Engine()) {

    companion object {

        fun doGet(req: Request, res: Response): ModelAndView{
            val params = HashMap<String, Any>()
            params["name"] = req.params(":name")
            return ModelAndView(params, "hello")
        }

    }

}