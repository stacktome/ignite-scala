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
import org.apache.ignite.lang.IgnitePredicate

case class IgniteCollection[T](
  override val name: String,
  override val atomicityMode: Option[CacheAtomicityMode] = None,
  override val backups: Option[Int] = None,
  override val cacheMode: Option[CacheMode] = None,
  override val collocated: Option[Boolean] = None,
  override val groupName: Option[String] = None,
  override val nodeFilter: Option[IgnitePredicate[ClusterNode]] = None,
  override val offHeapMaxMemory: Option[Long] = None
) extends SetBuilder[T] with QueueBuilder[T]
