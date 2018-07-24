package com.clouway.bankapp.adapter.web

import com.clouway.bankapp.core.SessionRepository
import com.clouway.bankapp.core.UserRepository
import org.jmock.Expectations
import org.jmock.Mockery
import org.junit.Rule
import org.junit.Test
import org.jmock.integration.junit4.JUnitRuleMockery


/**
 * @author tsbonev@gmail.com
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

    private val loginController = LoginController(userRepo, sessionRepo, transformer)

    @Test
    fun logInWithCorrectCredentials(){

    }

}