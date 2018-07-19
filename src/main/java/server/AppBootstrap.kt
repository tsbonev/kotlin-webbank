package server

import com.clouway.bankapp.adapter.web.HomeSpark
import com.clouway.bankapp.adapter.web.helper.ThymeleafEngine
import spark.Spark.*
import spark.servlet.SparkApplication

class AppBootstrap : SparkApplication{
    override fun init() {

        val homeSpark = HomeSpark()
        val engine = ThymeleafEngine()

        get("/hello/:name") {
            req, res -> ThymeleafEngine::getEngine.invoke(engine)
                .render(HomeSpark::doGet.invoke(homeSpark, req, res))
        }
    }
}