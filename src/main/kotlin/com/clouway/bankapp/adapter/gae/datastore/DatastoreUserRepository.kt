package com.clouway.bankapp.adapter.gae.datastore

import com.clouway.bankapp.adapter.gae.get
import com.clouway.bankapp.adapter.gae.putJson
import com.clouway.bankapp.core.*
import com.google.appengine.api.datastore.*
import com.google.appengine.api.datastore.FetchOptions.Builder.withLimit
import java.util.*
import kotlin.math.absoluteValue

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class DatastoreUserRepository(private val transformer: JsonTransformerWrapper) : UserRepository {

    private val service: DatastoreService
        get() = DatastoreServiceFactory.getDatastoreService()

    private fun andFilter(param: String, value: String): Query.Filter {
        return Query.FilterPredicate(param,
                Query.FilterOperator.EQUAL, value)
    }

    private fun checkIfUserExists(username: String): Boolean {
        return service.prepare(Query("User")
                        .setFilter(andFilter("username", username)))
                        .asList(withLimit(1))
                        .size != 0
    }

    override fun checkPassword(user: User): Boolean {

        val possbileUser = getByUsername(user.username)

        if (possbileUser.isPresent) {
            val retrievedUser = possbileUser.get()

            if (retrievedUser.password == user.password) {
                return true
            }
        }
        return false
    }

    override fun getById(id: Long): Optional<User> {
        val key = KeyFactory.createKey("User", id)
        return service.get(key, User::class.java, transformer)
    }

    override fun deleteById(id: Long) {
        val key = KeyFactory.createKey("User", id)
        service.delete(key)
    }

    override fun update(user: User) {
        val key = KeyFactory.createKey("User", user.id)
        service.putJson(key, user, transformer)
    }

    override fun getByUsername(username: String): Optional<User> {

        val entity = service
                .prepare(Query("User").setKeysOnly()
                        .setFilter(andFilter("username", username)))
                .asSingleEntity() ?: return Optional.empty()

        return service.get(entity.key, User::class.java, transformer)
    }

    override fun registerIfNotExists(registerRequest: UserRegistrationRequest): User {

        val user = User(UUID.randomUUID()
                .leastSignificantBits
                    .absoluteValue,
                registerRequest.username,
                registerRequest.password)

        if (checkIfUserExists(registerRequest.username)) {
            throw UserAlreadyExistsException()
        }
        val userKey = KeyFactory.createKey("User", user.id)

        service.putJson(userKey, user, transformer)

        return user
    }
}