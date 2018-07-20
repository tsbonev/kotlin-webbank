package com.clouway.bankapp.core

import java.util.*

interface UserRepository {

    fun getById(id: Long): Optional<User>

    fun getAll(): List<User>

    fun deleteById(id: Long)

    fun update(user: User)

    fun getByUsername(username: String): Optional<User>

    @Throws(UserAlreadyExistsException::class)
    fun registerIfNotExists(registerRequest: UserRegistrationRequest)

    fun checkPassword(user: User): Boolean

}