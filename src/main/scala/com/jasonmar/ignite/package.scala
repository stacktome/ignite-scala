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
import org.apache.ignite.client.IgniteClient
import org.apache.ignite.{Ignite, Ignition}
import org.apache.ignite.configuration.{CacheConfiguration, ClientConfiguration, IgniteConfiguration}

import scala.collection.JavaConverters.asJavaCollectionConverter
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Try}
import org.apache.ignite.client.IgniteClient
import org.slf4j.LoggerFactory

/** Executable command-line application
  * Starts a production-ready Ignite server node
  */
package object ignite {

  trait CacheConfigurator[K, V] {
    def apply(cfg: CacheConfiguration[K, V]): CacheConfiguration[K, V]
  }

  trait IgniteConfigurator {
    def apply(cfg: IgniteConfiguration): IgniteConfiguration
  }

  trait IgniteFunction {
    val igniteFunction: (Ignite) => Unit
  }
  val log = LoggerFactory.getLogger(this.getClass.getName)

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
      cacheBuilders: Seq[CacheBuilder[_, _]] = Seq.empty,
      igniteFunction: Option[(Ignite => Unit)] = None,
      activate: Boolean = false
  ): Unit = {
    val cfg = configs.foldLeft(new IgniteConfiguration()) { (cfg, configurator) =>
      configurator(cfg)
    }
    autoCloseWithShutdownHook(Ignition.start(cfg)) { ignite =>
      activateCluster(ignite, activate)
      Option(cacheBuilders.map(_.cacheConfiguration))
        .filter(_.nonEmpty)
        .foreach { cfgs =>
          ignite.getOrCreateCaches(cfgs.asJavaCollection)
        }
      igniteFunction.foreach(_.apply(ignite))
    } match {
      case Failure(e) =>
        System.err.println(e)
        throw e
      case _ =>
    }
  }

  def activateCluster(ignite: Ignite, activate: Boolean = true): Unit = {
    if (!ignite.active()) {
      ignite.log().warning("Ignite cluster is not active")
      if (activate) {
        ignite.log().info("Activating Ignite cluster")
        ignite.active(true)
        if (ignite.active()) {
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
      cacheBuilders: Option[Seq[CacheBuilder[_, _]]] = None,
      igniteFunction: Option[(Ignite => Unit)] = None,
      activate: Boolean = false
  ): Ignite = {
    // Generate IgniteConfiguration
    val cfg = configs
      .filter(_.nonEmpty)
      .map {
        _.foldLeft(new IgniteConfiguration()) { (cfg, configurator) =>
          configurator(cfg)
        }
      }
      .getOrElse(new IgniteConfiguration())

    // Start Ignite instance
    val ignite = Ignition.start(cfg)
    activateCluster(ignite, activate)

    // Create IgniteCaches
    cacheBuilders
      .filter(_.nonEmpty)
      .map(_.map(_.cacheConfiguration).asJavaCollection)
      .foreach(ignite.getOrCreateCaches)

    // Apply IgniteFunctio
    igniteFunction.foreach(_.apply(ignite))

    ignite
  }

  def initClient(adr: String, retry: Int = 0, ports: List[String] = List("10800", "10801", "10802"))(
      implicit ec: ExecutionContext): Option[org.apache.ignite.client.IgniteClient] = {
    import scala.concurrent.duration._
    val res = Try(Await.result(Future {
      Ignition.startClient(new ClientConfiguration().setTimeout(30000).setAddresses(s"$adr:${ports.head}"))
    }, 20.seconds))
    val client = res match {
      case scala.util.Success(client) => {
        log.info(s"connected to: $adr")
        Some(client)
      }
      case scala.util.Failure(exp) => {
        log.warn(s"client could not connect ${exp} retry: $retry")
        None
      }
    }
    if (client.isEmpty && retry < 10) {
      Thread.sleep(1000L * (retry + 1))
      initClient(adr, retry + 1, if (ports.size > 1) ports.tail else List("10800", "10801", "10802"))
    } else
      client
  }

}
