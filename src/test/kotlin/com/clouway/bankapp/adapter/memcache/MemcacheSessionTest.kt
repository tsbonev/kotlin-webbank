package com.clouway.bankapp.adapter.memcache

import com.clouway.bankapp.adapter.gae.memcache.MemcacheSessionHandler
import com.clouway.bankapp.core.GsonWrapper
import com.clouway.bankapp.core.Session
import com.clouway.bankapp.core.SessionNotFoundException
import com.clouway.bankapp.core.security.SessionHandler
import org.jmock.Expectations
import org.jmock.Mockery
import org.jmock.integration.junit4.JUnitRuleMockery
import org.junit.Test
import org.junit.Assert.assertThat
import org.junit.Rule
import rule.MemcacheRule
import org.jmock.AbstractExpectations.*
import java.time.LocalDateTime
import org.hamcrest.CoreMatchers.`is` as Is

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class MemcacheSessionTest {

    @Rule
    @JvmField
    val helper: MemcacheRule = MemcacheRule()

    private fun Mockery.expecting(block: Expectations.() -> Unit){
            checking(Expectations().apply(block))
    }

    @Rule
    @JvmField
    val context: JUnitRuleMockery = JUnitRuleMockery()

    private val yesterday = LocalDateTime.now().minusDays(4)

    private val transformerWrapper = GsonWrapper()
    private val persistentSessionHandler = context.mock(SessionHandler::class.java)
    private val cachedSessionHandler = MemcacheSessionHandler(persistentSessionHandler, transformerWrapper)

    private val session = Session(1, "123SID", yesterday, "John",true)


    @Test
    fun saveSessionInMemcache(){

        context.expecting {
            oneOf(persistentSessionHandler).saveSession(session)
        }

        cachedSessionHandler.saveSession(session)

        val retrievedSession = cachedSessionHandler.getSessionById(session.sessionId)
        
        assertThat(retrievedSession.sessionId == session.sessionId, Is(true))
    }

    @Test(expected = SessionNotFoundException::class)
    fun removeSessionFromMemcache(){

        context.expecting {
            oneOf(persistentSessionHandler).saveSession(session)
            oneOf(persistentSessionHandler).terminateSession(session.sessionId)
            oneOf(persistentSessionHandler).getSessionById(session.sessionId)
            will(throwException(SessionNotFoundException()))
        }

        cachedSessionHandler.saveSession(session)

        cachedSessionHandler.terminateSession(session.sessionId)

        cachedSessionHandler.getSessionById(session.sessionId)
    }

}