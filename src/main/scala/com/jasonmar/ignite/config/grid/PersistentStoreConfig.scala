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

import org.apache.ignite.configuration.{DataStorageConfiguration, PersistentStoreConfiguration, WALMode}
import org.apache.ignite.internal.processors.cache.persistence.file.FileIOFactory

case class PersistentStoreConfig(
    alwaysWriteFullPages: Option[Boolean] = None,
    checkpointingFrequency: Option[Long] = None,
    checkpointingPageBufferSize: Option[Long] = None,
    checkpointingThreads: Option[Int] = None,
    fileIOFactory: Option[FileIOFactory] = None,
    lockWaitTime: Option[Long] = None,
    metricsEnabled: Option[Boolean] = None,
    persistentStorePath: Option[String] = None,
    rateTimeInterval: Option[Long] = None,
    subIntervals: Option[Int] = None,
    tlbSize: Option[Int] = None,
    walArchivePath: Option[String] = None,
    walAutoArchiveAfterInactivity: Option[Long] = None,
    walFlushFrequency: Option[Long] = None,
    walFsyncDelayNanos: Option[Long] = None,
    walHistorySize: Option[Int] = None,
    walMode: Option[WALMode] = None,
    walRecordIteratorBufferSize: Option[Int] = None,
    walSegments: Option[Int] = None,
    walSegmentSize: Option[Int] = None,
    walStorePath: Option[String] = None
) {
  def load: DataStorageConfiguration = {
    val cfg = new DataStorageConfiguration()
    alwaysWriteFullPages.foreach(cfg.setAlwaysWriteFullPages)
    checkpointingFrequency.foreach(cfg.setCheckpointFrequency)
    checkpointingThreads.foreach(cfg.setCheckpointThreads)
    fileIOFactory.foreach(cfg.setFileIOFactory)
    lockWaitTime.foreach(cfg.setLockWaitTime)
    metricsEnabled.foreach(cfg.setMetricsEnabled)
    persistentStorePath.foreach(cfg.setStoragePath)
    rateTimeInterval.foreach(cfg.setMetricsRateTimeInterval)
    subIntervals.foreach(cfg.setMetricsSubIntervalCount)
    tlbSize.foreach(cfg.setWalThreadLocalBufferSize)
    walArchivePath.foreach(cfg.setWalArchivePath)
    walAutoArchiveAfterInactivity.foreach(cfg.setWalAutoArchiveAfterInactivity)
    walFlushFrequency.foreach(cfg.setWalFlushFrequency)
    walFsyncDelayNanos.foreach(cfg.setWalFsyncDelayNanos)
    walHistorySize.foreach(cfg.setWalHistorySize)
    walMode.foreach(cfg.setWalMode)
    walRecordIteratorBufferSize.foreach(cfg.setWalRecordIteratorBufferSize)
    walSegments.foreach(cfg.setWalSegments)
    walSegmentSize.foreach(cfg.setWalSegmentSize)
    cfg
  }
}
