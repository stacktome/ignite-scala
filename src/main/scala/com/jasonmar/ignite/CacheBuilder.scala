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

trait CacheBuilder[K,V] {
  val name: String
  val keyClass: Class[_]
  val valueClass: Class[_]
  val indexFields: Option[Seq[(String, Class[_])]] = None
  val fields: Option[Seq[(String, Class[_])]] = None
  val cfgs: Seq[CacheConfigurator[K,V]] = Seq.empty

  def cacheConfiguration: CacheConfiguration[K,V] = {
    val cfg = new CacheConfiguration[K,V]()
    cfg.setName(name)
    indexFields.foreach{x => cfg.setIndexedTypes(x.map(_._2).distinct:_*)}

    cfg.setQueryEntities(queryEntities)
    cfgs.foldLeft(cfg){(cfg,subCfg) => subCfg(cfg)}
  }

  protected def queryIndex: java.util.Collection[QueryIndex] = {
    indexFields.getOrElse(Seq.empty).map(t => new QueryIndex(t._1)).asJavaCollection
  }

  protected def fieldMap: java.util.LinkedHashMap[String, String] = {
    val m = new java.util.LinkedHashMap[String,String]()
    fields.getOrElse(Seq.empty).foreach{t => m.put(t._1, t._2.getName)}
    m
  }

  protected def queryEntities: java.util.Collection[QueryEntity] = {
    val q = new QueryEntity()
    q.setKeyType(keyClass.getName)
    q.setValueType(valueClass.getName)
    q.setFields(fieldMap)
    q.setIndexes(queryIndex)
    Seq(q).asJavaCollection
  }
}
