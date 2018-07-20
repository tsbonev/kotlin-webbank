package com.clouway.bankapp.adapter.datastore

import com.google.appengine.api.datastore.DatastoreService

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
interface ServiceProvider {

    fun get(): DatastoreService

}