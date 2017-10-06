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

package com.jasonmar.ignite.config

import com.jasonmar.ignite.IgniteConfigurator
import com.jasonmar.ignite.config.grid._
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder

import scala.collection.JavaConverters._

object IgniteClientConfig {
  def parse(args: Array[String]): Option[IgniteClientConfig] = parser.parse(args, IgniteClientConfig())
  def parser: scopt.OptionParser[IgniteClientConfig] = {
    new scopt.OptionParser[IgniteClientConfig]("ignite") {
      head("ignite", "2.2")

      opt[Int]('f', "metricsFrequency")
        .valueName("<metricsFrequency>")
        .action((x, c) => c.copy(metricsFrequency = x))
        .text("metricsFrequency is an optional integer property")

      opt[String]('b', "bindAddress")
        .required()
        .valueName("<bindAddress>")
        .action((x, c) => c.copy(bindAddress = x))
        .text("bindAddress is an optional string property")

      opt[String]('w', "workDir")
        .valueName("<workDir>")
        .action((x, c) => c.copy(workDirectory = x))
        .text("workDir is an optional string property")

      opt[Seq[String]]('s', "servers")
        .valueName("<ip1>,<ip2>...")
        .action((x, c) => c.copy(servers = Some(x)))
        .text("servers to include")

      help("help").text("prints this usage text")

      note("some notes.")
    }
  }
}

/** Ignite config for building an ignite client
  *
  * @param bindAddress default 127.0.0.1
  * @param workDirectory default /tmp/ignite_client
  * @param servers sequence of ip or ip:port for cluster nodes
  * @param peerClassLoadingEnabled default false
  * @param metricsFrequency default 0, set > 0 to periodically log memory usage
  */
case class IgniteClientConfig(
  bindAddress: String = "127.0.0.1",
  workDirectory: String = "/tmp/ignite_client",
  servers: Option[Seq[String]] = None,
  peerClassLoadingEnabled: Boolean = false,
  metricsFrequency: Int = 0
) extends IgniteConfigurator {
  val igniteConfigs: Seq[IgniteConfigurator] = {
    Seq[IgniteConfigurator](
      GridConfig(
        activeOnStart = Some(true),
        clientMode = Some(true),
        peerClassLoadingEnabled = Some(peerClassLoadingEnabled),
        workDirectory = Some(workDirectory)
      ),
      NetworkConfig(localHost = Some(bindAddress)),
      SpiConfig(
        discoverySpi = Some(
          servers.filter(_.nonEmpty).map{s =>
            new TcpDiscoverySpi()
              .setIpFinder(new TcpDiscoveryVmIpFinder().setAddresses(s.asJavaCollection))
          }.getOrElse(
            new TcpDiscoverySpi()
              .setIpFinder(new TcpDiscoveryMulticastIpFinder().setLocalAddress(bindAddress))
          )
        )
      ),
      LoggingConfig(metricsLogFrequency = Some(metricsFrequency))
    )
  }

  override def apply(cfg: IgniteConfiguration): IgniteConfiguration = {
    igniteConfigs.foldLeft(cfg)((a,b) => b.apply(a))
  }
}
