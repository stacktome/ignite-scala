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

import javax.cache.Cache.Entry
import javax.cache.event.CacheEntryEvent

import org.apache.ignite.IgniteCache
import org.apache.ignite.cache.query.QueryCursor

trait QueryRunner[K,V] extends ContinuousQueryBuilder[K,V] {
  protected def handleEvent(event: CacheEntryEvent[_<:K,_<:V]): Unit

  /** Runs ContinousQuery against provided IgniteCache and executes processEntry for each entry returned */
  def runQuery(cache: IgniteCache[K,V]): QueryCursor[Entry[K,V]] = {
    cache.query(buildQuery())
  }
}
