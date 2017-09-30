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

import java.lang
import javax.cache.configuration.Factory
import javax.cache.event.{CacheEntryEvent, CacheEntryEventFilter, CacheEntryUpdatedListener}

import org.apache.ignite.cache.query.{ContinuousQuery, ScanQuery}
import org.apache.ignite.lang.{IgniteAsyncCallback, IgniteBiPredicate}

trait ScanQueryBuilder[K,V] extends ContinuousQueryBuilder[K,V]{

  private def pred: Option[IgniteBiPredicate[K, V]] = {
    filter.map{f =>
      new IgniteBiPredicate[K, V] {
        override def apply(e1: K, e2: V): Boolean = f(e1, e2)
      }
    }
  }

  private def scanQuery = new ScanQuery[K,V](pred.orNull)

  private def eventFilterFactory: Option[Factory[CacheEntryEventFilter[K,V]]] = {
    eventFilter.map{f =>
      new Factory[CacheEntryEventFilter[K,V]]{
        override def create(): CacheEntryEventFilter[K, V] = {
          new CacheEntryEventFilter[K,V]{
            override def evaluate(event: CacheEntryEvent[_<:K,_<:V]): Boolean = {
              f.apply(event)
            }
          }
        }
      }
    }
  }

  private def listener: Option[CacheEntryUpdatedListener[K,V]] = onUpdate.map{ f =>
    new CacheEntryUpdatedListener[K,V]{
      @IgniteAsyncCallback
      override def onUpdated(events: lang.Iterable[CacheEntryEvent[_ <: K, _ <: V]]): Unit = {
        f.apply(events)
      }
    }
  }

  /** Builds ContinuousQuery */
  override def buildQuery(): ContinuousQuery[K,V] = {
    val q = new ContinuousQuery[K,V]().setInitialQuery(scanQuery)
    listener.foreach(q.setLocalListener)
    eventFilterFactory.foreach(q.setRemoteFilterFactory)
    q
  }
}
