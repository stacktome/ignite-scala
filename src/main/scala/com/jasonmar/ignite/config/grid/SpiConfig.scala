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
import org.apache.ignite.spi.checkpoint.CheckpointSpi
import org.apache.ignite.spi.collision.CollisionSpi
import org.apache.ignite.spi.communication.CommunicationSpi
import org.apache.ignite.spi.deployment.DeploymentSpi
import org.apache.ignite.spi.discovery.DiscoverySpi
import org.apache.ignite.spi.eventstorage.EventStorageSpi
import org.apache.ignite.spi.failover.FailoverSpi
import org.apache.ignite.spi.indexing.IndexingSpi
import org.apache.ignite.spi.loadbalancing.LoadBalancingSpi

case class SpiConfig(
  eventStorageSpi: Option[EventStorageSpi] = None, // evtSpi
  discoverySpi: Option[DiscoverySpi] = None, // discoSpi
  communicationSpi: Option[CommunicationSpi[_]] = None, // commSpi
  collisionSpi: Option[CollisionSpi] = None, // colSpi
  deploymentSpi: Option[DeploymentSpi] = None, // deploySpi
  checkpoIntSpi: Option[Seq[CheckpointSpi]] = None, // cpSpi
  failoverSpi: Option[Seq[FailoverSpi]] = None, // failSpi
  loadBalancingSpi: Option[Seq[LoadBalancingSpi]] = None, // loadBalancingSpi
  indexingSpi: Option[IndexingSpi] = None // indexingSpi
) extends IgniteConfigurator {
  override def apply(cfg: IgniteConfiguration): IgniteConfiguration = {
    eventStorageSpi.foreach(cfg.setEventStorageSpi)
    discoverySpi.foreach(cfg.setDiscoverySpi)
    //setCommunicationSpi.foreach(x => cfg.setCommunicationSpi(x))
    collisionSpi.foreach(cfg.setCollisionSpi)
    deploymentSpi.foreach(cfg.setDeploymentSpi)
    //setCheckpointSpi.foreach(x => x.foreach(spi => cfg.setCheckpointSpi(spi)))
    failoverSpi.foreach(cfg.setFailoverSpi(_:_*))
    loadBalancingSpi.foreach(cfg.setLoadBalancingSpi(_:_*))
    indexingSpi.foreach(cfg.setIndexingSpi)
    cfg
  }
}
