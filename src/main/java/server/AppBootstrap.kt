package server

import com.clouway.bankapp.adapter.HomeSpark
import com.clouway.bankapp.adapter.helper.Engine
import spark.Spark.*
import spark.servlet.SparkApplication

class AppBootstrap : SparkApplication{
    override fun init() {

        get("/hello/:name") {
            req, res -> Engine.getEngine().render(HomeSpark.doGet(req, res))
        }
    }
}