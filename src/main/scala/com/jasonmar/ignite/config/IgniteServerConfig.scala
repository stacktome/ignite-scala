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
import org.apache.ignite.configuration._

object IgniteServerConfig {
  def parse(args: Array[String]): Option[IgniteServerConfig] = parser.parse(args, IgniteServerConfig())
  def parser: scopt.OptionParser[IgniteServerConfig] = {
    new scopt.OptionParser[IgniteServerConfig]("ignite") {
      head("ignite", "2.4")

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

      opt[Seq[String]]('s', "servers")
        .valueName("<ip1>,<ip2>...")
        .action((x, c) => c.copy(servers = Some(x)))
        .text("servers to include")

      opt[String]('w', "workDir")
        .valueName("<workDir>")
        .action((x, c) => c.copy(workDirectory = x))
        .text("workDir is an optional string property")

      opt[Seq[String]]('s', "deploymentUris")
        .required()
        .valueName("<uri1>,<uri2>...")
        .action((x, c) => c.copy(deploymentUris = x))
        .text("deploymentUris is a comma separated sequence of uris containing jar/gar archives")

      opt[Map[String, String]]("userAttributes")
        .valueName("k1=v1,k2=v2...")
        .action((x, c) => c.copy(userAttributes = Some(x)))
        .text("userAttributes is an optional comma separated sequence of string key/value pairs")

      opt[Unit]("peerClassLoading")
        .action((_, c) => c.copy(peerClassLoading = true))
        .text("peerClassLoading is an optional flag")

      opt[Int]('f', "metricsFrequency")
        .valueName("<metricsFrequency>")
        .action((x, c) => c.copy(metricsFrequency = x))
        .text("metricsFrequency is an optional integer property")

      opt[Int]('m', "memMaxSize")
        .valueName("<memMaxSize>")
        .action((x, c) => c.copy(memMaxSizeGb = x * 1024L * 1024L * 1024L))
        .text("memMaxSize is an optional integer property")

      opt[Unit]("activate")
        .action((_, c) => c.copy(activate = true))
        .text("activate is an optional flag")

      help("help").text("prints this usage text")

      note("some notes.")
    }
  }
}

/** Ignite config with defaults closer to production than shipped with Ignite
  *
  * @param name defaults to naming based on IP address
  * @param bindAddress default 127.0.0.1
  * @param commsPort default 47100
  * @param discoveryPort default 47500
  * @param portRange default 0
  * @param servers sequence of ip or ip:port for cluster nodes
  * @param deploymentUris examples: file://freq=2000@localhost/var/ignite/deployment http://freq=60000@127.0.0.1:8080
  * @param userAttributes set attributes for this node which can be used for targeting compute tasks
  * @param peerClassLoading default false
  * @param workDirectory default /var/ignite
  * @param metricsFrequency default 0, set > 0 to periodically log memory usage
  * @param memMaxSizeGb default 20
  * @param dataStreamerThreadPoolSize default 16
  * @param asyncCallbackPoolSize default 16
  * @param igfsThreadPoolSize default 4
  * @param queryThreadPoolSize default 12
  * @param persistentStoreEnabled default true
  * @param walFlushFrequency default 10000, lower if using SSD or NVMe; only takes effect if persistentStoreEnabled is set to true
  * @param checkpointingFrequency default 540000 (9 minutes), lower if using SSD or NVMe; only takes effect if persistentStoreEnabled is set to true
  * @param walFsyncDelayNanos default 10000 (10 milliseconds), lower if using SSD or NVMe; only takes effect if persistentStoreEnabled is set to true
  * @param activate default false, whether to activate the cluster immediately
  */
case class IgniteServerConfig(
    name: Option[String] = None,
    bindAddress: String = "127.0.0.1",
    commsPort: Option[Int] = None,
    discoveryPort: Option[Int] = None,
    portRange: Option[Int] = None,
    servers: Option[Seq[String]] = None,
    kubeSvcName: Option[String] = None,
    deploymentUris: Seq[String] = Seq(),
    userAttributes: Option[Map[String, String]] = None,
    peerClassLoading: Boolean = false,
    workDirectory: String = "/tmp/ignite_server",
    metricsFrequency: Int = 0,
    memMaxSizeGb: Long = 2 * 1024 * 1024 * 1024,
    dataStreamerThreadPoolSize: Int = 16,
    asyncCallbackPoolSize: Int = 16,
    igfsThreadPoolSize: Int = 4,
    queryThreadPoolSize: Int = 12,
    persistentStoreEnabled: Boolean = true,
    walFlushFrequency: Int = DataStorageConfiguration.DFLT_WAL_FLUSH_FREQ * 5,
    checkpointingFrequency: Int = DataStorageConfiguration.DFLT_CHECKPOINT_FREQ * 3,
    walFsyncDelayNanos: Int = DataStorageConfiguration.DFLT_WAL_FSYNC_DELAY * 10,
    activate: Boolean = false
) extends IgniteConfigurator {
  val igniteConfigs: Seq[IgniteConfigurator] = Seq[IgniteConfigurator](
    GridConfig(
      activeOnStart = Some(true),
      clientMode = Some(false),
      userAttributes = userAttributes,
      peerClassLoadingEnabled = Some(peerClassLoading),
      workDirectory = Some(workDirectory)
    ),
    NetworkConfig(localHost = Some(bindAddress)),
    SubSystemConfig(
      persistentStoreConfig = {
        if (persistentStoreEnabled) {
          Some(
            PersistentStoreConfig(
              walFlushFrequency = Some(walFlushFrequency),
              checkpointingFrequency = Some(checkpointingFrequency),
              walFsyncDelayNanos = Some(walFsyncDelayNanos)
            ).load
          )
        } else {
          None
        }
      },
      dataStorageConfiguration = Some(
        new DataStorageConfiguration().setDataRegionConfigurations(
          new DataRegionConfiguration()
            .setInitialSize(memMaxSizeGb / 2) // half max size
            .setMaxSize(memMaxSizeGb)
            .setName(name.getOrElse(DataStorageConfiguration.DFLT_DATA_REG_DEFAULT_NAME))
            .setMetricsEnabled(false)
        )
      )
    ),
    networkSpi(name, bindAddress, servers, kubeSvcName, commsPort, discoveryPort, portRange),
    ThreadPoolConfig(
      dataStreamerThreadPoolSize = Some(dataStreamerThreadPoolSize), // default 8
      asyncCallbackPoolSize = Some(asyncCallbackPoolSize), // default 8
      igfsThreadPoolSize = Some(igfsThreadPoolSize), // default # cores
      queryThreadPoolSize = Some(queryThreadPoolSize) // default 8
    ),
    LoggingConfig(metricsLogFrequency = Some(metricsFrequency))
  )

  override def apply(cfg: IgniteConfiguration): IgniteConfiguration = {
    igniteConfigs.foldLeft(cfg) { (a, b) =>
      b.apply(a)
    }
  }
}
