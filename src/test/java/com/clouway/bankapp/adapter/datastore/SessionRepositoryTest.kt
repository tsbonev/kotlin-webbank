package com.clouway.bankapp.adapter.datastore

import com.clouway.bankapp.core.Session
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig
import com.google.appengine.tools.development.testing.LocalServiceTestHelper
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertThat
import java.time.Instant
import java.util.*
import org.hamcrest.CoreMatchers.`is` as Is

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class SessionRepositoryTest {

    private val helper = LocalServiceTestHelper(LocalDatastoreServiceTestConfig())

    private val provider = DatastoreServiceProvider()
    private val sessionRepo = DatastoreSessionRepository(provider)
    private val tomorrow = Date.from(Instant.now().plusSeconds(86401))
    private val now = Date.from(Instant.now())
    private val yesterday = Date.from(Instant.now().minusSeconds(86401))

    private val session = Session(1, "123", yesterday, true)

    @Before
    fun setUp() {
        helper.setUp()
    }

    @After
    fun tearDown() {
        helper.tearDown()
    }

    @Test
    fun shouldRegisterSession(){

        val session = Session(1, "123", tomorrow, true)

        sessionRepo.registerSession(session)

        assertThat(sessionRepo.getSessionAvailableAt("123", now).isPresent, Is(true))

    }

    @Test
    fun shouldNotGetExpiredSession(){

        sessionRepo.registerSession(session)

        assertThat(sessionRepo.getSessionAvailableAt("123", now).isPresent, Is(false))

    }

    @Test
    fun shouldDeleteExpiringSession(){

        sessionRepo.registerSession(session)
        sessionRepo.deleteSessionsExpiringBefore(now)

        assertThat(sessionRepo.getSessionAvailableAt("123", now).isPresent, Is(false))

    }

    @Test
    fun shouldTerminateSession(){

        sessionRepo.registerSession(session)
        sessionRepo.terminateSession(session.sessionId)

        assertThat(sessionRepo.getSessionAvailableAt("123", now).isPresent, Is(false))

    }

    @Test
    fun shouldCountActiveSessions(){

        val sessionActive1 = Session(1, "123", tomorrow, true)
        val sessionActive2 = Session(2, "1234", tomorrow, true)
        val sessionInactive = Session(3, "12345", yesterday, true)

        sessionRepo.registerSession(sessionActive1)
        sessionRepo.registerSession(sessionActive2)
        sessionRepo.registerSession(sessionInactive)

        assertThat(sessionRepo.getActiveSessionsCount() == 2, Is(true))

    }

    @Test
    fun shouldRefreshSession(){

        sessionRepo.registerSession(session)
        sessionRepo.refreshSession(session)

        assertThat(sessionRepo.getSessionAvailableAt(session.sessionId, now).isPresent, Is(true))

    }

}