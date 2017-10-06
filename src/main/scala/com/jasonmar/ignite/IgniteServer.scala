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

package com.jasonmar.ignite

import com.jasonmar.ignite.config.IgniteServerConfig
import org.apache.ignite.Ignite

/** CLI template trait
  * Builds a fully functional Ignite server node configured via command line options
  */
trait IgniteServer {
  val igniteFunction: Option[(Ignite) => Unit]
  def main(args: Array[String]): Unit = {
    IgniteServerConfig.parse(args).foreach{cfg =>
      init(configs = Some(cfg.igniteConfigs), igniteFunction = igniteFunction, activate = cfg.activate)
    }
  }
}
