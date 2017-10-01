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

package com.jasonmar.ignite.service

import org.apache.ignite.cluster.ClusterNode
import org.apache.ignite.lang.IgnitePredicate
import org.apache.ignite.services.Service

trait ServiceConfig[S<:Service,K] {
  val serviceName: String
  val service: S
  val cacheName: String
  val affinityKey: K
  val maxPerNodeCount: Option[Int] = None
  val nodeFilter: Option[IgnitePredicate[ClusterNode]] = None
  val totalCount: Option[Int] = None
}