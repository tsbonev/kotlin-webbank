package com.clouway.bankapp.adapter.transformer

import com.clouway.bankapp.adapter.gae.get
import com.clouway.bankapp.adapter.gae.putJson
import com.clouway.bankapp.core.GsonWrapper
import com.clouway.bankapp.core.User
import com.google.appengine.api.memcache.MemcacheServiceFactory
import org.junit.Rule
import org.hamcrest.CoreMatchers.`is` as Is
import org.junit.Assert.assertThat
import org.junit.Test
import rule.MemcacheRule


/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class CacheTransformerTest {
    @Rule
    @JvmField
    val mcContext: MemcacheRule = MemcacheRule()

    private val user = User(1, "John", "password")
    private val transformer = GsonWrapper()

    @Test
    fun cacheToJsonAndBack(){

        val cache = MemcacheServiceFactory.getMemcacheService()

        cache.putJson("uid", user, transformer)

        assertThat(cache.get("uid", User::class.java, transformer).get() == user, Is(true))
    }
}