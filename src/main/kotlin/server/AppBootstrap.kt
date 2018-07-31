package server
import com.clouway.bankapp.adapter.gae.datastore.DatastoreSessionHandler
import com.clouway.bankapp.adapter.gae.datastore.DatastoreSessionRepository
import com.clouway.bankapp.adapter.gae.datastore.DatastoreTransactionRepository
import com.clouway.bankapp.adapter.gae.datastore.DatastoreUserRepository
import com.clouway.bankapp.adapter.gae.memcache.MemcacheSessionHandler
import com.clouway.bankapp.adapter.spark.*
import com.clouway.bankapp.core.GsonWrapper
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


        val transformerWrapper = GsonWrapper()
        val responseTransformer= JsonResponseTransformer(transformerWrapper)
        val userRepo = DatastoreUserRepository(transformerWrapper)
        val sessionRepo = DatastoreSessionRepository(transformerWrapper)
        val transactionRepo = DatastoreTransactionRepository(transformerWrapper)

        val sessionLoader = SessionLoader(
                MemcacheSessionHandler(
                DatastoreSessionHandler(sessionRepo),
                        transformerWrapper))

        val securityFilter = SecurityFilter()

        val registerController = RegisterController(userRepo, transformerWrapper)
        val listTransactionController = ListTransactionController(transactionRepo)
        val saveTransactionController = SaveTransactionController(transactionRepo, transformerWrapper)
        val loginController = LoginController(userRepo, sessionLoader, transformerWrapper)
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
                responseTransformer)

        get("/transactions",
                SecuredController(listTransactionController, sessionLoader),
                responseTransformer)

        get("/active", Route{
            req, res ->
            return@Route sessionRepo.getActiveSessionsCount()
        }, responseTransformer)

        get("/statistics"){
            req, res ->
            val service = MemcacheServiceFactory.getMemcacheService()
            val stats = service.statistics
            responseTransformer.render(stats)
        }

        post("/transactions",
                SecuredController(saveTransactionController, sessionLoader),
                responseTransformer)

        post("/login",
                AppController(loginController),
                responseTransformer)

        post("/register",
                AppController(registerController),
                responseTransformer)

        post("/logout", SecuredController(logoutController, sessionLoader),
                responseTransformer)

    }
}