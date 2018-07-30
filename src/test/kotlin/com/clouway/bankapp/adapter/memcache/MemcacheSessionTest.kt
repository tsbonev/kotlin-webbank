package com.clouway.bankapp.adapter.memcache

import com.clouway.bankapp.adapter.gae.datastore.DatastoreSessionHandler
import com.clouway.bankapp.adapter.gae.datastore.DatastoreSessionRepository
import com.clouway.bankapp.adapter.gae.memcache.MemcacheSessionHandler
import com.clouway.bankapp.core.Session
import com.clouway.bankapp.core.SessionNotFoundException
import com.clouway.bankapp.core.User
import com.google.appengine.api.memcache.Expiration
import com.google.appengine.api.memcache.MemcacheServiceFactory
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig
import com.google.appengine.tools.development.testing.LocalServiceTestHelper
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.util.*
import org.junit.Assert.assertThat
import org.junit.Rule
import rule.MemcacheRule
import kotlin.collections.LinkedHashMap
import org.hamcrest.CoreMatchers.`is` as Is

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class MemcacheSessionTest {

    @Rule
    @JvmField
    val helper: MemcacheRule = MemcacheRule()

    private val yesterday = Date.from(Instant.now().minusSeconds(86401))

    private val sessionRepo = DatastoreSessionRepository()
    private val persistentSessionHandler = DatastoreSessionHandler(sessionRepo)
    private val cachedSessionHandler = MemcacheSessionHandler(persistentSessionHandler)

    private val session = Session(1, "123SID", yesterday, "John",true)


    @Test
    fun saveSessionInMemcache(){

        cachedSessionHandler.saveSession(session)

        val retrievedSession = cachedSessionHandler.getSessionById(session.sessionId)

        assertThat(retrievedSession == session, Is(true))

    }

    @Test(expected = SessionNotFoundException::class)
    fun removeSessionFromMemcache(){

        cachedSessionHandler.saveSession(session)

        cachedSessionHandler.terminateSession(session.sessionId)

        cachedSessionHandler.getSessionById(session.sessionId)
    }

}