package com.clouway.bankapp.adapter.datastore

import com.clouway.bankapp.core.Session
import com.clouway.bankapp.core.SessionRepository
import java.sql.Timestamp
import java.util.*

class DatastoreSessionRepository : SessionRepository {

    override fun registerSession(session: Session) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun refreshSession(session: Session) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun terminateSession(sessionId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteSessionsExpiringAfter(timestamp: Timestamp) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getSessionAvailableAt(sessionId: String, timestamp: Timestamp): Optional<Session> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getActiveSessionsCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}