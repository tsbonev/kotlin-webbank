package server
import com.clouway.bankapp.adapter.gae.datastore.DatastoreServiceProvider
import com.clouway.bankapp.adapter.gae.datastore.DatastoreSessionRepository
import com.clouway.bankapp.adapter.gae.datastore.DatastoreTransactionRepository
import com.clouway.bankapp.adapter.gae.datastore.DatastoreUserRepository
import com.clouway.bankapp.adapter.gae.memcache.MemcacheServiceProvider
import com.clouway.bankapp.adapter.gae.memcache.MemcacheSessionHandler
import com.clouway.bankapp.adapter.spark.JsonTransformer
import com.clouway.bankapp.adapter.spark.LoginController
import com.clouway.bankapp.adapter.spark.RegisterController
import com.clouway.bankapp.adapter.spark.TransactionController
import com.clouway.bankapp.core.security.SecurityFilter
import com.clouway.bankapp.core.SessionNotFoundException
import spark.Filter
import spark.Route
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


        val sessionFilter = SecurityFilter(sessionHandler,
                sessionRepo, userRepo)

        val registerController = RegisterController(userRepo, transformer)
        val transactionController = TransactionController(transactionRepo, transformer, sessionFilter)
        val loginController = LoginController(userRepo, sessionRepo, transformer)


        before(Filter { req, res ->
            res.raw().characterEncoding = "UTF-8"
        })

        before(sessionFilter)

        after(Filter {req, res ->
            res.type("application/json")
        })


        get("/user", Route{
            req, res ->
            try{
                return@Route sessionFilter.getUserContext(req.cookie("SID"))
            }catch (e: SessionNotFoundException){
                return@Route Any()
            }
        }, transformer)

        get("/transactions", Route { req, res ->
            return@Route transactionController.doGet(req, res)
        }, transformer)

        get("/active", Route{
            req, res ->
            return@Route sessionRepo.getActiveSessionsCount()
        }, transformer)

        get("/sessions", Route{
            req, res ->
            return@Route listOf(sessionRepo.getActiveSessionsCount(),
                    sessionFilter.isLoggedIn(req.cookie("SID").toString()))
        }, transformer)

        get("/statistics"){
            req, res ->
            val stats = memcacheProvider.service.statistics
            transformer.render(stats)
        }


        post("/transactions", Route{
            req, res ->
            transactionController.doPost(req, res)
        }, transformer)

        post("/register", Route{
            req, res ->
            registerController.doPost(req, res)
        }, transformer)

        post("/login", Route{
            req, res ->
            loginController.doPost(req, res)
        }, transformer)

        post("/logout", Route{
            req, res ->
            sessionFilter.logOut(req.cookie("SID").toString())
        }, transformer)

    }
}