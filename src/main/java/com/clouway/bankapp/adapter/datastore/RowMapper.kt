package com.clouway.bankapp.adapter.datastore

import com.google.appengine.api.datastore.Entity
import java.sql.SQLException

interface RowMapper<T> {

    @Throws(SQLException::class)
    fun map(entityList: List<Entity>): T

}