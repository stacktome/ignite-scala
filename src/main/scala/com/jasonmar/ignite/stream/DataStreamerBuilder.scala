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

package com.jasonmar.ignite.stream

import org.apache.ignite.lang.IgniteBiInClosure
import org.apache.ignite.stream.StreamVisitor
import org.apache.ignite.{Ignite, IgniteCache, IgniteDataStreamer}

trait DataStreamerBuilder[K,V] extends Visitor[K,V]{
  private def clo: IgniteBiInClosure[IgniteCache[K, V], java.util.Map.Entry[K, V]] = {
    new IgniteBiInClosure[IgniteCache[K, V], java.util.Map.Entry[K, V]] {
      override def apply(e1: IgniteCache[K, V], e2: java.util.Map.Entry[K, V]): Unit = {
        visit(e1, e2.getKey, e2.getValue)
      }
    }
  }

  /**
    * @param ignite Ignite instance
    * @param targetCacheName where to write data added to this streamer
    * @param overwrite if set to false, data for keys that already exist in the cache will be dropped
    * @return
    */
  def buildDataStreamer(
    ignite: Ignite,
    targetCacheName: String,
    overwrite: Boolean = true,
    flushFrequencyMillis: Long = 10000,
    skipStore: Boolean = false
  ): IgniteDataStreamer[K,V] = {
    val s = ignite.dataStreamer[K,V](targetCacheName)
    s.receiver(StreamVisitor.from(clo))
    s.allowOverwrite(overwrite)
    s.autoFlushFrequency(flushFrequencyMillis)
    s.skipStore(skipStore)
    s
  }
}
