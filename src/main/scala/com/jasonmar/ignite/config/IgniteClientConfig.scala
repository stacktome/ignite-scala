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

object IgniteClientConfig {
  def parse(args: Array[String]): Option[IgniteClientConfig] = parser.parse(args, IgniteClientConfig())
  def parser: scopt.OptionParser[IgniteClientConfig] = {
    new scopt.OptionParser[IgniteClientConfig]("ignite") {
      head("ignite", "2.2")

      opt[String]('n', "name")
        .valueName("<name>")
        .action((x, c) => c.copy(name = Some(x)))
        .text("name is an optional string property")

      opt[String]('b', "bindAddress")
        .required()
        .valueName("<bindAddress>")
        .action((x, c) => c.copy(bindAddress = x))
        .text("bindAddress is an optional string property")

      opt[Int]('c', "commsPort")
        .valueName("<commsPort>")
        .action((x, c) => c.copy(commsPort = Some(x)))
        .text("commsPort is an optional integer property")

      opt[Int]('d', "discoveryPort")
        .valueName("<discoveryPort>")
        .action((x, c) => c.copy(discoveryPort = Some(x)))
        .text("discoveryPort is an optional integer property")

      opt[Int]('r', "portRange")
        .valueName("<portRange>")
        .action((x, c) => c.copy(portRange = Some(x)))
        .text("portRange is an optional integer property")

      opt[String]('w', "workDir")
        .valueName("<workDir>")
        .action((x, c) => c.copy(workDirectory = x))
        .text("workDir is an optional string property")

      opt[Seq[String]]('s', "servers")
        .valueName("<ip1>,<ip2>...")
        .action((x, c) => c.copy(servers = Some(x)))
        .text("servers to include")

      opt[Unit]("peerClassLoading")
        .action((_, c) => c.copy(peerClassLoading = true))
        .text("peerClassLoading is an optional flag")

      opt[Int]('f', "metricsFrequency")
        .valueName("<metricsFrequency>")
        .action((x, c) => c.copy(metricsFrequency = x))
        .text("metricsFrequency is an optional integer property")

      opt[Unit]("activate")
        .action((_, c) => c.copy(activate = true))
        .text("activate is an optional flag")

      help("help").text("prints this usage text")

      note("some notes.")
    }
  }
}

/** Ignite config for building an ignite client
  *
  * @param name default 127.0.0.1
  * @param bindAddress default 127.0.0.1
  * @param commsPort default 47100
  * @param discoveryPort default 47500
  * @param portRange default 0 if ports are set
  * @param workDirectory default /tmp/ignite_client
  * @param servers sequence of ip or ip:port for cluster nodes
  * @param peerClassLoading default false
  * @param metricsFrequency default 0, set > 0 to periodically log memory usage
  * @param activate default false, set true to activate cluster if currently inactive
  */
case class IgniteClientConfig(
    name: Option[String] = None,
    bindAddress: String = "0.0.0.0",
    commsPort: Option[Int] = None,
    discoveryPort: Option[Int] = None,
    portRange: Option[Int] = None,
    workDirectory: String = "/tmp/ignite_client",
    servers: Option[Seq[String]] = None,
    kubeSvcName: Option[String] = None,
    peerClassLoading: Boolean = false,
    metricsFrequency: Int = 0,
    activate: Boolean = false
) extends IgniteConfigurator {
  commsPort.foreach(port => require(port > 1000 && port <= 65535))
  discoveryPort.foreach(port => require(port > 1000 && port <= 65535))
  portRange.foreach(range => require(range >= 0 && range <= 65535))
  val igniteConfigs: Seq[IgniteConfigurator] = {
    Seq[IgniteConfigurator](
      GridConfig(
        activeOnStart = Some(true),
        clientMode = Some(true),
        peerClassLoadingEnabled = Some(peerClassLoading),
        workDirectory = Some(workDirectory)
      ),
      NetworkConfig(localHost = Some(bindAddress)),
      networkSpi(name, bindAddress, servers, kubeSvcName, commsPort, discoveryPort, portRange),
      LoggingConfig(metricsLogFrequency = Some(metricsFrequency))
    )
  }

  override def apply(cfg: IgniteConfiguration): IgniteConfiguration = {
    igniteConfigs.foldLeft(cfg)((a, b) => b.apply(a))
  }
}
