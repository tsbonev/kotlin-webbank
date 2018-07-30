package server
import com.clouway.bankapp.adapter.gae.datastore.DatastoreSessionHandler
import com.clouway.bankapp.adapter.gae.datastore.DatastoreSessionRepository
import com.clouway.bankapp.adapter.gae.datastore.DatastoreTransactionRepository
import com.clouway.bankapp.adapter.gae.datastore.DatastoreUserRepository
import com.clouway.bankapp.adapter.gae.memcache.MemcacheSessionHandler
import com.clouway.bankapp.adapter.spark.*
import com.clouway.bankapp.core.security.SecurityFilter
import com.clouway.bankapp.core.security.SessionLoader
import com.google.appengine.api.memcache.MemcacheServiceFactory
import spark.Filter
import spark.Route
import spark.Spark.*
import spark.kotlin.before
import spark.servlet.SparkApplication

class AppBootstrap : SparkApplication{
    override fun init() {

        val transformer= JsonTransformer()

        val userRepo = DatastoreUserRepository()
        val sessionRepo = DatastoreSessionRepository()
        val transactionRepo = DatastoreTransactionRepository()

        val sessionLoader = SessionLoader(MemcacheSessionHandler(DatastoreSessionHandler(sessionRepo)))

        val securityFilter = SecurityFilter()

        val registerController = RegisterController(userRepo, transformer)
        val listTransactionController = ListTransactionController(transactionRepo)
        val saveTransactionController = SaveTransactionController(transactionRepo, transformer)
        val loginController = LoginController(userRepo, sessionLoader, transformer)
        val userController = UserController()
        val logoutController = LogoutController(sessionLoader)


        before(Filter { req, res ->
            res.raw().characterEncoding = "UTF-8"
        })

        before(securityFilter)

        after(Filter {req, res ->
            res.type("application/json")
        })


        get("/user",
                SecuredController(userController, sessionLoader),
                transformer)

        get("/transactions",
                SecuredController(listTransactionController, sessionLoader),
                transformer)

        get("/active", Route{
            req, res ->
            return@Route sessionRepo.getActiveSessionsCount()
        }, transformer)

        get("/statistics"){
            req, res ->
            val service = MemcacheServiceFactory.getMemcacheService()
            val stats = service.statistics
            transformer.render(stats)
        }

        post("/transactions",
                SecuredController(saveTransactionController, sessionLoader),
                transformer)

        post("/login",
                AppController(loginController),
                transformer)

        post("/register",
                AppController(registerController),
                transformer)

        post("/logout", SecuredController(logoutController, sessionLoader),
                transformer)

    }
}