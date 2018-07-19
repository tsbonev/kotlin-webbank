package com.clouway.bankapp.adapter.datastore

import com.google.appengine.api.datastore.DatastoreService
import com.google.appengine.api.datastore.DatastoreServiceFactory

class DatastoreServiceProvider : ServiceProvider {
    override fun get(): DatastoreService {
        return DatastoreServiceFactory.getDatastoreService()
    }
}