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

import javax.cache.configuration.Factory
import javax.net.ssl.SSLContext

import com.jasonmar.ignite.IgniteConfigurator
import org.apache.ignite.configuration.{AddressResolver, IgniteConfiguration}
import org.apache.ignite.plugin.segmentation.{SegmentationPolicy, SegmentationResolver}

case class NetworkConfig(
  addressResolver: Option[AddressResolver] = None, // addrRslvr
  allSegmentationResolversPassRequired: Option[Boolean] = None, // allResolversPassReq
  clientFailureDetectionTimeout: Option[Long] = None, // clientFailureDetectionTimeout
  discoveryStartupDelay: Option[Long] = None, // discoStartupDelay
  failureDetectionTimeout: Option[Long] = None, // failureDetectionTimeout
  localHost: Option[String] = None, // locHost
  networkSendRetryCount: Option[Int] = None, // sndRetryCnt
  networkSendRetryDelay: Option[Long] = None, // sndRetryDelay
  networkTimeout: Option[Long] = None, // netTimeout
  segmentationPolicy: Option[SegmentationPolicy] = None, // segPlc
  segmentationResolveAttempts: Option[Int] = None, // segResolveAttempts
  segmentationResolvers: Option[Seq[SegmentationResolver]] = None, // segResolvers
  segmentCheckFrequency: Option[Long] = None, // segChkFreq
  sslContextFactory: Option[Factory[SSLContext]] = None, // sslCtxFactory
  timeServerPortBase: Option[Int] = None, // timeSrvPortBase
  timeServerPortRange: Option[Int] = None, // timeSrvPortRange
  waitForSegmentOnStart: Option[Boolean] = None // waitForSegOnStart
) extends IgniteConfigurator {
  override def apply(cfg: IgniteConfiguration): IgniteConfiguration = {
    localHost.foreach(cfg.setLocalHost)
    networkTimeout.foreach(cfg.setNetworkTimeout)
    networkSendRetryDelay.foreach(cfg.setNetworkSendRetryDelay)
    networkSendRetryCount.foreach(cfg.setNetworkSendRetryCount)
    sslContextFactory.foreach(cfg.setSslContextFactory)
    segmentationPolicy.foreach(cfg.setSegmentationPolicy)
    waitForSegmentOnStart.foreach(cfg.setWaitForSegmentOnStart)
    allSegmentationResolversPassRequired.foreach(cfg.setAllSegmentationResolversPassRequired)
    segmentationResolveAttempts.foreach(cfg.setSegmentationResolveAttempts)
    segmentationResolvers.foreach(cfg.setSegmentationResolvers(_:_*))
    segmentCheckFrequency.foreach(cfg.setSegmentCheckFrequency)
    clientFailureDetectionTimeout.foreach(cfg.setClientFailureDetectionTimeout)
    failureDetectionTimeout.foreach(cfg.setFailureDetectionTimeout)
    discoveryStartupDelay.foreach(cfg.setDiscoveryStartupDelay)
    addressResolver.foreach(cfg.setAddressResolver)
    timeServerPortBase.foreach(cfg.setTimeServerPortBase)
    timeServerPortRange.foreach(cfg.setTimeServerPortRange)
    cfg
  }
}
