package com.clouway.bankapp.adapter.datastore

import com.clouway.bankapp.core.User
import com.clouway.bankapp.core.UserAlreadyExistsException
import com.clouway.bankapp.core.UserRegistrationRequest
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig
import com.google.appengine.tools.development.testing.LocalServiceTestHelper
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertThat
import org.hamcrest.CoreMatchers.`is` as Is

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class UserRepositoryTest {

    private val helper = LocalServiceTestHelper(LocalDatastoreServiceTestConfig())

    private val provider = DatastoreServiceProvider()
    private val userRepo = DatastoreUserRepository(provider)

    private val registerJohn = UserRegistrationRequest("John", "password")
    private val userJohn = User(1, "John", "password")

    @Before
    fun setUp() {
        helper.setUp()
    }

    @After
    fun tearDown() {
        helper.tearDown()
    }

    @Test
    fun shouldRegisterUser(){

        userRepo.registerIfNotExists(registerJohn)

        assertThat(userRepo.getById(1).get() == userJohn, Is(true))

    }

    @Test(expected = UserAlreadyExistsException::class)
    fun shouldNotRegisterUser(){

        userRepo.registerIfNotExists(registerJohn)
        userRepo.registerIfNotExists(registerJohn)

    }

    @Test
    fun shouldGetByUsername(){

        userRepo.registerIfNotExists(registerJohn)

        assertThat(userRepo.getByUsername(registerJohn.username).get() == userJohn,
                Is(true))

    }

    @Test
    fun shouldNotFindByUsername(){

        assertThat(userRepo.getByUsername(userJohn.username).isPresent, Is(false))

    }

    @Test
    fun verifyCorrectPassword(){

        userRepo.registerIfNotExists(UserRegistrationRequest("John", "password"))

        assertThat(userRepo.checkPassword(userJohn), Is(true))
    }

    @Test
    fun invalidateIncorrectPassword(){

        val userJohn = User(1, "John", "incorrect password")

        userRepo.registerIfNotExists(UserRegistrationRequest("John", "password"))

        assertThat(userRepo.checkPassword(userJohn), Is(false))

    }

    @Test
    fun shouldReturnAllUsers(){

        userRepo.registerIfNotExists(UserRegistrationRequest("John", "password"))

        val userDon = User(2, "Don", "password")

        userRepo.registerIfNotExists(UserRegistrationRequest("Don", "password"))

        assertThat(userRepo.getAll() == listOf(userJohn, userDon), Is(true))

    }

    @Test
    fun shouldDeleteUser(){

        userRepo.registerIfNotExists(UserRegistrationRequest("John", "password"))

        assertThat(userRepo.getById(1).isPresent, Is(true))

        userRepo.deleteById(1)
        assertThat(userRepo.getById(1).isPresent, Is(false))

    }

    @Test
    fun shouldUpdateUser(){

        userRepo.registerIfNotExists(UserRegistrationRequest("John", "password"))

        val userJohn = User(1, "Don", "password")

        userRepo.update(userJohn)

        assertThat(userRepo.getById(1).get().username == "Don", Is(true))

    }


}