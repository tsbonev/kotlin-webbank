package com.clouway.bankapp.adapter.datastore

import com.clouway.bankapp.core.Operation
import com.clouway.bankapp.core.Transaction
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

    private val transaction = Transaction(1,
            Operation.DEPOSIT,
            1,
            Date.from(Instant.now()),
            200.0,
            "John"
            )

    @Before
    fun setUp() {
        helper.setUp()
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
                    Transaction(x.toLong(),
                            Operation.DEPOSIT,
                            1,
                            Date.from(Instant.now()),
                            200.0,
                            "John"
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