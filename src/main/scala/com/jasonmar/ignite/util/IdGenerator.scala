package com.jasonmar.ignite.util

import org.apache.ignite.{Ignite, IgniteAtomicSequence, IgniteCache}

/** Populates forward and reverse id caches
  * https://apacheignite.readme.io/docs/id-generator
  */
case class IdGenerator(
  typeName: String,
  ignite: Ignite
) {
  protected val seq: IgniteAtomicSequence = ignite.atomicSequence(typeName, 0L, true)
  val rev: IgniteCache[Long,String] = ignite.getOrCreateCache[Long,String](typeName + "Names")
  val ids: IgniteCache[String,Long] = ignite.getOrCreateCache[String,Long](typeName + "Ids")

  def getId(key: String): Long = {
    if (ids.containsKey(key)) {
      ids.get(key)
    } else {
      val id = seq.incrementAndGet()
      ids.put(key, id)
      rev.put(id, key)
      id
    }
  }
}
