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

package com.jasonmar.ignite.service

import org.apache.ignite.services.{Service, ServiceConfiguration}

trait ServiceConfigBuilder[S<:Service,K] extends ServiceConfig[S,K] {

  require(maxPerNodeCount.getOrElse(0) + totalCount.getOrElse(0) > 0, "At least one of maxPerNodeCount or totalCount must be positive.")

  def buildConfig(): ServiceConfiguration = {
    val cfg = new ServiceConfiguration()
    cfg.setAffinityKey(affinityKey)
    cfg.setCacheName(cacheName)
    cfg.setName(serviceName)
    cfg.setService(service)
    totalCount.foreach(cfg.setTotalCount)
    maxPerNodeCount.foreach(cfg.setMaxPerNodeCount)
    nodeFilter.foreach(cfg.setNodeFilter)
    cfg
  }
}
