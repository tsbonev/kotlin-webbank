package com.clouway.bankapp.adapter.web

import com.clouway.bankapp.core.Operation
import com.clouway.bankapp.core.TransactionRequest
import org.junit.Test
import kotlin.test.assertTrue

/**
 * @author tsbonev@gmail.com
 */
class TransactionSystemTest {

    val transformer = JsonTransformer()

    @Test
    fun parseOverloadedRequestToTransaction(){

        val json = """
            {
            "operation":"WITHDRAW",
            "amount":"200.0"
            }
        """.trimIndent()

        val testTransaction = TransactionRequest(operation = Operation.WITHDRAW, amount =  200.0)
        val parsedTransaction = transformer.from(json, TransactionRequest::class.java)
        
        println(testTransaction)
        println(parsedTransaction)

    }

}