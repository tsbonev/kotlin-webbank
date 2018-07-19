package com.clouway.bankapp.adapter.datastore

import com.clouway.bankapp.core.Transaction
import com.clouway.bankapp.core.TransactionRepository

class DatastoreTranslationRepository : TransactionRepository {
    override fun save(transaction: Transaction) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUserTransactions(id: Int, page: Int, pageSize: Int): List<Transaction> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUserTransactions(id: Int): List<Transaction> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}