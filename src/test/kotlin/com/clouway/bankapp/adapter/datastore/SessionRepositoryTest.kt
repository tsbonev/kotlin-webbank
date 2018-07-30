package com.clouway.bankapp.adapter.datastore

import com.clouway.bankapp.adapter.gae.datastore.DatastoreSessionRepository
import com.clouway.bankapp.core.Session
import org.junit.Test
import org.junit.Assert.assertThat
import org.junit.Rule
import rule.DatastoreRule
import java.time.Instant
import java.util.*
import org.hamcrest.CoreMatchers.`is` as Is

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class SessionRepositoryTest {

    @Rule
    @JvmField
    val helper: DatastoreRule= DatastoreRule()

    private val sessionRepo = DatastoreSessionRepository()
    private val tomorrow = Date.from(Instant.now().plusSeconds(86401))
    private val now = Date.from(Instant.now())
    private val yesterday = Date.from(Instant.now().minusSeconds(86401))

    private val activeSession = Session(1, "123", tomorrow, "John", true)
    private val expiredSession = Session(1, "123", yesterday, "John", true)

    @Test
    fun shouldRegisterSession(){

        sessionRepo.registerSession(activeSession)

        assertThat(sessionRepo.getSessionAvailableAt("123", now).isPresent, Is(true))

    }

    @Test
    fun shouldNotGetExpiredSession(){

        sessionRepo.registerSession(expiredSession)

        assertThat(sessionRepo.getSessionAvailableAt("123", now).isPresent, Is(false))

    }

    @Test
    fun shouldDeleteExpiringSession(){

        sessionRepo.registerSession(expiredSession)
        sessionRepo.deleteSessionsExpiringBefore(now)

        assertThat(sessionRepo.getSessionAvailableAt("123", now).isPresent, Is(false))

    }

    @Test
    fun shouldTerminateSession(){

        sessionRepo.registerSession(activeSession)
        sessionRepo.terminateSession(activeSession.sessionId)

        assertThat(sessionRepo.getSessionAvailableAt("123", now).isPresent, Is(false))

    }

    @Test
    fun shouldCountActiveSessions(){

        val sessionActive1 = Session(1, "123", tomorrow, "John", true)
        val sessionActive2 = Session(2, "1234", tomorrow, "John",true)
        val sessionInactive = Session(3, "12345", yesterday, "John", true)

        sessionRepo.registerSession(sessionActive1)
        sessionRepo.registerSession(sessionActive2)
        sessionRepo.registerSession(sessionInactive)

        assertThat(sessionRepo.getActiveSessionsCount() == 2, Is(true))

    }

    @Test
    fun shouldRefreshSession(){

        sessionRepo.registerSession(expiredSession)
        sessionRepo.refreshSession(expiredSession)

        assertThat(sessionRepo.getSessionAvailableAt(expiredSession.sessionId, now).isPresent, Is(true))

    }

}