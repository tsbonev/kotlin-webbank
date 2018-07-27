package com.clouway.bankapp.adapter.gae.memcache

import com.google.appengine.api.memcache.MemcacheService

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
interface CacheServiceProvider {

    val service: MemcacheService

}