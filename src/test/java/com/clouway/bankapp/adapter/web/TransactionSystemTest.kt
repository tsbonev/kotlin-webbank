package com.clouway.bankapp.adapter.web

import com.clouway.bankapp.adapter.memcache.SessionHandler
import com.clouway.bankapp.adapter.web.filter.SessionFilter
import com.clouway.bankapp.core.*
import org.eclipse.jetty.http.HttpStatus
import org.jmock.AbstractExpectations.returnValue
import org.jmock.Expectations
import org.jmock.Mockery
import org.jmock.integration.junit4.JUnitRuleMockery
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import spark.Request
import spark.Response
import java.time.Instant
import java.util.*
import org.hamcrest.CoreMatchers.`is` as Is


/**
 * @author tsbonev@gmail.com
 */
class TransactionSystemTest {

    @Rule
    @JvmField
    val context: JUnitRuleMockery = JUnitRuleMockery()

    private fun Mockery.expecting(block: Expectations.() -> Unit){
        checking(Expectations().apply(block))
    }

    private val transactionRepo = context.mock(TransactionRepository::class.java)
    private val userRepo = context.mock(UserRepository::class.java)
    private val sessionHandler = context.mock(SessionHandler::class.java)
    private val sessionRepo = context.mock(SessionRepository::class.java)

    private val testDate = Date.from(Instant.now())

    private val sessionFilter = SessionFilter(sessionHandler, sessionRepo, userRepo, getCurrentTime = {testDate})
    private val transformer = JsonTransformer()


    private val transactionController = TransactionController(transactionRepo,
            transformer, sessionFilter)

    private val testSession = Session(1L, "123", testDate, true)
    private val testUser = User(1L, "John", "password")
    private val sessionUserPair = Pair(testSession, testUser)
    private val SID = "123"
    private var statusReturn: Int = 0

    private val transactionRequest = TransactionRequest(1L, Operation.WITHDRAW, 200.0)
    private val transactionJson = """
        {
        "operation": "WITHDRAW",
        "amount": "200.0"
        }
    """.trimIndent()

    private val req = object: Request(){
        override fun body(): String {
            return transactionJson
        }
        override fun cookie(name: String): String{
            return SID
        }
    }

    private val res = object: Response() {

        override fun type(contentType: String?) {

        }

        override fun status(statusCode: Int){
            statusReturn = statusCode
        }
    }

    @Test
    fun getUserTransactions(){

        context.expecting {
            oneOf(sessionHandler).getSessionById(SID)
            will(returnValue(sessionUserPair))
            oneOf(transactionRepo).getUserTransactions(1L)
            will(returnValue(emptyList<Transaction>()))
        }

        transactionController.doGet(req, res)
        assertThat(statusReturn == HttpStatus.FOUND_302, Is(true))
    }

    @Test
    fun rejectCallWithNoSession(){

        context.expecting {
            oneOf(sessionHandler).getSessionById(SID)
        }

        transactionController.doGet(req, res)
        assertThat(statusReturn == HttpStatus.UNAUTHORIZED_401, Is(true))

    }

    @Test
    fun addTransactionToUserHistory(){

        context.expecting {
            oneOf(sessionHandler).getSessionById(SID)
            will(returnValue(sessionUserPair))

            oneOf(transactionRepo).save(transactionRequest)

        }

        transactionController.doPost(req, res)
        assertThat(statusReturn == HttpStatus.CREATED_201, Is(true))
    }

    @Test
    fun rejectAddingToMissingSession(){

        context.expecting {
            oneOf(sessionHandler).getSessionById(SID)
        }

        transactionController.doPost(req, res)
        assertThat(statusReturn == HttpStatus.UNAUTHORIZED_401, Is(true))

    }

}