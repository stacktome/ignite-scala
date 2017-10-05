package com.jasonmar.ignite.app

import com.jasonmar.ignite.Init
import com.jasonmar.ignite.config.IgniteClientConfig

/** Extend this trait and implement igniteFunction: (Ignite) => Unit
  * for a fully functional Ignite client node that accepts configuration via command line
  */
trait IgniteClient extends IgniteFunction {
  def main(args: Array[String]): Unit = {
    IgniteClientConfig.parse(args).foreach{cfg =>
      Init(configs = cfg.igniteConfigs, igniteFunction = Some(igniteFunction))
    }
  }
}
