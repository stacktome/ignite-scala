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

import java.io.Serializable
import java.util.UUID

import com.jasonmar.ignite.IgniteConfigurator
import org.apache.ignite.configuration._
import org.apache.ignite.events.Event
import org.apache.ignite.lang.{IgniteInClosure, IgnitePredicate}
import org.apache.ignite.marshaller.Marshaller

import scala.collection.JavaConverters.mapAsJavaMapConverter


case class GridConfig (
  activeOnStart: Option[Boolean] = None, // activeOnStart
  cacheSanityCheckEnabled: Option[Boolean] = None, // cacheSanityCheckEnabled
  classLoader: Option[ClassLoader] = None, // classLdr
  clientMode: Option[Boolean] = None, // clientMode
  consistentId: Option[Serializable] = None, // consistentId
  daemon: Option[Boolean] = None, // daemon
  deploymentMode: Option[DeploymentMode] = None, // deployMode
  gridName: Option[String] = None, // gridName
  igniteInstanceName: Option[String] = None, // instanceName
  igniteHome: Option[String] = None, // igniteHome
  includeProperties: Option[Seq[String]] = None, // includeProps
  lateAffinityAssignment: Option[Boolean] = None, // lateAffAssignment
  localEventListeners: Option[Map[IgnitePredicate[Event], Array[Int]]] = None, // lsnrs
  longQueryWarningTimeout: Option[Long] = None, // longQryWarnTimeout
  marshaller: Option[Marshaller] = None, // marsh
  marshalLocalJobs: Option[Boolean] = None, // marshLocJobs
  modeId: Option[UUID] = None, // nodeId
  peerClassLoadingEnabled: Option[Boolean] = None, // p2pEnabled
  peerClassLoadingLocalClassPathExclude: Option[Seq[String]] = None, // p2pLocClsPathExcl
  peerClassLoadingMissedResourcesCacheSize: Option[Int] = None, // p2pMissedCacheSize
  userAttributes: Option[Map[String, String]] = None, // userAttrs
  utilityCacheKeepAliveTime: Option[Long] = None, // keepAliveTime
  warmupClosure: Option[IgniteInClosure[IgniteConfiguration]] = None, // warmupClos
  workDirectory: Option[String] = None // igniteWorkDir
) extends IgniteConfigurator {
  override def apply(cfg: IgniteConfiguration): IgniteConfiguration = {
    daemon.foreach(cfg.setDaemon)
    //setGridName.foreach(cfg.setGridName)
    igniteInstanceName.foreach(cfg.setIgniteInstanceName)
    consistentId.foreach(cfg.setConsistentId)
    userAttributes.foreach(x => cfg.setUserAttributes(x.asJava))
    utilityCacheKeepAliveTime.foreach(cfg.setUtilityCacheKeepAliveTime)
    igniteHome.foreach(cfg.setIgniteHome)
    workDirectory.foreach(cfg.setWorkDirectory)
    //setNodeId.foreach(cfg.setNodeId)
    //setMarshaller.foreach(cfg.setMarshaller)
    marshalLocalJobs.foreach(cfg.setMarshalLocalJobs)
    peerClassLoadingEnabled.foreach(cfg.setPeerClassLoadingEnabled)
    peerClassLoadingLocalClassPathExclude.foreach(cfg.setPeerClassLoadingLocalClassPathExclude(_:_*))
    deploymentMode.foreach(cfg.setDeploymentMode)
    peerClassLoadingMissedResourcesCacheSize.foreach(cfg.setPeerClassLoadingMissedResourcesCacheSize)
    clientMode.foreach(cfg.setClientMode)
    activeOnStart.foreach(cfg.setActiveOnStart)
    cacheSanityCheckEnabled.foreach(cfg.setCacheSanityCheckEnabled)
    includeProperties.foreach(cfg.setIncludeProperties(_:_*))
    //setLocalEventListeners.foreach(x => cfg.setLocalEventListeners(x.asJava))
    warmupClosure.foreach(cfg.setWarmupClosure)
    classLoader.foreach(cfg.setClassLoader)
    //setLateAffinityAssignment.foreach(cfg.setLateAffinityAssignment)
    longQueryWarningTimeout.foreach(cfg.setLongQueryWarningTimeout)
    cfg
  }

}
