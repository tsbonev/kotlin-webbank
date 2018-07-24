package com.clouway.bankapp.adapter.datastore

import com.google.appengine.api.datastore.DatastoreService
import com.google.appengine.api.datastore.DatastoreServiceFactory

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class DatastoreServiceProvider : ServiceProvider {

    override val service: DatastoreService
        get() = DatastoreServiceFactory.getDatastoreService()

}