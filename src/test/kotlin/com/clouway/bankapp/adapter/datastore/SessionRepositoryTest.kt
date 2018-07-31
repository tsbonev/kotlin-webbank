package com.clouway.bankapp.adapter.datastore

import com.clouway.bankapp.adapter.gae.datastore.DatastoreSessionRepository
import com.clouway.bankapp.core.GsonWrapper
import com.clouway.bankapp.core.Session
import org.junit.Test
import org.junit.Assert.assertThat
import org.junit.Rule
import rule.DatastoreRule
import java.time.LocalDateTime
import org.hamcrest.CoreMatchers.`is` as Is

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class SessionRepositoryTest {

    @Rule
    @JvmField
    val helper: DatastoreRule= DatastoreRule()

    private val jsonTransformer = GsonWrapper()

    private val tomorrow =  LocalDateTime.of(2018, 8, 3, 10, 36, 23, 905000000)
    private val now = LocalDateTime.of(2018, 8, 2, 10, 36, 23, 905000000)
    private val yesterday =  LocalDateTime.of(2018, 8, 1, 10, 36, 23, 905000000)
    private val sessionRepo = DatastoreSessionRepository(jsonTransformer, instant = now, sessionRefreshDays = 1)

    private val activeSession = Session(1, "123", tomorrow, "John", true)
    private val expiredSession = Session(1, "1234", yesterday, "Don", false)

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

        sessionRepo.registerSession(expiredSession)
        sessionRepo.registerSession(activeSession)
        
        assertThat(sessionRepo.getActiveSessionsCount() == 1, Is(true))

    }

    @Test
    fun shouldRefreshSession(){

        sessionRepo.registerSession(expiredSession)
        sessionRepo.refreshSession(expiredSession)

        assertThat(sessionRepo.getSessionAvailableAt(expiredSession.sessionId, now).get().expiresOn, Is(tomorrow))

    }

    @Test
    fun shouldReturnEmptyWhenNotFound(){
        assertThat(sessionRepo.
                getSessionAvailableAt("fakeSID", LocalDateTime.now())
                .isPresent, Is(false))
    }

}