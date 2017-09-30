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

import com.jasonmar.ignite.config.grid.IgniteConfigurator
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.{Ignite, Ignition}

object Init {
  /**
    *
    * @param configs a collection of IgniteConfigurators which will be combined into a single IgniteConfiguration
    * @param cacheBuilders a collection of CacheBuilders to build CacheConfigurations and IgniteCaches
    * @return
    */
  def apply(
    configs: Seq[IgniteConfigurator] = Seq.empty,
    cacheBuilders: Seq[CacheBuilder[_,_]] = Seq.empty
  ): Ignite = {
    val cfg = configs.foldLeft(new IgniteConfiguration()){(cfg, configurator) => configurator(cfg)}
    Ignition.start(cfg)
    implicit val ignite: Ignite = Ignition.ignite()
    cacheBuilders.map(_.build())
    ignite
  }
}
