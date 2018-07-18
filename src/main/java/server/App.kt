package server

import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Spark
import spark.Spark.*
import spark.template.thymeleaf.ThymeleafTemplateEngine

fun message(req: Request, res: Response): ModelAndView{
    val params = HashMap<String, Any>()
    params["name"] = req.params(":name")
    return ModelAndView(params, "hello")
}

fun main(args: Array<String>) {
    Spark.staticFiles.location("/web")

    get("/hello/:name") {
        req: Request, res: Response -> ThymeleafTemplateEngine().render(message(req, res))
    }

}