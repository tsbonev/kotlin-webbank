package com.clouway.bankapp.core

interface TransactionRepository {

    fun save(transaction: Transaction)

    fun getUserTransactions(id: Int, page: Int, pageSize: Int): List<Transaction>
    fun getUserTransactions(id: Int): List<Transaction>

}