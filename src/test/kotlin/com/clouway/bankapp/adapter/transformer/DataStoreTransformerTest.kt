package com.clouway.bankapp.adapter.transformer
import com.clouway.bankapp.adapter.gae.get
import com.clouway.bankapp.adapter.gae.putJson
import com.clouway.bankapp.core.GsonWrapper
import com.clouway.bankapp.core.User
import com.google.appengine.api.datastore.DatastoreServiceFactory
import com.google.appengine.api.datastore.KeyFactory
import org.hamcrest.CoreMatchers.`is` as Is
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import rule.DatastoreRule

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class DataStoreTransformerTest {

    @Rule
    @JvmField
    val context: DatastoreRule = DatastoreRule()

    private val user = User(1L, "John", "password")
    private val transformer = GsonWrapper()

    @Test
    fun storeToJsonAndBack(){

        val store = DatastoreServiceFactory.getDatastoreService()

        val key = KeyFactory.createKey("User", 123)

        store.putJson(key, user, transformer)

        assertThat(store.get(key, User::class.java, transformer).get() == user, Is(true))
    }
}