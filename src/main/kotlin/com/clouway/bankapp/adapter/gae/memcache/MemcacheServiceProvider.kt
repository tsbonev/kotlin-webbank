package com.clouway.bankapp.adapter.gae.memcache

import com.google.appengine.api.memcache.MemcacheService
import com.google.appengine.api.memcache.MemcacheServiceFactory

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class MemcacheServiceProvider : CacheServiceProvider {
    override val service: MemcacheService
            get() = MemcacheServiceFactory.getMemcacheService()
}