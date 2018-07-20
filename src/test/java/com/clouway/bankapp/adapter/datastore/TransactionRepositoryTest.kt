package com.clouway.bankapp.adapter.datastore

import com.clouway.bankapp.core.*
import com.google.appengine.api.datastore.Entity
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
class TransactionRepositoryTest {

    private val helper = LocalServiceTestHelper(LocalDatastoreServiceTestConfig())

    private val provider = DatastoreServiceProvider()
    private val transactionRepo = DatastoreTransactionRepository(provider)
    private val transaction = TransactionRequest(1, Operation.DEPOSIT, 200.0)

    @Before
    fun setUp() {
        helper.setUp()
        val userEntity = Entity("User", 1)
        userEntity.setProperty("username", "John")
        userEntity.setProperty("password", "password")
        provider.get().put(userEntity)
    }

    @After
    fun tearDown() {
        helper.tearDown()
    }

    @Test
    fun shouldSaveTransaction(){
        transactionRepo.save(transaction)

        assertThat(transactionRepo.getUserTransactions(1).isNotEmpty(), Is(true))
    }

    @Test
    fun shouldReturnEmptyTransactionList(){

        assertThat(transactionRepo.getUserTransactions(1).isEmpty(), Is(true))

    }

    @Test
    fun shouldPaginateTransactions(){

        for(x in 1..10)
            transactionRepo.save(
                    TransactionRequest(
                            1,
                            Operation.DEPOSIT,
                            200.0
                    )
            )

        assertThat(transactionRepo
                .getUserTransactions(1, 1, 2)
                .size == 2, Is(true))

        assertThat(transactionRepo
                .getUserTransactions(1, 3, 2)[0]
                .id == 5L, Is(true))
    }

}