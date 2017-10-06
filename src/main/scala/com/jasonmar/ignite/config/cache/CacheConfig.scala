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

package com.jasonmar.ignite.config.cache

import com.jasonmar.ignite.CacheConfigurator
import org.apache.ignite.cache.{CacheRebalanceMode, CacheWriteSynchronizationMode, PartitionLossPolicy}
import org.apache.ignite.cluster.ClusterNode
import org.apache.ignite.configuration.CacheConfiguration
import org.apache.ignite.lang.IgnitePredicate

case class CacheConfig[K,V](
  backups: Option[Int] = None,
  groupName: Option[String] = None,
  name: Option[String] = None,
  nodeFilter: Option[IgnitePredicate[ClusterNode]] = None,
  maxConcurrentAsyncOperations: Option[Int] = None,
  partitionLossPolicy: Option[PartitionLossPolicy] = None,
  queryParallelism: Option[Int] = None,
  readFromBackup: Option[Boolean] = None,
  rebalanceMode: Option[CacheRebalanceMode] = None,
  sqlFunctionClasses: Option[Seq[Class[_]]] = None,
  sqlSchema: Option[String] = None,
  writeSynchronizationMode: Option[CacheWriteSynchronizationMode] = None
) extends CacheConfigurator[K,V] {
  override def apply(cfg: CacheConfiguration[K,V]): CacheConfiguration[K, V] = {
    backups.foreach(cfg.setBackups)
    groupName.foreach(cfg.setGroupName)
    name.foreach(cfg.setName)
    nodeFilter.foreach(cfg.setNodeFilter)
    maxConcurrentAsyncOperations.foreach(cfg.setMaxConcurrentAsyncOperations)
    partitionLossPolicy.foreach(cfg.setPartitionLossPolicy)
    queryParallelism.foreach(cfg.setQueryParallelism)
    readFromBackup.foreach(cfg.setReadFromBackup)
    rebalanceMode.foreach(cfg.setRebalanceMode)
    sqlFunctionClasses.foreach(cfg.setSqlFunctionClasses(_:_*))
    sqlSchema.foreach(cfg.setSqlSchema)
    writeSynchronizationMode.foreach(cfg.setWriteSynchronizationMode)
    cfg
  }
}
