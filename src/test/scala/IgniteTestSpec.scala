import com.jasonmar.ignite.config.IgniteClientConfig
import com.jasonmar.ignite.sql._
import com.jasonmar.ignite.util.AutoIncrementingIgniteCache
import com.jasonmar.ignite.{CacheBuilder, exec}
import org.apache.ignite.cache.query.annotations.QuerySqlField
import org.apache.ignite.{Ignite, IgniteCache}
import org.scalatest.FlatSpec

import scala.collection.JavaConverters._
import javax.cache.Cache
import org.slf4j.LoggerFactory

import scala.annotation.meta.field

/**
  * Created by evaldas on 12/27/16.
  */
class IgniteTestSpec extends FlatSpec {

  lazy val logger = LoggerFactory.getLogger(classOf[IgniteTestSpec])

  val NAME = "IgniteTest"
  def init(assertFunc: (Ignite, AutoIncrementingIgniteCache[Boo]) => Unit) = {
    val config        = IgniteClientConfig(peerClassLoading = true)
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

  "Boo " should " allow put and get values for simple query" in {
    init((ign, vCache) => {
      vCache.put(Boo(d = 20))
      vCache.put(Boo(d = 10))
      vCache.put(Boo(d = 30))

      val res = sqlQuery(vCache.cache, "d > 10").getOrElse(Array()).map(_.getValue)
      assert(res.size == 2)
      assert(res.contains(Boo(d = 30)))

      val resWithArgs = sqlQuery(vCache.cache, "d > ?", 10).getOrElse(Array()).map(_.getValue)
      assert(resWithArgs.size == 2)
      assert(resWithArgs.contains(Boo(d = 30)))

      val resWithSelect =
        sqlQuery(vCache.cache, "select b.* from Boo b where b.d > 10").getOrElse(Array()).map(_.getValue)
      assert(resWithSelect.size == 2)
      assert(resWithSelect.contains(Boo(d = 30)))
    })
  }

  "Boo " should " allow put and get values for simple aggregate" in {
    init((ign, vCache) => {
      vCache.put(Boo(d = 40))
      vCache.put(Boo(d = 60))
      vCache.put(Boo(d = 20))
      def fromJavaBigDecimal(v: Any) = BigDecimal(v.asInstanceOf[java.math.BigDecimal])
      val res =
        sqlFieldsQuery[BigDecimal](vCache.cache,
                                   "select sum(d) from Boo",
                                   _.toList.map(list => fromJavaBigDecimal(list.get(0))).head)
      assert(res.getOrElse(0) == 120)

      val resWithArgs = sqlFieldsQuery[BigDecimal](vCache.cache,
                                                   "select sum(d) from Boo where d > ?",
                                                   _.toList.map(list => fromJavaBigDecimal(list.get(0))).head,
                                                   20)
      assert(resWithArgs.getOrElse(0) == 100)
    })
  }
}
case class Boo(@(QuerySqlField @field)(index = true) d: Int)
