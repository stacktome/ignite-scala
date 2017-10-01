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

import org.apache.ignite.IgniteServices
import org.apache.ignite.services.Service

trait ServiceBuilder[S<:Service,K] extends ServiceConfigBuilder[S,K] {
  def getService()(implicit svcs: IgniteServices): S = {
    svcs.service[S](serviceName)
  }

  def getServices()(implicit svcs: IgniteServices): java.util.Collection[S] = {
    svcs.services[S](serviceName)
  }

  def getServiceProxy(c: Class[S], sticky: Boolean = false)(implicit svcs: IgniteServices): S = {
    svcs.serviceProxy[S](serviceName, c, sticky)
  }

  def deployService()(implicit svcs: IgniteServices): Unit = {
    svcs.deploy(buildConfig())
  }
}
