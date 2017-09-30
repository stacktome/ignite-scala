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

import javax.cache.configuration.Factory
import javax.cache.integration.CacheWriter

import org.apache.ignite.configuration.CacheConfiguration

case class WriteThroughConfig[K,V](
  writeBehindConfig: Option[WriteBehindConfig[K,V]] = None,
  cacheWriterFactory: Factory[CacheWriter[K,V]]
) extends CacheConfigurator[K,V] {
  override def apply(cfg: CacheConfiguration[K,V]): CacheConfiguration[K, V] = {
    cfg.setWriteThrough(true)
    cfg.setCacheWriterFactory(cacheWriterFactory)
    writeBehindConfig match {
      case Some(c) => c.apply(cfg)
      case _ => cfg.setWriteBehindEnabled(false)
    }
    cfg
  }
}
