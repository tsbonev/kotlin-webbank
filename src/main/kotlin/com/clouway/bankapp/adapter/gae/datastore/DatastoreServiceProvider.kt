package com.clouway.bankapp.adapter.gae.datastore

import com.google.appengine.api.datastore.DatastoreService
import com.google.appengine.api.datastore.DatastoreServiceFactory

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class DatastoreServiceProvider : StoreServiceProvider {

    override val service: DatastoreService
        get() = DatastoreServiceFactory.getDatastoreService()

}