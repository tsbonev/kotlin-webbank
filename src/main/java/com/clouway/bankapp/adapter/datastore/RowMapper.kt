package com.clouway.bankapp.adapter.datastore

import com.google.appengine.api.datastore.Entity
import java.sql.SQLException

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
interface RowMapper<T> {

    fun map(entity: Entity): T

}