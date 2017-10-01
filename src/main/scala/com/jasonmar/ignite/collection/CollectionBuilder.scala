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

package com.jasonmar.ignite.collection

import org.apache.ignite.cache.{CacheAtomicityMode, CacheMode}
import org.apache.ignite.cluster.ClusterNode
import org.apache.ignite.configuration.CollectionConfiguration
import org.apache.ignite.lang.IgnitePredicate

trait CollectionBuilder[T] {
  val name: String
  val atomicityMode: Option[CacheAtomicityMode] = None
  val backups: Option[Int] = None
  val cacheMode: Option[CacheMode] = None
  val collocated: Option[Boolean] = None
  val groupName: Option[String] = None
  val nodeFilter: Option[IgnitePredicate[ClusterNode]] = None
  val offHeapMaxMemory: Option[Long] = None

  def buildConfig(): CollectionConfiguration = {
    val cfg = new CollectionConfiguration()
    atomicityMode.foreach(cfg.setAtomicityMode)
    backups.foreach(cfg.setBackups)
    cacheMode.foreach(cfg.setCacheMode)
    collocated.foreach(cfg.setCollocated)
    groupName.foreach(cfg.setGroupName)
    nodeFilter.foreach(cfg.setNodeFilter)
    offHeapMaxMemory.foreach(cfg.setOffHeapMaxMemory)
    cfg
  }

}
