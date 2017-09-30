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

import org.apache.ignite.configuration.CacheConfiguration

case class WriteBehindConfig[K,V](
  writeBehindBatchSize: Option[Int] = None,
  writeBehindCoalescing: Option[Boolean] = None,
  writeBehindFlushFrequency: Option[Long] = None,
  writeBehindFlushSize: Option[Int] = None
) extends CacheConfigurator[K,V] {
  override def apply(cfg: CacheConfiguration[K, V]): CacheConfiguration[K, V] = {
    writeBehindBatchSize.foreach(cfg.setWriteBehindBatchSize)
    writeBehindCoalescing.foreach(cfg.setWriteBehindCoalescing)
    writeBehindFlushFrequency.foreach(cfg.setWriteBehindFlushFrequency)
    writeBehindFlushSize.foreach(cfg.setWriteBehindFlushSize)
    cfg
  }

}
