package com.clouway.bankapp.adapter.spark

import com.clouway.bankapp.core.*
import org.eclipse.jetty.http.HttpStatus
import org.jmock.AbstractExpectations.*
import org.jmock.Expectations
import org.jmock.Mockery
import org.junit.Rule
import org.junit.Test
import org.jmock.integration.junit4.JUnitRuleMockery
import org.junit.Assert.assertThat
import spark.Request
import spark.Response
import java.time.Instant
import java.util.*
import org.hamcrest.CoreMatchers.`is` as Is


/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class LoginSystemTest {

    @Rule
    @JvmField
    val context: JUnitRuleMockery = JUnitRuleMockery()

    private fun Mockery.expecting(block: Expectations.() -> Unit){
        checking(Expectations().apply(block))
    }

    private val userRepo = context.mock(UserRepository::class.java)
    private val sessionRepo = context.mock(SessionRepository::class.java)
    private val transformer = JsonTransformer()

    private val testDate = Date.from(Instant.now())

    private val loginController = LoginController(userRepo,
            sessionRepo,
            transformer,
            getExpirationDate = {testDate})

    private val registerController = RegisterController(userRepo, transformer)

    private val loginJSON = """
        {
        "username": "John",
        "password": "password"
        }
    """.trimIndent()

    private val testUser = User(1L, "John", "password")
    private val possibleUser = Optional.of(testUser)
    private val SID = "123"
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
    fun logInWithCorrectCredentials(){

        val testSession = Session(
                1,
                SID,
                testDate,
                true
        )

        context.expecting {
            oneOf(userRepo).getByUsername("John")
            will(returnValue(possibleUser))
            oneOf(sessionRepo).registerSession(testSession)
        }

        loginController.doPost(req, res)
        assertThat(statusReturn == HttpStatus.OK_200, Is(true))

    }

    @Test
    fun rejectInvalidLoginCredentials(){
        val user = User(1L, "John", "wrong pass")
        val possibleUser = Optional.of(user)

        context.expecting {
            oneOf(userRepo).getByUsername("John")
            will(returnValue(possibleUser))
        }

        loginController.doPost(req, res)
        assertThat(statusReturn == HttpStatus.UNAUTHORIZED_401, Is(true))
    }

    @Test
    fun userNotFoundInLogin(){
        val possibleUser = Optional.empty<User>()

        context.expecting {
            oneOf(userRepo).getByUsername("John")
            will(returnValue(possibleUser))
        }

        loginController.doPost(req, res)
        assertThat(statusReturn == HttpStatus.UNAUTHORIZED_401, Is(true))
    }

    @Test
    fun registerUserForFirstTime(){

        val userRegistrationRequest = UserRegistrationRequest("John", "password")

        context.expecting {
            oneOf(userRepo)
                    .registerIfNotExists(userRegistrationRequest)
        }

        registerController.doPost(req, res)
        assertThat(statusReturn == HttpStatus.CREATED_201, Is(true))

    }


    @Test
    fun rejectRegisteringTakenUsername(){

        val userRegistrationRequest = UserRegistrationRequest("John", "password")

        context.expecting {
            oneOf(userRepo)
                    .registerIfNotExists(userRegistrationRequest)
            will(throwException(UserAlreadyExistsException()))
        }

        registerController.doPost(req, res)
        assertThat(statusReturn == HttpStatus.BAD_REQUEST_400, Is(true))

    }

}