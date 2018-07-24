package com.clouway.bankapp.adapter.memcache

import com.clouway.bankapp.core.Session
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
import kotlin.collections.LinkedHashMap
import org.hamcrest.CoreMatchers.`is` as Is

/**
 * @author tsbonev@gmail.com
 */
class MemcacheSessionTest {

    private val helper = LocalServiceTestHelper(LocalMemcacheServiceTestConfig())
    private val mc = MemcacheServiceFactory.getMemcacheService()
    private val yesterday = Date.from(Instant.now().minusSeconds(86401))

    private val session = Session(1, "123SID", yesterday, true)

    @Before
    fun setUp() {
        helper.setUp()
    }

    @After
    fun tearDown() {
        helper.tearDown()
    }



    @Test
    fun saveSessionInMemcache(){
        val sessionMap = mutableMapOf<String, Any>()
        sessionMap["userId"] = session.userId
        sessionMap["username"] = "John"
        sessionMap["expiresOn"] = session.expiresOn
        sessionMap["isAuthenticated"] = session.isAuthenticated

        mc.put(session.sessionId, sessionMap,
                Expiration.onDate(Date.from(Instant.now().plusSeconds(2000))))

        assertThat(mc.get(session.sessionId) == sessionMap, Is(true))

        val retrievedSession = mc.get(session.sessionId)
        val sessionCache = retrievedSession as? LinkedHashMap<*, *>

        assertThat(sessionCache?.get("userId") == 1L, Is(true))

    }

    @Test
    fun removeSessionFromMemcache(){

        val sessionMap = mutableMapOf<String, Any>()
        sessionMap["userId"] = session.userId
        sessionMap["username"] = "John"
        sessionMap["expiresOn"] = session.expiresOn
        sessionMap["isAuthenticated"] = session.isAuthenticated

        mc.put(session.sessionId, sessionMap
                , Expiration.onDate(Date.from(Instant.now().plusSeconds(2000))))

        mc.delete(session.sessionId)

        assertThat(mc.get(session.sessionId) == null, Is(true))

    }

}