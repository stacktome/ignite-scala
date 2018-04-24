import com.jasonmar.ignite.config.IgniteClientConfig
import com.jasonmar.ignite.sql.sqlQuery
import com.jasonmar.ignite.util.AutoIncrementingIgniteCache
import com.jasonmar.ignite.{CacheBuilder, exec}
import org.apache.ignite.cache.query.annotations.QuerySqlField
import org.apache.ignite.{Ignite, IgniteCache}
import org.scalatest.FlatSpec

import scala.annotation.meta.field

/**
  * Created by evaldas on 12/27/16.
  */
class IgniteTestSpec extends FlatSpec {

  val NAME = "IgniteTest"
  def init(assertFunc: (Ignite, AutoIncrementingIgniteCache[Boo]) => Unit) = {
    val config = IgniteClientConfig(peerClassLoading = true)
    val cacheBuilders = Seq(CacheBuilder.ofClass(NAME, classOf[Boo]))
    def igniteFunc: Ignite => Unit = (ign: Ignite) => {
      val cache = mkCache[Long, Boo](ign)
      cache.clear()
      assertFunc(ign, AutoIncrementingIgniteCache(ign, cache))
      cache.destroy()
    }
    exec(Seq(config), cacheBuilders, Some(igniteFunc))
  }

  def mkCache[K, V](ignite: Ignite): IgniteCache[K, V] =
    ignite.cache[K, V](NAME)

  "Boo " should " allow put and get values" in {
    init((ign, vCache) => {
      vCache.put(Boo(d=20))
      vCache.put(Boo(d=10))
      vCache.put(Boo(d=30))

      val res = sqlQuery(vCache.cache, "d > 10").getOrElse(Array()).map(_.getValue)
      assert(res.size == 2)
      assert(res.contains(Boo(d=30)))
    })
  }
}
case class Boo(@(QuerySqlField @field)(index = true) d: Int)
