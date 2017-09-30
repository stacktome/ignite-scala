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

import javax.cache.event.CacheEntryEvent

import org.apache.ignite.cache.query.ContinuousQuery

trait ContinuousQueryBuilder[K,V] {
  /** Executed on server for all entries in cache when query is received */
  protected val filter: Option[(K,V) => Boolean]

  /** This will be executed client-side each time an event passes the filter */
  protected val onUpdate: Option[(java.lang.Iterable[CacheEntryEvent[_<:K,_<:V]]) => Unit]

  /** Executes on server before sending events to client */
  protected val eventFilter: Option[(CacheEntryEvent[_<:K,_<:V]) => Boolean]

  def buildQuery(): ContinuousQuery[K,V]
}
