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
import com.clouway.bankapp.core.SessionNotFoundException
import spark.Filter
import spark.Route
import spark.Spark.*
import spark.kotlin.before
import spark.kotlin.get
import spark.kotlin.post
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

        before(Filter { req, res ->
            res.raw().characterEncoding = "UTF-8"
        })

        before(sessionFilter)

        get("/user", Route{
            req, res ->
            try{
                return@Route sessionFilter.getUserContext(req.cookie("SID"))
            }catch (e: SessionNotFoundException){
                return@Route "Not logged in"
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

        post("/logout", Route{
            req, res ->
            sessionFilter.logOut(req.cookie("SID").toString())
        }, transformer)


        post("/transactions/save", "application/json", Route{
            req, res ->
            transactionController.doPost(req, res)
        }, transformer)

        post("/register", "application/json", Route{
            req, res ->
            registerController.doPost(req, res)
        }, transformer)

        post("/login", "application/json", Route{
            req, res ->
            loginController.doPost(req, res)
        }, transformer)

    }
}