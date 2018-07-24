package com.clouway.bankapp.adapter.memcache

import com.google.appengine.api.memcache.MemcacheService

/**
 * @author tsbonev@gmail.com
 */
interface CacheServiceProvider {

    val service: MemcacheService

}