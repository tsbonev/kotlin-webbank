package com.clouway.bankapp.core

import java.util.Date

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
data class Transaction (val id: Long = -1,
                        val operation: Operation,
                        val userId: Int,
                        val date: Date,
                        val amount: Double,
                        val username: String = ""){

    fun getAmountFormatted(): String{
        return String.format("%.2f", amount)
    }

}

