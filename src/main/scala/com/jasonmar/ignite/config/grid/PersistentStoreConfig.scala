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

import org.apache.ignite.configuration.{PersistentStoreConfiguration, WALMode}
import org.apache.ignite.internal.processors.cache.persistence.file.FileIOFactory

case class PersistentStoreConfig(
  setAlwaysWriteFullPages: Option[Boolean] = None,
  setCheckpointingFrequency: Option[Long] = None,
  setCheckpointingPageBufferSize: Option[Long] = None,
  setCheckpointingThreads: Option[Int] = None,
  setFileIOFactory: Option[FileIOFactory] = None,
  setLockWaitTime: Option[Long] = None,
  setMetricsEnabled: Option[Boolean] = None,
  setPersistentStorePath: Option[String] = None,
  setRateTimeInterval: Option[Long] = None,
  setSubIntervals: Option[Int] = None,
  setTlbSize: Option[Int] = None,
  setWalArchivePath: Option[String] = None,
  setWalAutoArchiveAfterInactivity: Option[Long] = None,
  setWalFlushFrequency: Option[Long] = None,
  setWalFsyncDelayNanos: Option[Long] = None,
  setWalHistorySize: Option[Int] = None,
  setWalMode: Option[WALMode] = None,
  setWalRecordIteratorBufferSize: Option[Int] = None,
  setWalSegments: Option[Int] = None,
  setWalSegmentSize: Option[Int] = None,
  setWalStorePath: Option[String] = None
) {
  def load: PersistentStoreConfiguration = {
    val cfg = new PersistentStoreConfiguration()
    setAlwaysWriteFullPages.foreach(cfg.setAlwaysWriteFullPages)
    setCheckpointingFrequency.foreach(cfg.setCheckpointingFrequency)
    setCheckpointingPageBufferSize.foreach(cfg.setCheckpointingPageBufferSize)
    setCheckpointingThreads.foreach(cfg.setCheckpointingThreads)
    setFileIOFactory.foreach(cfg.setFileIOFactory)
    setLockWaitTime.foreach(cfg.setLockWaitTime)
    setMetricsEnabled.foreach(cfg.setMetricsEnabled)
    setPersistentStorePath.foreach(cfg.setPersistentStorePath)
    setRateTimeInterval.foreach(cfg.setRateTimeInterval)
    setSubIntervals.foreach(cfg.setSubIntervals)
    setTlbSize.foreach(cfg.setTlbSize)
    setWalArchivePath.foreach(cfg.setWalArchivePath)
    setWalAutoArchiveAfterInactivity.foreach(cfg.setWalAutoArchiveAfterInactivity)
    setWalFlushFrequency.foreach(cfg.setWalFlushFrequency)
    setWalFsyncDelayNanos.foreach(cfg.setWalFsyncDelayNanos)
    setWalHistorySize.foreach(cfg.setWalHistorySize)
    setWalMode.foreach(cfg.setWalMode)
    setWalRecordIteratorBufferSize.foreach(cfg.setWalRecordIteratorBufferSize)
    setWalSegments.foreach(cfg.setWalSegments)
    setWalSegmentSize.foreach(cfg.setWalSegmentSize)
    setWalStorePath.foreach(cfg.setWalStorePath)
    cfg
  }
}
