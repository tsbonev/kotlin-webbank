package com.clouway.bankapp.adapter.datastore

import com.clouway.bankapp.core.Operation
import com.clouway.bankapp.core.Transaction
import com.clouway.bankapp.core.TransactionRepository
import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.datastore.FetchOptions.Builder.withLimit
import com.google.appengine.api.datastore.Query
import java.time.Instant
import java.util.*

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class DatastoreTransactionRepository(private val provider: ServiceProvider,
                                     private val limit: Int = 100) : TransactionRepository {


    private val transactionEntityMapper = object: EntityMapper<Transaction>{
        override fun map(obj: Transaction): Entity {
            val entity = Entity("Transaction", obj.id)

            entity.setProperty("userId", obj.userId)
            entity.setProperty("operation", obj.operation.name)
            entity.setProperty("date", obj.date)
            entity.setProperty("amount", obj.amount)
            entity.setProperty("username", obj.username)

            return entity
        }
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
                    Integer.parseInt(entity.properties["userId"].toString()),
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

    private fun getTranscationEntityList(id: Long, pageSize: Int = limit, offset: Int = 0): List<Entity>{

        return provider.get()
                .prepare(Query("Transaction").setFilter(andFilter("userId", id)))
                .asList(withLimit(pageSize)
                        .offset(offset))

    }

    override fun save(transaction: Transaction) {
        provider.get().put(transactionEntityMapper.map(transaction))
    }

    override fun getUserTransactions(id: Long, page: Int, pageSize: Int): List<Transaction> {

        val transactionEntityList = getTranscationEntityList(id, pageSize, (page - 1) * pageSize)
        val transactionList = mutableListOf<Transaction>()

        for(entity in transactionEntityList){
            transactionList.add(transactionRowMapper.map(entity))
        }

        return transactionList

    }

    override fun getUserTransactions(id: Long): List<Transaction> {
        val transactionEntityList = getTranscationEntityList(id)
        val transactionList = mutableListOf<Transaction>()

        for(entity in transactionEntityList){
            transactionList.add(transactionRowMapper.map(entity))
        }

        return transactionList
    }
}