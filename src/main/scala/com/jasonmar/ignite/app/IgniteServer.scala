package com.jasonmar.ignite.app

import com.jasonmar.ignite.Init
import com.jasonmar.ignite.config.IgniteServerConfig

/** Extend this trait and implement igniteFunction: (Ignite) => Unit
  * for a fully functional Ignite server node that accepts configuration via command line
  */
trait IgniteServer extends IgniteFunction {
  def main(args: Array[String]): Unit = {
    IgniteServerConfig.parse(args).foreach{cfg =>
      Init(configs = cfg.igniteConfigs, igniteFunction = Some(igniteFunction))
    }
  }
}
