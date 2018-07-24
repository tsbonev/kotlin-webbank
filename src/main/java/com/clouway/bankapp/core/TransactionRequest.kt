package com.clouway.bankapp.core

/**
 * @author tsbonev@gmail.com
 */
data class TransactionRequest ( val userId: Long = 0, val operation: Operation, val amount: Double)