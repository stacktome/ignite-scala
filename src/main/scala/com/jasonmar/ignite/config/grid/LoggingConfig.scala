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

import javax.management.MBeanServer

import com.jasonmar.ignite.IgniteConfigurator
import org.apache.ignite.IgniteLogger
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.lifecycle.LifecycleBean

case class LoggingConfig (
  gridLogger: Option[IgniteLogger] = None, // log
  lifecycleBeans: Option[Seq[LifecycleBean]] = None, // lifecycleBeans
  includeEventTypes: Option[Seq[Int]] = None, // inclEvtTypes
  mBeanServer: Option[MBeanServer] = None, // mbeanSrv
  metricsExpireTime: Option[Long] = None, // metricsExpTime
  metricsHistorySize: Option[Int] = None, // metricsHistSize
  metricsLogFrequency: Option[Long] = None, // metricsLogFreq
  metricsUpdateFrequency: Option[Long] = None // metricsUpdateFreq
) extends IgniteConfigurator {
  override def apply(cfg: IgniteConfiguration): IgniteConfiguration = {
    gridLogger.foreach(cfg.setGridLogger)
    mBeanServer.foreach(cfg.setMBeanServer)
    metricsHistorySize.foreach(cfg.setMetricsHistorySize)
    metricsUpdateFrequency.foreach(cfg.setMetricsUpdateFrequency)
    metricsExpireTime.foreach(cfg.setMetricsExpireTime)
    lifecycleBeans.foreach(cfg.setLifecycleBeans(_:_*))
    includeEventTypes.foreach(cfg.setIncludeEventTypes(_:_*))
    metricsLogFrequency.foreach(cfg.setMetricsLogFrequency)
    cfg
  }
}
