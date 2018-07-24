package com.clouway.bankapp.adapter.memcache

import com.google.appengine.api.memcache.MemcacheService
import com.google.appengine.api.memcache.MemcacheServiceFactory

/**
 * @author tsbonev@gmail.com
 */
class MemcacheServiceProvider : ServiceProvider {
    override val service: MemcacheService
            get() = MemcacheServiceFactory.getMemcacheService()
}