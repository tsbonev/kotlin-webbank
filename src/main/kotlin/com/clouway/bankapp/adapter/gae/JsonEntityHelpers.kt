package com.clouway.bankapp.adapter.gae

import com.clouway.bankapp.core.*
import com.google.appengine.api.datastore.DatastoreService
import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.datastore.EntityNotFoundException
import com.google.appengine.api.datastore.Key
import com.google.appengine.api.memcache.MemcacheService
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
fun <T> MemcacheService.get(key: String, typeOfT: Class<T>, transformer: JsonTransformerWrapper): Optional<T> {
    val entity = this.get(key) ?: return Optional.empty()
    return Optional.of(transformer.fromJson(entity as String, typeOfT))
}

fun <T> DatastoreService.get(key: Key, typeOfT: Class<T>, transformer: JsonTransformerWrapper): Optional<T> {
    return try {
        val entity = this.get(key)
        val entityContent = entity.properties["content"] as String
        Optional.of(transformer.fromJson(entityContent, typeOfT))
    } catch (e: EntityNotFoundException) {
        Optional.empty()
    }
}

fun MemcacheService.putJson(key: String, obj: Any, transformer: JsonTransformerWrapper) {
    val objToJson = transformer.toJson(obj)
    this.put(key, objToJson)
}

fun DatastoreService.putJson(key: Key, obj: Any, transformer: JsonTransformerWrapper) {
    val objToJson = transformer.toJson(obj)
    val entity = Entity(key)
    attachObjectInformation(entity, obj)

    entity.setProperty("content", objToJson)
    this.put(entity)
}

fun LocalDateTime.toUtilDate(): Date{
    return Date.from(this.atZone(ZoneId.systemDefault()).toInstant())
}

private fun attachObjectInformation(entity: Entity, obj: Any){

    when(obj){
        is User -> entity.setProperty("username", obj.username)
        is Session -> entity.setProperty("expiresOn", obj.expiresOn.toUtilDate())
        is Transaction -> entity.setProperty("userId", obj.userId)
    }
}
