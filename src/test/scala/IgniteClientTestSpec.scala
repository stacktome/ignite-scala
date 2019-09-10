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
class IgniteClientTestSpec extends FlatSpec {

  lazy val logger = LoggerFactory.getLogger(classOf[IgniteClientTestSpec])

  val NAME  = "IgniteTest"
  val NAME2 = "IgniteTest2"
  val NAME3 = "IgniteTest3"
  def init(assertFunc: (IgniteClient, AutoIncrementingClientCache[Boo], Ignite) => Unit,
           customBuilders: Option[Seq[CacheBuilder[_, _]]] = None) = {
    val config        = IgniteClientConfig(peerClassLoading = true, servers = Some(List("127.0.0.1")))
    val cacheBuilders = customBuilders.getOrElse(Seq(CacheBuilder.ofClass(NAME, classOf[Boo])))
    def igniteFunc: Ignite => Unit = (ign: Ignite) => {
      val ignClient = initClient(config.servers.get.head).get
      val cache     = mkCache[Long, Boo](ignClient)
      cache.clear()
      assertFunc(ignClient, AutoIncrementingClientCache(ign, cache), ign)
//      cache.destroy()
    }
    exec(Seq(config), cacheBuilders, Some(igniteFunc))
  }

  def mkCache[K, V](ignite: IgniteClient, name: String = NAME): ClientCache[K, V] =
    ignite.cache[K, V](name)

  "Boo " should " allow put and get values for simple query" in {
    init((ign, vCache, _) => {
      vCache.put(Boo(d = 20))
      vCache.put(Boo(d = 10))
      vCache.put(Boo(d = 30))

      val res = sqlQueryClient(vCache.cache, "d > 10").getOrElse(Array()).map(_.getValue)
      assert(res.size == 2)
      assert(res.contains(Boo(d = 30)))

      val resWithArgs = sqlQueryClient(vCache.cache, "d > ?", 10).getOrElse(Array()).map(_.getValue)
      assert(resWithArgs.size == 2)
      assert(resWithArgs.contains(Boo(d = 30)))

      val resWithManyArgs = sqlQueryClient(vCache.cache, "d > ? AND d < ?", 10, 30).getOrElse(Array()).map(_.getValue)
      assert(resWithManyArgs.size == 1)
      assert(resWithManyArgs.contains(Boo(d = 20)))

      val resWithSelect =
        sqlQueryClient(vCache.cache, "select b.* from Boo b where b.d > 10").getOrElse(Array()).map(_.getValue)
      assert(resWithSelect.size == 2)
      assert(resWithSelect.contains(Boo(d = 30)))
    })
  }

  "Boo " should " allow put and get values for simple aggregate" in {
    init((ign, vCache, _) => {
      vCache.put(Boo(d = 40))
      vCache.put(Boo(d = 60))
      vCache.put(Boo(d = 20))
      def fromJavaBigDecimal(v: Any) = BigDecimal(v.asInstanceOf[java.math.BigDecimal])
      val res =
        sqlFieldsClientQuery[BigDecimal](vCache.cache,
                                         "select sum(d) from Boo",
                                         _.toList.map(list => fromJavaBigDecimal(list.get(0))).head)
      assert(res.getOrElse(0) == 120)

      val resWithArgs = sqlFieldsClientQuery[BigDecimal](vCache.cache,
                                                         "select sum(d) from Boo where d > ?",
                                                         _.toList.map(list => fromJavaBigDecimal(list.get(0))).head,
                                                         20)
      assert(resWithArgs.getOrElse(0) == 100)
    })
  }

  "Boo " should " should support order by asc/desc" in {
    init((ign, vCache, _) => {
      vCache.put(Boo(d = 40))
      vCache.put(Boo(d = 60))
      vCache.put(Boo(d = 20))

      val res = sqlQueryClient(vCache.cache, "select * from Boo order by d").getOrElse(Array()).map(_.getValue)
      assert(res.head.d == 20)
      assert(res.last.d == 60)

      val res2 = sqlQueryClient(vCache.cache, "select * from Boo order by d desc").getOrElse(Array()).map(_.getValue)
      assert(res2.head.d == 60)
      assert(res2.last.d == 20)
    })
  }

  "Single cache " should " support multi table" in {
    init(
      (ign, vCache, ignFull) => {

        val cacheF  = AutoIncrementingClientCache(ignFull, mkCache[Long, Foo](ign, NAME2))
        val cacheAF = mkCache[AffinityKey[Long], Boo](ign, NAME3)

        cacheF.cache.clear()
        cacheAF.clear()

        cacheF.put(Foo(name = "first", 1))
        vCache.put(Boo(d = 20, 1))
        cacheAF.put(new AffinityKey[Long](1, 1), Boo(d = 20, 1))

        val res = sqlQueryClient(vCache.cache, "select * from Boo order by d").getOrElse(Array()).map(_.getValue)
        assert(res.last.d == 20)

        val res2 =
          sqlQueryClient(cacheF.cache, s"select * from Foo order by name desc").getOrElse(Array()).map(_.getValue)
        assert(res2.last.name == "first")

        val res3 = sqlQueryClient(cacheAF,
                                  s"""select b.* from Boo b, "$NAME2".Foo AS f
              where fooId = f.id and f.name = 'first'
              order by d desc""").getOrElse(Array()).map(_.getValue)
        assert(res3.last.d == 20)
      },
      Some(
        Seq(
          CacheBuilder.ofClass(NAME, classOf[Boo]),
          CacheBuilder.ofClass(NAME2, classOf[Foo]),
          CacheBuilder.builderOf(NAME3, classOf[AffinityKey[Long]], classOf[Boo])
        ))
    )
  }
}
