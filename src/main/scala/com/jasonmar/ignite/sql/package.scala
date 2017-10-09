package com.jasonmar.ignite

import javax.cache.Cache

import com.jasonmar.ignite.util.AutoClose
import org.apache.ignite.IgniteCache
import org.apache.ignite.cache.query.{SqlFieldsQuery, SqlQuery}

import scala.collection.JavaConverters._

package object sql {

  def sqlQuery[K,V](cache: IgniteCache[K,V], valueClass: Class[_], q: String): Option[Array[Cache.Entry[K,V]]] = {
    val sqlQuery: SqlQuery[K,V] = new SqlQuery(valueClass, q)
    AutoClose.autoClose(cache.query(sqlQuery)){r =>
      r.iterator()
        .asScala
        .toArray
    }.toOption
  }

  /** Provides a wrapper around SqlFieldsQuery which automatically closes the cursor after transforming the result set
    *
    * @param cache cache to be queries
    * @param q Ignite SQL Query
    * @param f A function that transforms the result set
    * @tparam T return type from transformation of the result set
    * @return
    */
  def sqlFieldsQuery[T](cache: IgniteCache[_,_], q: String, f: (Iterator[java.util.List[_]]) => T): Option[T] = {
    AutoClose.autoClose(cache.query(new SqlFieldsQuery(q))){r => f(r.iterator().asScala)}.toOption
  }
}
