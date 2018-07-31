package com.clouway.bankapp.adapter.spark

import com.clouway.bankapp.core.*
import com.clouway.bankapp.core.security.SessionHandler
import com.clouway.bankapp.core.security.SessionLoader
import org.eclipse.jetty.http.HttpStatus
import org.jmock.AbstractExpectations.returnValue
import org.jmock.AbstractExpectations.throwException
import org.jmock.Expectations
import org.jmock.Mockery
import org.jmock.integration.junit4.JUnitRuleMockery
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import spark.Request
import spark.Response
import java.time.LocalDateTime
import org.hamcrest.CoreMatchers.`is` as Is


/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class TransactionSystemTest {

    @Rule
    @JvmField
    val context: JUnitRuleMockery = JUnitRuleMockery()

    private fun Mockery.expecting(block: Expectations.() -> Unit){
        checking(Expectations().apply(block))
    }

    private val transactionRepo = context.mock(TransactionRepository::class.java)
    private val sessionHandler = context.mock(SessionHandler::class.java)
    private val jsonTransformer = context.mock(JsonTransformerWrapper::class.java)

    private val sessionLoader = SessionLoader(sessionHandler)

    private val testDate = LocalDateTime.now()


    private val listTransactionController = ListTransactionController(transactionRepo)
    private val saveTransactionController = SaveTransactionController(transactionRepo, jsonTransformer)

    private val testSession = Session(1L, "123", testDate, "John", true)
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

        override fun headers(header: String?): String {
            return SID
        }

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
            will(returnValue(testSession))
            oneOf(transactionRepo).getUserTransactions(1L)
            will(returnValue(emptyList<Transaction>()))
        }

        SecuredController(listTransactionController, sessionLoader).handle(req, res)
        assertThat(statusReturn == HttpStatus.OK_200, Is(true))
    }

    @Test
    fun rejectCallWithNoSession(){

        context.expecting {
            oneOf(sessionHandler).getSessionById(SID)
            will(throwException(SessionNotFoundException()))
        }

        SecuredController(listTransactionController, sessionLoader).handle(req, res)
        assertThat(statusReturn == HttpStatus.UNAUTHORIZED_401, Is(true))

    }

    @Test
    fun addTransactionToUserHistory(){

        context.expecting {
            oneOf(jsonTransformer).fromJson(transactionJson, TransactionRequest::class.java)
            will(returnValue(transactionRequest))
            oneOf(sessionHandler).getSessionById(SID)
            will(returnValue(testSession))

            oneOf(transactionRepo).save(transactionRequest)

        }

        SecuredController(saveTransactionController, sessionLoader).handle(req, res)
        assertThat(statusReturn == HttpStatus.CREATED_201, Is(true))
    }

    @Test
    fun rejectAddingToMissingSession(){

        context.expecting {
            oneOf(sessionHandler).getSessionById(SID)
            will(throwException(SessionNotFoundException()))
        }

        SecuredController(saveTransactionController, sessionLoader).handle(req, res)
        assertThat(statusReturn == HttpStatus.UNAUTHORIZED_401, Is(true))

    }

}