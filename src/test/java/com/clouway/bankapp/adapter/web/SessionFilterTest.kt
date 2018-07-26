package com.clouway.bankapp.adapter.web

import org.jmock.AbstractExpectations.*
import com.clouway.bankapp.adapter.memcache.SessionHandler
import com.clouway.bankapp.adapter.web.filter.SessionFilter
import com.clouway.bankapp.core.*
import org.eclipse.jetty.http.HttpStatus
import org.jmock.Expectations
import org.jmock.Mockery
import org.junit.Rule
import org.jmock.integration.junit4.JUnitRuleMockery
import org.junit.Assert.assertThat
import org.junit.Test
import spark.Request
import spark.Response
import java.time.Instant
import java.util.*
import org.hamcrest.CoreMatchers.`is` as Is


/**
 * @author tsbonev@gmail.com
 */
class SessionFilterTest {

    @Rule
    @JvmField
    val context: JUnitRuleMockery = JUnitRuleMockery()

    private fun Mockery.expecting(block: Expectations.() -> Unit){
        checking(Expectations().apply(block))
    }

    private val userRepo = context.mock(UserRepository::class.java)
    private val sessionRepo = context.mock(SessionRepository::class.java)
    private val sessionHandler = context.mock(SessionHandler::class.java)
    private val sessionFilter = SessionFilter(sessionHandler, sessionRepo, userRepo,
            getCurrentTime = {
                testDate
            })

    private val testDate = Date.from(Instant.now())

    private val loginJSON = """
        {
        "username": "John",
        "password": "password"
        }
    """.trimIndent()

    private val SID = "123"

    private val testSession = Session(1L,
            SID,
            testDate,
            true)

    private val testUser = User(1L, "John", "password")
    private var statusReturn: Int = 0

    private val req = object: Request(){
        override fun body(): String {
            return loginJSON
        }
        override fun cookie(name: String): String{
            return SID
        }
    }

    private val res = object: Response() {
        override fun status(statusCode: Int){
            statusReturn = statusCode
        }
    }

    @Test
    fun sessionPresentInMemcache(){

        context.expecting {
            oneOf(sessionHandler).getSessionById("123")
            will(returnValue(Pair(testSession, testUser)))
        }

        sessionFilter.handle(req, res)
        assertThat(statusReturn == HttpStatus.FOUND_302, Is(true))

    }

    @Test
    fun sessionPresentInDB(){

        context.expecting {
            oneOf(sessionHandler).getSessionById("123")
            will(returnValue(null))
            oneOf(sessionRepo).getSessionAvailableAt(SID, testDate)
            will(returnValue(Optional.of(testSession)))
            oneOf(userRepo).getById(1L)
            will(returnValue(Optional.of(testUser)))
            oneOf(sessionHandler).saveSessionInCache(testSession, testUser)
            oneOf(sessionRepo).refreshSession(testSession)
        }

        sessionFilter.handle(req, res)
        assertThat(statusReturn == HttpStatus.FOUND_302, Is(true))

    }

    @Test
    fun sessionNotPresent(){

        context.expecting {
            oneOf(sessionHandler).getSessionById("123")
            will(returnValue(null))
            oneOf(sessionRepo).getSessionAvailableAt(SID, testDate)
            will(returnValue(Optional.empty<Session>()))
        }

        sessionFilter.handle(req, res)
        assertThat(statusReturn == HttpStatus.NOT_FOUND_404, Is(true))
    }

    @Test
    fun userNotPresent(){

        context.expecting {
            oneOf(sessionHandler).getSessionById("123")
            will(returnValue(null))
            oneOf(sessionRepo).getSessionAvailableAt(SID, testDate)
            will(returnValue(Optional.of(testSession)))
            oneOf(userRepo).getById(1L)
            will(returnValue(Optional.empty<User>()))
            oneOf(sessionRepo).terminateSession(SID)
        }

        sessionFilter.handle(req, res)
        assertThat(statusReturn == HttpStatus.NOT_FOUND_404, Is(true))
    }

}