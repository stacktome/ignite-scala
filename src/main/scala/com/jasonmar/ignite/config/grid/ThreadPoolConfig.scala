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

package com.jasonmar.ignite.config.grid

import org.apache.ignite.configuration.IgniteConfiguration

case class ThreadPoolConfig (
  asyncCallbackPoolSize: Option[Int] = None,
  dataStreamerThreadPoolSize: Option[Int] = None,
  igfsThreadPoolSize: Option[Int] = None,
  managementThreadPoolSize: Option[Int] = None,
  peerClassLoadingThreadPoolSize: Option[Int] = None,
  publicThreadPoolSize: Option[Int] = None,
  queryThreadPoolSize: Option[Int] = None,
  rebalanceThreadPoolSize: Option[Int] = None, // rebalanceThreadPoolSize
  serviceThreadPoolSize: Option[Int] = None,
  stripedPoolSize: Option[Int] = None, // stripedPoolSize
  systemThreadPoolSize: Option[Int] = None,
  utilityCachePoolSize: Option[Int] = None
) extends IgniteConfigurator {
  override def apply(cfg: IgniteConfiguration): IgniteConfiguration = {
    stripedPoolSize.foreach(cfg.setStripedPoolSize)
    publicThreadPoolSize.foreach(cfg.setPublicThreadPoolSize)
    serviceThreadPoolSize.foreach(cfg.setServiceThreadPoolSize)
    systemThreadPoolSize.foreach(cfg.setSystemThreadPoolSize)
    asyncCallbackPoolSize.foreach(cfg.setAsyncCallbackPoolSize)
    managementThreadPoolSize.foreach(cfg.setManagementThreadPoolSize)
    peerClassLoadingThreadPoolSize.foreach(cfg.setPeerClassLoadingThreadPoolSize)
    igfsThreadPoolSize.foreach(cfg.setIgfsThreadPoolSize)
    dataStreamerThreadPoolSize.foreach(cfg.setDataStreamerThreadPoolSize)
    utilityCachePoolSize.foreach(cfg.setUtilityCachePoolSize)
    queryThreadPoolSize.foreach(cfg.setQueryThreadPoolSize)
    rebalanceThreadPoolSize.foreach(cfg.setRebalanceThreadPoolSize)
    cfg
  }
}
