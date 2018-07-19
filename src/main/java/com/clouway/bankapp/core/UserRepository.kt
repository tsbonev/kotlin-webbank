package com.clouway.bankapp.core

import java.util.*

interface UserRepository {

    fun getById(id: Int): Optional<User>

    fun getAll(): List<User>

    fun deleteById(id: Int)

    fun update(user: User)

    fun getByUsername(username: String): Optional<User>

    @Throws(UserAlreadyExistsException::class)
    fun registerIfNotExists(registerRequest: UserRegistrationRequest): User

}