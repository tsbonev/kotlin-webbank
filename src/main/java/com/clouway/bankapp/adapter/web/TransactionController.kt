package com.clouway.bankapp.adapter.web

import com.clouway.bankapp.core.Transaction
import com.clouway.bankapp.core.TransactionRepository
import com.clouway.bankapp.core.TransactionRequest
import spark.Request
import spark.Response

/**
 * @author tsbonev@gmail.com
 */
class TransactionController(private val transactionRepo: TransactionRepository,
                            private val transformer: JsonTransformer) {

    fun doGet(req: Request, res: Response): List<Transaction> {
        return  transactionRepo.getUserTransactions(req.params(":id").toLong())
    }

    fun doPost(req: Request, res: Response){
        res.type("application/json")
        transactionRepo.save(transformer.from(req.body(), TransactionRequest::class.java))
        res.status(201)
    }

}