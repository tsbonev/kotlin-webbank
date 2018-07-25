package com.clouway.bankapp.adapter.datastore

import com.clouway.bankapp.core.User
import com.clouway.bankapp.core.UserAlreadyExistsException
import com.clouway.bankapp.core.UserRegistrationRequest
import com.clouway.bankapp.core.UserRepository
import com.google.appengine.api.datastore.*
import com.google.appengine.api.datastore.FetchOptions.Builder.withLimit
import java.util.*

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class DatastoreUserRepository(private val provider: StoreServiceProvider,
                              private val limit: Int = 100) : UserRepository {

    private val registrationEntityMapper = object: EntityMapper<UserRegistrationRequest>{
        override fun map(obj: UserRegistrationRequest): Entity {
            val entity = Entity("User")
            entity.setProperty("username", obj.username)
            entity.setProperty("password", obj.password)
            return entity
        }
    }

    private val userEntityMapper = object: EntityMapper<User>{
        override fun map(obj: User): Entity {
            val entity = Entity("User")
            entity.setProperty("username", obj.username)
            entity.setProperty("password", obj.password)
            return entity
        }
    }

    private val userRowMapper = object: RowMapper<User>{
        override fun map(entity: Entity): User {
            return User(
                    entity.key.id,
                    entity.properties["username"] as String,
                    entity.properties["password"] as String
            )
        }
    }

    private fun andFilter(param: String, value: String): Query.Filter{
        return Query.FilterPredicate(param,
                Query.FilterOperator.EQUAL, value)
    }


    private fun checkIfUserExists(username: String): Boolean{

        if(provider.service
                        .prepare(Query("User")
                                .setFilter(andFilter("username", username)))
                        .asList(withLimit(1))
                        .size != 0){
            return true
        }

        return false
    }

    override fun checkPassword(user: User): Boolean {

        val possbileUser = getByUsername(user.username)

        if(possbileUser.isPresent){
            val retrievedUser = possbileUser.get()

            if(retrievedUser.password == user.password){
                return true
            }
        }
        return false
    }

    override fun getById(id: Long): Optional<User> {
        val key = KeyFactory.createKey("User", id)

        return try{
            val entity = provider.service.get(key)
            Optional.of(userRowMapper.map(entity))
        }catch (e: EntityNotFoundException){
            Optional.empty()
        }
    }

    override fun getAll(): List<User> {
        val entityList = provider.service
                .prepare(Query("User")).asList(withLimit(limit))

        val userList = mutableListOf<User>()

        for (entity in entityList){
            userList.add(userRowMapper.map(entity))
        }

        return userList

    }

    override fun deleteById(id: Long) {

        val key = KeyFactory.createKey("User", id)
        provider.service.delete(key)

    }

    override fun update(user: User) {
        val key = KeyFactory.createKey("User", user.id)
        val dsUser = provider.service.get(key)
        dsUser.setPropertiesFrom(userEntityMapper.map(user))
        provider.service.put(dsUser)

    }

    override fun getByUsername(username: String): Optional<User> {

        val entity = provider.service
                .prepare(Query("User")
                        .setFilter(andFilter("username", username)))
                .asSingleEntity()

        if(entity != null){
            return Optional.of(userRowMapper.map(entity))
        }

        return Optional.empty()

    }

    override fun registerIfNotExists(registerRequest: UserRegistrationRequest) {
        val entity = registrationEntityMapper.map(registerRequest)

        if(checkIfUserExists(registerRequest.username)){
            throw UserAlreadyExistsException()
        }

        provider.service.put(entity)

    }
}