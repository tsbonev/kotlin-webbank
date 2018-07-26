package com.clouway.bankapp.adapter.memcache

import com.clouway.bankapp.core.Session
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
    private val user = User(1L, "John", "password")

    private val sessionMap = session.toMap()
    private val userMap = user.toMap()

    private val sessionUserPair = Pair(sessionMap, userMap)

    @Before
    fun setUp() {
        helper.setUp()
    }

    @After
    fun tearDown() {
        helper.tearDown()
    }



    @Test
    fun saveSessionUserPairInMemcache(){

        mc.put(session.sessionId, sessionUserPair,
                Expiration.onDate(Date.from(Instant.now().plusSeconds(2000))))

        assertThat(mc.get(session.sessionId) == sessionUserPair, Is(true))

        val retrievedSession = mc.get(session.sessionId) as? Pair<*, *>
        val sessionCache = retrievedSession?.first as? LinkedHashMap<*, *>
        val userCache = retrievedSession?.second as? LinkedHashMap<*, *>

        assertThat(sessionCache?.get("userId") == 1L, Is(true))
        assertThat(userCache?.get("id") == 1L, Is(true))

    }

    @Test
    fun removeSessionUserPairFromMemcache(){

        mc.put(session.sessionId, sessionUserPair
                , Expiration.onDate(Date.from(Instant.now().plusSeconds(2000))))

        mc.delete(session.sessionId)

        assertThat(mc.get(session.sessionId) == null, Is(true))

    }

}