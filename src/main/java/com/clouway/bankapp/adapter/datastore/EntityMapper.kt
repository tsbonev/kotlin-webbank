package com.clouway.bankapp.adapter.datastore

import com.google.appengine.api.datastore.Entity

interface EntityMapper<T> {

    fun map(obj: T): Entity

}