package com.jasonmar.ignite.util

import org.apache.ignite.{Ignite, IgniteAtomicSequence, IgniteCache}

/** Wraps an IgniteCache and autoincrements key
  *
  * @param ignite Ignite instance
  * @param cache IgniteCache to be autoincremented
  * @tparam V value type
  */
case class AutoIncrementingIgniteCache[V](ignite: Ignite, cache: IgniteCache[Long,V]) {
  protected val seq: IgniteAtomicSequence = ignite.atomicSequence(cache.getName + "AutoIncrId", 0L, true)
  def put(v: V): Long = {
    val key = seq.incrementAndGet()
    cache.put(key, v)
    key
  }
}
