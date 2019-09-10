import com.jasonmar.ignite
import com.jasonmar.ignite.config.IgniteClientConfig
import com.jasonmar.ignite.sql._
import com.jasonmar.ignite.util.{AutoIncrementingClientCache, AutoIncrementingIgniteCache}
import com.jasonmar.ignite.{CacheBuilder, exec, initClient}
import org.apache.ignite.cache.query.annotations.QuerySqlField
import org.apache.ignite.{Ignite, IgniteCache}
import org.scalatest.FlatSpec

import scala.concurrent.ExecutionContext.Implicits._
import scala.collection.JavaConverters._
import javax.cache.Cache
import org.apache.ignite.cache.affinity.AffinityKey
import org.apache.ignite.client.{ClientCache, IgniteClient}
import org.slf4j.LoggerFactory

import scala.annotation.meta.field

/**
  * Created by evaldas on 12/27/16.
  */
class IgniteTestSpec extends FlatSpec {

  lazy val logger = LoggerFactory.getLogger(classOf[IgniteTestSpec])

  val NAME  = "IgniteTest"
  val NAME2 = "IgniteTest2"
  val NAME3 = "IgniteTest3"
  def init(assertFunc: (IgniteClient, AutoIncrementingClientCache[Boo]) => Unit,
           customBuilders: Option[Seq[CacheBuilder[_, _]]] = None) = {
    val config        = IgniteClientConfig(peerClassLoading = true, servers = Some(List("127.0.0.1")))
    val cacheBuilders = customBuilders.getOrElse(Seq(CacheBuilder.ofClass(NAME, classOf[Boo])))
    def igniteFunc: Ignite => Unit = (ign: Ignite) => {
      val ignClient = initClient(config.servers.get.head).get
      val cache     = mkCache[Long, Boo](ignClient)
      cache.clear()
      assertFunc(ignClient, AutoIncrementingClientCache(ign, cache))
//      cache.destroy()
    }
    exec(Seq(config), cacheBuilders, Some(igniteFunc))
  }

  def mkCache[K, V](ignite: IgniteClient, name: String = NAME): ClientCache[K, V] =
    ignite.cache[K, V](name)

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

      val resWithManyArgs = sqlQuery(vCache.cache, "d > ? AND d < ?", 10, 30).getOrElse(Array()).map(_.getValue)
      assert(resWithManyArgs.size == 1)
      assert(resWithManyArgs.contains(Boo(d = 20)))

      val resWithSelect =
        sqlQuery(vCache.cache, "select b.* from Boo b where b.d > 10").getOrElse(Array()).map(_.getValue)
      assert(resWithSelect.size == 2)
      assert(resWithSelect.contains(Boo(d = 30)))
    })
  }

}
