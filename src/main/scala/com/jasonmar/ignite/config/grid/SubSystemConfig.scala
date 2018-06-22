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

import com.jasonmar.ignite.IgniteConfigurator
import org.apache.ignite.cache.CacheKeyConfiguration
import org.apache.ignite.configuration._
import org.apache.ignite.plugin.PluginConfiguration
import org.apache.ignite.services.ServiceConfiguration

/** Subsystems with dedicated configuration classes
  *
  */
case class SubSystemConfig(
    atomicConfiguration: Option[AtomicConfiguration] = None, // atomicCfg
    binaryConfiguration: Option[BinaryConfiguration] = None, // binaryCfg
    cacheConfigurations: Option[Seq[CacheConfiguration[_, _]]] = None, // cacheCfg
    cacheKeyConfigurations: Option[Seq[CacheKeyConfiguration]] = None, // cacheKeyCfg
    connectorConfiguration: Option[ConnectorConfiguration] = None, // connectorCfg
    executorConfigurations: Option[Seq[ExecutorConfiguration]] = None, // execCfgs
    fileSystemConfigurations: Option[Seq[FileSystemConfiguration]] = None, // igfsCfg
    hadoopConfiguration: Option[HadoopConfiguration] = None,           // hadoopCfg
    dataStorageConfiguration: Option[DataStorageConfiguration] = None, // memCfg
    //odbcConfiguration: Option[OdbcConfiguration] = None, // odbcCfg
    persistentStoreConfig: Option[DataStorageConfiguration] = None, // pstCfg
    platformConfiguration: Option[PlatformConfiguration] = None, // platformCfg
    pluginConfigurations: Option[Seq[PluginConfiguration]] = None, // pluginCfgs
    serviceConfigurations: Option[Seq[ServiceConfiguration]] = None, // svcCfgs
    sqlConnectorConfiguration: Option[SqlConnectorConfiguration] = None, // sqlConnCfg
    transactionConfiguration: Option[TransactionConfiguration] = None // txCfg
) extends IgniteConfigurator {
  override def apply(cfg: IgniteConfiguration): IgniteConfiguration = {
    cacheConfigurations.foreach(s => s.foreach(c => cfg.setCacheConfiguration(c)))
    cacheKeyConfigurations.foreach(cfg.setCacheKeyConfiguration(_: _*))
    binaryConfiguration.foreach(cfg.setBinaryConfiguration)
    dataStorageConfiguration.foreach(cfg.setDataStorageConfiguration)
    fileSystemConfigurations.foreach(cfg.setFileSystemConfiguration(_: _*))
    hadoopConfiguration.foreach(cfg.setHadoopConfiguration)
    connectorConfiguration.foreach(cfg.setConnectorConfiguration)
    //odbcConfiguration.foreach(cfg.setOdbcConfiguration)
    serviceConfigurations.foreach(cfg.setServiceConfiguration(_: _*))
    transactionConfiguration.foreach(cfg.setTransactionConfiguration)
    pluginConfigurations.foreach(cfg.setPluginConfigurations(_: _*))
    atomicConfiguration.foreach(cfg.setAtomicConfiguration)
    platformConfiguration.foreach(cfg.setPlatformConfiguration)
    executorConfigurations.foreach(cfg.setExecutorConfiguration(_: _*))
    sqlConnectorConfiguration.foreach(cfg.setSqlConnectorConfiguration)
    cfg
  }
}
