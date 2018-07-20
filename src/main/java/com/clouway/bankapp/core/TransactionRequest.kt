package com.clouway.bankapp.core

/**
 * @author tsbonev@gmail.com
 */
data class TransactionRequest (val userId: Long, val operation: Operation, val amount: Double)