package com.clouway.bankapp.core

import java.sql.Date

data class Transaction (val id: Int = -1,
                        val operation: Operation,
                        val userId: Int,
                        val date: Date,
                        val amount: Double,
                        val username: String = ""){

    fun getAmountFormatted(): String{
        return String.format("%.2f", amount)
    }

}

