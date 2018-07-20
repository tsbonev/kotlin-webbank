package com.clouway.bankapp.adapter.datastore

import com.clouway.bankapp.core.Operation
import com.clouway.bankapp.core.Transaction
import com.clouway.bankapp.core.TransactionRepository
import com.clouway.bankapp.core.TransactionRequest
import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.datastore.FetchOptions.Builder.withLimit
import com.google.appengine.api.datastore.KeyFactory
import com.google.appengine.api.datastore.Query
import java.time.Instant
import java.util.*

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class DatastoreTransactionRepository(private val provider: ServiceProvider,
                                     private val limit: Int = 100) : TransactionRepository {

    private val transactionRequestEntityMapper = object: EntityMapper<TransactionRequest>{
        override fun map(obj: TransactionRequest): Entity {
            val entity = Entity("Transaction")

            entity.setProperty("userId", obj.userId)
            entity.setProperty("operation", obj.operation.name)
            entity.setProperty("date", Date.from(Instant.now()))
            entity.setProperty("amount", obj.amount)
            entity.setProperty("username", retrieveUsername(obj.userId))

            return entity
        }
    }

    private fun retrieveUsername(userId: Long): String{
        val userKey = KeyFactory.createKey("User", userId)
        return provider.get().get(userKey).properties["username"].toString()
    }

    private fun andFilter(param: String, value: Long): Query.Filter{
        return Query.FilterPredicate(param,
                Query.FilterOperator.EQUAL, value)
    }

    private val transactionRowMapper = object: RowMapper<Transaction>{
        override fun map(entity: Entity): Transaction{
            return Transaction(
                entity.key.id,
                    Operation.valueOf(entity.properties["operation"].toString()),
                    entity.properties["userId"].toString().toLong(),
                    convertToDate(entity.properties["date"]),
                    entity.properties["amount"].toString().toDouble(),
                    entity.properties["username"].toString()
            )
        }
    }

    private fun convertToDate(obj: Any?): Date {

        if(obj is Date){
            return obj
        }
        return Date.from(Instant.now())
    }

    private fun getTransactionEntityList(id: Long, pageSize: Int = limit, offset: Int = 0): List<Entity>{

        return provider.get()
                .prepare(Query("Transaction").setFilter(andFilter("userId", id)))
                .asList(withLimit(pageSize)
                        .offset(offset))

    }

    override fun save(transaction: TransactionRequest) {
        provider.get().put(transactionRequestEntityMapper.map(transaction))
    }

    override fun getUserTransactions(id: Long, page: Int, pageSize: Int): List<Transaction> {

        val transactionEntityList = getTransactionEntityList(id, pageSize, (page - 1) * pageSize)
        val transactionList = mutableListOf<Transaction>()

        for(entity in transactionEntityList){
            transactionList.add(transactionRowMapper.map(entity))
        }

        return transactionList

    }

    override fun getUserTransactions(id: Long): List<Transaction> {
        val transactionEntityList = getTransactionEntityList(id)
        val transactionList = mutableListOf<Transaction>()

        for(entity in transactionEntityList){
            transactionList.add(transactionRowMapper.map(entity))
        }

        return transactionList
    }
}