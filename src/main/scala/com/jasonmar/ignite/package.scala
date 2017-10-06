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

package com.jasonmar

import com.jasonmar.ignite.util.AutoClose.autoCloseWithShutdownHook
import org.apache.ignite.{Ignite, Ignition}
import org.apache.ignite.configuration.{CacheConfiguration, IgniteConfiguration}

import scala.collection.JavaConverters.asJavaCollectionConverter
import scala.util.Failure

/** Executable command-line application
  * Starts a production-ready Ignite server node
  */
package object ignite {

  trait CacheConfigurator[K,V] {
    def apply(cfg: CacheConfiguration[K,V]): CacheConfiguration[K,V]
  }

  trait IgniteConfigurator {
    def apply(cfg: IgniteConfiguration): IgniteConfiguration
  }

  trait IgniteFunction {
    val igniteFunction: (Ignite) => Unit
  }

  /** Initializes Ignite instance with configurations
    * Creates caches
    * Executes provided closure
    *
    * @param configs collection of IgniteConfiguration closures
    * @param cacheBuilders collection of IgniteCache configuration providers
    * @param igniteFunction closure using initialized Ignite instance
    * @return initialized Ignite instance
    */
  def exec(
    configs: Seq[IgniteConfigurator] = Seq.empty,
    cacheBuilders: Seq[CacheBuilder[_,_]] = Seq.empty,
    igniteFunction: Option[(Ignite => Unit)] = None,
    activate: Boolean = false
  ): Unit = {
    val cfg = configs.foldLeft(new IgniteConfiguration()){(cfg, configurator) => configurator(cfg)}
    autoCloseWithShutdownHook(Ignition.start(cfg)) { ignite =>
      activateCluster(ignite, activate)
      Option(cacheBuilders.map(_.cacheConfiguration))
        .filter(_.nonEmpty)
        .foreach{cfgs => ignite.getOrCreateCaches(cfgs.asJavaCollection)}
      igniteFunction.foreach(_.apply(ignite))
    } match {
      case Failure(e) =>
        System.err.println(e)
        throw e
      case _ =>
    }
  }

  def activateCluster(ignite: Ignite, activate: Boolean = true): Unit = {
    if (!ignite.active()){
      ignite.log().warning("Ignite cluster is not active")
      if (activate){
        ignite.log().info("Activating Ignite cluster")
        ignite.active(true)
        if (ignite.active()){
          ignite.log().info("Ignite cluster is active")
        } else {
          ignite.log().error("Failed to activate Ignite cluster")
        }
      }
    } else {
      ignite.log().info("Ignite cluster is active")
    }
  }

  def init(
    configs: Option[Seq[IgniteConfigurator]] = None,
    cacheBuilders: Option[Seq[CacheBuilder[_,_]]] = None,
    igniteFunction: Option[(Ignite => Unit)] = None,
    activate: Boolean = false
  ): Ignite = {
    // Generate IgniteConfiguration
    val cfg = configs.filter(_.nonEmpty)
      .map{_.foldLeft(new IgniteConfiguration()){(cfg, configurator) => configurator(cfg)}}
      .getOrElse(new IgniteConfiguration())

    // Start Ignite instance
    val ignite = Ignition.start(cfg)
    activateCluster(ignite, activate)

    // Create IgniteCaches
    cacheBuilders.filter(_.nonEmpty)
      .map(_.map(_.cacheConfiguration).asJavaCollection)
      .foreach(ignite.createCaches)

    // Apply IgniteFunctio
    igniteFunction.foreach(_.apply(ignite))

    ignite
  }

}
