package com.clouway.bankapp.adapter.datastore

import com.google.appengine.api.datastore.Query

interface QueryTemplate {

    fun execute(query: Query)

    fun <T> executeQuery(query: Query, rowMapper: RowMapper<T>): MutableList<T>

}