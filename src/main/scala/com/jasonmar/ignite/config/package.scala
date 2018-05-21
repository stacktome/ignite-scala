package com.jasonmar.ignite

import com.jasonmar.ignite.config.grid.SpiConfig
import org.apache.ignite.IgniteException
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.kubernetes.TcpDiscoveryKubernetesIpFinder
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder

import scala.collection.JavaConverters._

package object config {
  def networkSpi(name: Option[String],
                 bindAddress: String,
                 servers: Option[Seq[String]],
                 kubeServiceName: Option[String],
                 commsPort: Option[Int],
                 discoveryPort: Option[Int],
                 portRange: Option[Int]): SpiConfig = {
    SpiConfig(
      communicationSpi = Some {
        val tcp = new TcpCommunicationSpi().setLocalAddress(bindAddress)
        name.foreach(tcp.setName)
        commsPort.foreach(tcp.setLocalPortRange(portRange.getOrElse(0)).setLocalPort)
        tcp
      },
      discoverySpi = Some {
        val spi = (servers, kubeServiceName) match {
          case (Some(s), None) =>
            new TcpDiscoverySpi().setIpFinder(new TcpDiscoveryVmIpFinder().setAddresses(s.asJavaCollection))
          case (None, Some(sn)) =>
            new TcpDiscoverySpi().setIpFinder {
              val finder = new TcpDiscoveryKubernetesIpFinder()
              finder.setServiceName(sn)
              finder
            }
          case (Some(_), Some(_)) => throw new IgniteException("wrong config - choose either servers or kube")
          case (None, None) =>
            new TcpDiscoverySpi().setIpFinder(new TcpDiscoveryMulticastIpFinder().setLocalAddress(bindAddress))
        }

        discoveryPort.foreach(spi.setLocalPortRange(portRange.getOrElse(0)).setLocalPort)
        spi
      }
    )
  }
}
