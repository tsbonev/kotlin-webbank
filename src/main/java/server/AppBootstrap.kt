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
import spark.Spark.*
import spark.kotlin.before
import spark.servlet.SparkApplication

class AppBootstrap : SparkApplication{
    override fun init() {

        val transformer= JsonTransformer()
        val datastoreProvider = DatastoreServiceProvider()
        val memcacheProvider = MemcacheServiceProvider()
        val sessionHandler = MemcacheSessionHandler(memcacheProvider)


        val userRepo = DatastoreUserRepository(datastoreProvider)
        val sessionRepo = DatastoreSessionRepository(datastoreProvider)
        val transactionRepo = DatastoreTransactionRepository(datastoreProvider)


        val sessionFilter = SessionFilter(sessionHandler,
                sessionRepo, userRepo)

        val registerController = RegisterController(userRepo, transformer)
        val transactionController = TransactionController(transactionRepo, transformer, sessionFilter)
        val loginController = LoginController(userRepo, sessionRepo, transformer)

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

        before(sessionFilter)

        before(Filter { req, res -> res.header("Access-Control-Allow-Origin", "*") })

        before(Filter { req, res ->
            res.raw().characterEncoding = "UTF-8"
        })


        get("/users"){
            req, res ->
            val userList = userRepo.getAll()
            transformer.render(userList)
        }

        get("/islogged"){
            req, res ->
            val isLogged = sessionFilter.isLoggedIn(req.cookie("SID").toString())
            transformer.render(isLogged)
        }

        post("/logout"){
            req, res ->
            sessionFilter.logOut(req.cookie("SID").toString())
            transformer.render(res.status())
        }

        get("/transactions"){
                req, res ->
                val transactionList = transactionController.doGet(req, res)
                transformer.render(transactionList)
        }

        get("/sessions"){
            req, res ->
            val sessionList = listOf(sessionRepo.getActiveSessionsCount(),
                    sessionFilter.isLoggedIn(req.cookie("SID").toString()))
            transformer.render(sessionList)
        }

        get("/addToMemcache"){
            req, res ->
            memcacheProvider.service.put("test1", "test2")
            transformer.render(memcacheProvider.service.get("test1"))
        }

        get("/viewMemcache"){
            req, res ->
            val memcached = memcacheProvider.service.get("test1")
            transformer.render(memcached)
        }

        get("/statistics"){
            req, res ->
            val stats = memcacheProvider.service.statistics
            transformer.render(stats)
        }

        post("/transactions/save", "application/json"){
            req, res ->
            transactionController.doPost(req, res)
            transformer.render(res.status())
        }

        post("/register", "application/json"){
            req, res ->
            registerController.doPost(req, res)
            transformer.render(res.status())
        }

        post("/login", "application/json"){
            req, res ->
            loginController.doPost(req, res)
            transformer.render(res.status())
        }

    }
}