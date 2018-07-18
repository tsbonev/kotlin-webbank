package server

import com.clouway.bankapp.adapter.HomeSpark
import com.clouway.bankapp.adapter.helper.Engine
import spark.Spark
import spark.Spark.get


fun main(args: Array<String>) {
    Spark.staticFiles.location("/web")


    get("/hello/:name") {
        req, res -> Engine.getEngine().render(HomeSpark.doGet(req, res))
    }

}