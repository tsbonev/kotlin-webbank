package com.clouway.bankapp.adapter.datastore

import com.google.appengine.api.datastore.Query
import com.google.appengine.api.datastore.FetchOptions.Builder.withLimit

class DatastoreQueryTemplate(val limit: Int = 100, val provider: ServiceProvider) : QueryTemplate {

    override fun execute(query: Query) {
        val ds = provider.get()
        ds.prepare(query)
    }

    override fun <T> executeQuery(query: Query, rowMapper: RowMapper<T>): MutableList<T> {
        val ds = provider.get()

        val entityList = ds.prepare(query).asList(withLimit(limit))

        return mutableListOf(rowMapper.map(entityList))

    }
}