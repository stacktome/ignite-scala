/*
 * Copyright 2017 Jason Mar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jasonmar.ignite

import org.apache.ignite.cache.{QueryEntity, QueryIndex}
import org.apache.ignite.configuration.CacheConfiguration

import scala.collection.JavaConverters.asJavaCollectionConverter

object CacheBuilder {
  def builderOf[K,V](cacheName: String, keyCls: Class[K], valueCls: Class[V]): CacheBuilder[K,V] = {
    new CacheBuilder[K,V] {
      override val name: String = cacheName
      override val keyClass: Class[_] = keyCls
      override val valueClass: Class[_] = valueCls
    }
  }

  /** Generates a CacheBuilder which stores numeric ids for a set of strings
    * The numeric id is used in all other caches
    */
  def ofIds(cacheName: String): CacheBuilder[String,Long] = {
    builderOf(cacheName, classOf[String], classOf[Long])
  }

  /** Generates a CacheBuilder which stores names for a set of ids
    * Used as a reverse lookup by id
    */
  def ofNames(cacheName: String): CacheBuilder[Long,String] = {
    builderOf(cacheName, classOf[Long], classOf[String])
  }

  /** Builds a cache for a value type
    * Keyed by a numeric id
    */
  def ofClass[V](cacheName: String, valueClass: Class[V]): CacheBuilder[Long,V] = {
    builderOf(cacheName, classOf[Long], valueClass)
  }
}

trait CacheBuilder[K,V] {
  val name: String
  val keyClass: Class[_]
  val valueClass: Class[_]
  val cfgs: Seq[CacheConfigurator[K,V]] = Seq.empty

  // Used for binary object storage
  val indexFields: Option[Seq[(String, Class[_])]] = None
  val fields: Option[Seq[(String, Class[_])]] = None

  def cacheConfiguration: CacheConfiguration[K,V] = {
    val cfg = new CacheConfiguration[K,V]()
    cfg.setName(name)
    cfg.setEagerTtl(false)
    cfg.setIndexedTypes(keyClass, valueClass)
    cfg.setBackups(1) // have at least 1 backup
    queryEntities.foreach(cfg.setQueryEntities)
    cfgs.foldLeft(cfg){(cfg,subCfg) => subCfg(cfg)}
  }

  protected def queryIndex: Option[java.util.Collection[QueryIndex]] = {
    indexFields.map(_.map(x => new QueryIndex(x._1)).asJavaCollection)
  }

  protected def fieldMap: Option[java.util.LinkedHashMap[String, String]] = {
    if (fields.isDefined){
      val m = new java.util.LinkedHashMap[String,String]()
      fields.getOrElse(Seq.empty).foreach{t => m.put(t._1, t._2.getName)}
      Some(m)
    } else {
      None
    }
  }

  protected def queryEntities: Option[java.util.Collection[QueryEntity]] = {
    if (queryIndex.isDefined && fieldMap.isDefined) {
      val q = new QueryEntity()
      q.setKeyType(keyClass.getName)
      q.setValueType(valueClass.getName)
      fieldMap.foreach(q.setFields)
      queryIndex.foreach(q.setIndexes)
      Some(Seq(q).asJavaCollection)
    } else {
      None
    }
  }
}
