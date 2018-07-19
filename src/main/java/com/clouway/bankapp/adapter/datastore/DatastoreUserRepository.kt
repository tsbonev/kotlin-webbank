package com.clouway.bankapp.adapter.datastore

import com.clouway.bankapp.core.User
import com.clouway.bankapp.core.UserRegistrationRequest
import com.clouway.bankapp.core.UserRepository
import java.util.*

class DatastoreUserRepository : UserRepository {
    override fun getById(id: Int): Optional<User> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAll(): List<User> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteById(id: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(user: User) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getByUsername(username: String): Optional<User> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerIfNotExists(registerRequest: UserRegistrationRequest): User {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}