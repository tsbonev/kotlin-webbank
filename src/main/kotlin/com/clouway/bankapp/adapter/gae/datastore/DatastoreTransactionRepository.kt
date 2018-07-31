package com.clouway.bankapp.adapter.gae.datastore

import com.clouway.bankapp.adapter.gae.get
import com.clouway.bankapp.adapter.gae.putJson
import com.clouway.bankapp.core.*
import com.clouway.bankapp.core.Transaction
import com.google.appengine.api.datastore.*
import com.google.appengine.api.datastore.FetchOptions.Builder.withLimit
import java.time.LocalDateTime
import java.util.*
import kotlin.math.absoluteValue

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class DatastoreTransactionRepository(private val transformer: JsonTransformerWrapper,
                                     private val limit: Int = 100) : TransactionRepository {

    private val service: DatastoreService
        get() = DatastoreServiceFactory.getDatastoreService()

    private fun retrieveUsername(userId: Long): String {
        val userKey = KeyFactory.createKey("User", userId)
        return service.get(userKey, User::class.java, transformer).get().username
    }

    private fun andFilter(param: String, value: Long): Query.Filter {
        return Query.FilterPredicate(param,
                Query.FilterOperator.EQUAL, value)
    }

    private fun getTransactionList(id: Long, pageSize: Int = limit, offset: Int = 0): List<Transaction> {

        val transactionEntities = service
                .prepare(Query("Transaction")
                        .setFilter(andFilter("userId", id)))
                .asList(withLimit(pageSize)
                        .offset(offset))

        val transactionList = mutableListOf<Transaction>()

        transactionEntities.forEach {
            transactionList.add(transformer.fromJson(it.properties["content"].toString(), Transaction::class.java))
        }

        return transactionList
    }

    override fun save(transactionRequest: TransactionRequest) {

        val transactionKey = KeyFactory.createKey("Transaction",
                UUID.randomUUID()
                        .leastSignificantBits
                        .absoluteValue)

        val transaction = Transaction(
                transactionKey.id,
                transactionRequest.operation,
                transactionRequest.userId,
                LocalDateTime.now(),
                transactionRequest.amount,
                retrieveUsername(transactionRequest.userId)
        )
        service.putJson(transactionKey, transaction, transformer)
    }

    override fun getUserTransactions(id: Long, page: Int, pageSize: Int): List<Transaction> {
        return getTransactionList(id, pageSize, (page - 1) * pageSize)
    }

    override fun getUserTransactions(id: Long): List<Transaction> {
        return getTransactionList(id)
    }
}