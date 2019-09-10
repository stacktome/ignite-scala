package com.jasonmar.ignite

import javax.cache.Cache
import com.jasonmar.ignite.util.AutoClose
import org.apache.ignite.IgniteCache
import org.apache.ignite.cache.query.{SqlFieldsQuery, SqlQuery}
import org.apache.ignite.client.ClientCache

import scala.collection.JavaConverters._
import scala.reflect.ClassTag

package object sql {

  def sqlQuery[K, V](cache: IgniteCache[K, V], q: String, args: Any*)(
      implicit tag: ClassTag[V]): Option[Array[Cache.Entry[K, V]]] = {

    val sqlQuery: SqlQuery[K, V] = new SqlQuery(tag.runtimeClass, q)
    sqlQuery.setArgs(args.map(_.asInstanceOf[AnyRef]): _*)
    AutoClose
      .autoClose(cache.query(sqlQuery)) { r =>
        r.iterator().asScala.toArray
      }
      .toOption
  }

  def sqlQueryClient[K, V](cache: ClientCache[K, V], q: String, args: Any*)(
    implicit tag: ClassTag[V]): Option[Array[Cache.Entry[K, V]]] = {

    val sqlQuery: SqlQuery[K, V] = new SqlQuery(tag.runtimeClass, q)
    sqlQuery.setArgs(args.map(_.asInstanceOf[AnyRef]): _*)
    AutoClose
      .autoClose(cache.query(sqlQuery)) { r =>
        r.iterator().asScala.toArray
      }
      .toOption
  }

  /** Provides a wrapper around SqlFieldsQuery which automatically closes the cursor after transforming the result set
    *
    * @param cache cache to be queries
    * @param q Ignite SQL Query
    * @param f A function that transforms the result set
    * @tparam T return type from transformation of the result set
    * @return
    */
  def sqlFieldsQuery[T](cache: IgniteCache[_, _],
                        q: String,
                        f: (Iterator[java.util.List[_]]) => T,
                        args: Any*): Option[T] = {

    val sqlFieldQuery = new SqlFieldsQuery(q)
    sqlFieldQuery.setArgs(args.map(_.asInstanceOf[AnyRef]): _*)
    AutoClose
      .autoClose(cache.query(sqlFieldQuery)) { r =>
        f(r.iterator().asScala)
      }
      .toOption
  }

  /** Provides a wrapper around SqlFieldsQuery which automatically closes the cursor after transforming the result set
    *
    * @param cache cache to be queries
    * @param q Ignite SQL Query
    * @param f A function that transforms the result set
    * @tparam T return type from transformation of the result set
    * @return
    */
  def sqlFieldsClientQuery[T](cache: ClientCache[_, _],
                        q: String,
                        f: (Iterator[java.util.List[_]]) => T,
                        args: Any*): Option[T] = {

    val sqlFieldQuery = new SqlFieldsQuery(q)
    sqlFieldQuery.setArgs(args.map(_.asInstanceOf[AnyRef]): _*)
    AutoClose
      .autoClose(cache.query(sqlFieldQuery)) { r =>
        f(r.iterator().asScala)
      }
      .toOption
  }
}
