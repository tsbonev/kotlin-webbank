package com.clouway.bankapp.adapter.datastore

import com.google.appengine.api.datastore.DatastoreService

interface ServiceProvider {

    fun get(): DatastoreService

}