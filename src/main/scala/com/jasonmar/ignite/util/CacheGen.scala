package com.jasonmar.ignite.util

import com.jasonmar.ignite.CacheBuilder
import org.apache.ignite.configuration.CacheConfiguration

/** Builds CacheConfigurations for forward and reverse resolution of ids
  *
  * @param typeName name of the type
  * @param typeClass class tag
  * @tparam V value class type
  */
case class CacheGen[V](typeName: String, typeClass: Class[V]) {
  /** Get Id by Name
    */
  def idCache: CacheBuilder[String,Long] = CacheBuilder.ofIds(typeName + "Ids")

  /** Get Name by Id
    */
  def revCache: CacheBuilder[Long,String] = CacheBuilder.ofNames(typeName + "Names")

  /** Retrieves value type by Id
    */
  def valCache: CacheBuilder[Long,V] = CacheBuilder.ofClass(typeName, typeClass)

  def cfgs: Seq[CacheConfiguration[_,_]] = Seq(idCache.cacheConfiguration, revCache.cacheConfiguration, valCache.cacheConfiguration)
}
