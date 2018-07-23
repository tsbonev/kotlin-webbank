package server
import com.clouway.bankapp.adapter.datastore.DatastoreServiceProvider
import com.clouway.bankapp.adapter.datastore.DatastoreTransactionRepository
import com.clouway.bankapp.adapter.datastore.DatastoreUserRepository
import com.clouway.bankapp.adapter.web.JsonTransformer
import com.clouway.bankapp.adapter.web.LoginController
import com.clouway.bankapp.adapter.web.RegisterController
import com.clouway.bankapp.adapter.web.TransactionController
import spark.Filter
import spark.Response
import spark.Spark.*
import spark.kotlin.before
import spark.servlet.SparkApplication
import java.util.*


/**
 * Adds a cookie to the user's browser
 * and returns its value.
 *
 * @param res response
 * @return created cookie's value
 */
fun addCookie(res: Response): String {

    val UUIDValue = UUID.randomUUID().toString()
    res.cookie("/", "SID", UUIDValue, 60000, false, true)
    return UUIDValue

}

class AppBootstrap : SparkApplication{
    override fun init() {

        val transformer= JsonTransformer()
        val provider = DatastoreServiceProvider()
        val userRepo = DatastoreUserRepository(provider)
        val transactionRepo = DatastoreTransactionRepository(provider)

        val registerController = RegisterController(userRepo, transformer)
        val transactionController = TransactionController(transactionRepo, transformer)
        val loginController = LoginController(userRepo, transformer)

        options("/*"
        ) { request, response ->

            val accessControlRequestHeaders = request
                    .headers("Access-Control-Request-Headers")
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers",
                        accessControlRequestHeaders)
            }

            val accessControlRequestMethod = request
                    .headers("Access-Control-Request-Method")
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods",
                        accessControlRequestMethod)
            }

            "OK"
        }

        before(Filter { req, res -> res.header("Access-Control-Allow-Origin", "*") })

        before(Filter { req, res ->
            addCookie(res)
            res.raw().characterEncoding = "UTF-8"
        })

        get("/user/:username"){
            req, res ->
            val user = loginController.doGet(req, res)
            transformer.render(user)
        }

        get("/transactions/:id"){
                req, res ->
                val transactionList = transactionController.doGet(req, res)
                transformer.render(transactionList)
        }

        post("/transactions/save", "application/json"){
            req, res ->
            transactionController.doPost(req, res)
        }

        post("/register", "application/json"){
            req, res ->
            registerController.doPost(req, res)
        }

        post("/login", "application/json"){
            req, res ->
            loginController.doPost(req, res)
        }

    }
}