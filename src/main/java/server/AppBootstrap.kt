package server
import com.clouway.bankapp.adapter.datastore.DatastoreServiceProvider
import com.clouway.bankapp.adapter.datastore.DatastoreSessionRepository
import com.clouway.bankapp.adapter.datastore.DatastoreTransactionRepository
import com.clouway.bankapp.adapter.datastore.DatastoreUserRepository
import com.clouway.bankapp.adapter.memcache.MemcacheServiceProvider
import com.clouway.bankapp.adapter.memcache.MemcacheSessionHandler
import com.clouway.bankapp.adapter.web.JsonTransformer
import com.clouway.bankapp.adapter.web.LoginController
import com.clouway.bankapp.adapter.web.RegisterController
import com.clouway.bankapp.adapter.web.TransactionController
import com.clouway.bankapp.adapter.web.filter.SessionFilter
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
    res.cookie("/", "SID", UUIDValue, 6000000, false, true)
    return UUIDValue

}

class AppBootstrap : SparkApplication{
    override fun init() {

        val transformer= JsonTransformer()
        val datastoreProvider = DatastoreServiceProvider()
        val memcacheProvider = MemcacheServiceProvider()
        val sessionHandler = MemcacheSessionHandler(memcacheProvider)
        val userRepo = DatastoreUserRepository(datastoreProvider)
        val sessionRepo = DatastoreSessionRepository(datastoreProvider)
        val transactionRepo = DatastoreTransactionRepository(datastoreProvider)

        val registerController = RegisterController(userRepo, transformer)
        val transactionController = TransactionController(transactionRepo, transformer)
        val loginController = LoginController(userRepo, sessionRepo, transformer)


        val sessionFilter = SessionFilter(sessionHandler,
                sessionRepo, userRepo)

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
            res.raw().characterEncoding = "UTF-8"
        })

        before(Filter { req, res ->
            if(req.cookie("SID") == null){
                addCookie(res)
            }
        })

        before(sessionFilter)

        get("/isLoggedIn"){
            req, res ->
            val isLogged = sessionFilter.isLoggedIn()
            transformer.render(isLogged)
        }

        get("/user/:username"){
            req, res ->
            val user = loginController.doGet(req, res)
            transformer.render(user)
        }

        get("/cookie"){
            req, res ->
            val cookie = req.cookie("SID")
            transformer.render(cookie)
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