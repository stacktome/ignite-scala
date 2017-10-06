package com.jasonmar.ignite

import com.jasonmar.ignite.config.grid.SpiConfig
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder
import scala.collection.JavaConverters._

package object config {
  def networkSpi(name: Option[String], bindAddress: String, servers: Option[Seq[String]], commsPort: Option[Int], discoveryPort: Option[Int], portRange: Option[Int]): SpiConfig = {
    SpiConfig(
      communicationSpi = Some{
        val tcp = new TcpCommunicationSpi().setLocalAddress(bindAddress)
        name.foreach(tcp.setName)
        commsPort.foreach(tcp.setLocalPortRange(portRange.getOrElse(0)).setLocalPort)
        tcp
      },
      discoverySpi = Some{
        val spi = servers.filter(_.nonEmpty).map{s =>
          new TcpDiscoverySpi().setIpFinder(new TcpDiscoveryVmIpFinder().setAddresses(s.asJavaCollection))
        }.getOrElse(
          new TcpDiscoverySpi().setIpFinder(new TcpDiscoveryMulticastIpFinder().setLocalAddress(bindAddress))
        )
        discoveryPort.foreach(spi.setLocalPortRange(portRange.getOrElse(0)).setLocalPort)
        spi
      }
    )
  }
}
