package com.clouway.bankapp.core

import java.time.Instant
import java.util.Date

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
data class Transaction (val id: Long = -1,
                        val operation: Operation,
                        val userId: Long,
                        val date: Date = Date.from(Instant.now()),
                        val amount: Double,
                        val username: String = ""){

    fun getAmountFormatted(): String{
        return String.format("%.2f", amount)
    }
}

