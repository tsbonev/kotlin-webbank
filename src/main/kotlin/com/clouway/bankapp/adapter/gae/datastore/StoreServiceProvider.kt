package com.clouway.bankapp.adapter.gae.datastore

import com.google.appengine.api.datastore.DatastoreService

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
interface StoreServiceProvider {

    val service: DatastoreService

}