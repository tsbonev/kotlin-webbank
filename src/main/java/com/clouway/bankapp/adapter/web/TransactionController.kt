package com.clouway.bankapp.adapter.web

import com.clouway.bankapp.adapter.web.filter.SessionFilter
import com.clouway.bankapp.core.Transaction
import com.clouway.bankapp.core.TransactionRepository
import com.clouway.bankapp.core.TransactionRequest
import org.eclipse.jetty.http.HttpStatus
import spark.Request
import spark.Response

/**
 * @author tsbonev@gmail.com
 */
class TransactionController(private val transactionRepo: TransactionRepository,
                            private val transformer: JsonTransformer,
                            private val sessionFilter: SessionFilter) {

    fun doGet(req: Request, res: Response): List<Transaction> {
        return  transactionRepo.getUserTransactions(sessionFilter.getUserContext(req.cookie("SID")).id)
    }

    fun doPost(req: Request, res: Response){
        res.type("application/json")
        transactionRepo.save(transformer.from(req.body(), TransactionRequest::class.java))
        res.status(HttpStatus.CREATED_201)
    }

}