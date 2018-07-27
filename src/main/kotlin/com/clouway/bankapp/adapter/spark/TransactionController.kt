package com.clouway.bankapp.adapter.spark

import com.clouway.bankapp.core.security.SecurityFilter
import com.clouway.bankapp.core.SessionNotFoundException
import com.clouway.bankapp.core.Transaction
import com.clouway.bankapp.core.TransactionRepository
import com.clouway.bankapp.core.TransactionRequest
import org.eclipse.jetty.http.HttpStatus
import spark.Request
import spark.Response

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class TransactionController(private val transactionRepo: TransactionRepository,
                            private val transformer: JsonTransformer,
                            private val securityFilter: SecurityFilter) {

    fun doGet(req: Request, res: Response): List<Transaction> {
        return try{
            val transactions = transactionRepo
                    .getUserTransactions(securityFilter.getUserContext(req.cookie("SID")).id)
            res.status(HttpStatus.OK_200)
            transactions
        }catch (e: SessionNotFoundException){
            res.status(HttpStatus.UNAUTHORIZED_401)
            emptyList()
        }
    }

    fun doPost(req: Request, res: Response){
        res.type("application/json")

        try{
            val transactionRequestFromJson = transformer.from(req.body(), TransactionRequest::class.java)
            val completeTransactionRequest = TransactionRequest(
                    securityFilter.getUserContext(req.cookie("SID")).id,
                    transactionRequestFromJson.operation,
                    transactionRequestFromJson.amount)

            transactionRepo.save(completeTransactionRequest)
            res.status(HttpStatus.CREATED_201)
        }catch (e: SessionNotFoundException){
            res.status(HttpStatus.UNAUTHORIZED_401)
        }

    }

}