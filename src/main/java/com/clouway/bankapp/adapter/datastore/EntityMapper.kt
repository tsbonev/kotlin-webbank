package com.clouway.bankapp.adapter.datastore

import com.google.appengine.api.datastore.Entity

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
interface EntityMapper<T> {

    fun map(obj: T): Entity

}