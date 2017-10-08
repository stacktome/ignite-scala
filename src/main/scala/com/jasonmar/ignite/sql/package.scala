package com.jasonmar.ignite

import javax.cache.Cache

import com.jasonmar.ignite.util.AutoClose
import org.apache.ignite.IgniteCache
import org.apache.ignite.cache.query.{FieldsQueryCursor, SqlFieldsQuery, SqlQuery}

import scala.collection.JavaConverters._
import scala.collection.mutable

package object sql {

  def sqlQuery[K,V](cache: IgniteCache[K,V], valueClass: Class[_], q: String): Option[Array[Cache.Entry[K,V]]] = {
    val sqlQuery: SqlQuery[K,V] = new SqlQuery(valueClass, q)
    AutoClose.autoClose(cache.query(sqlQuery)){r =>
      r.iterator()
        .asScala
        .toArray
    }.toOption
  }

  def sqlFieldsQuery[T](cache: IgniteCache[_,_], q: String): Option[Array[T]] = {
    AutoClose.autoClose(cache.query(new SqlFieldsQuery(q))){r =>
      val a = Array.newBuilder[Any]
      r.iterator().asScala.foreach(l => a += l.get(0))
      a.result().asInstanceOf[Array[T]]
    }.toOption
  }

  def sqlFieldsQuery2[T,T2](cache: IgniteCache[_,_], q: String): Option[(Array[T],Array[T2])] = {
    AutoClose.autoClose(cache.query(new SqlFieldsQuery(q)))(getCols2[T,T2]).toOption
  }

  def sqlFieldsQuery3[T,T2,T3](cache: IgniteCache[_,_], q: String): Option[(Array[T],Array[T2],Array[T3])] = {
    AutoClose.autoClose(cache.query(new SqlFieldsQuery(q)))(getCols3[T,T2,T3]).toOption
  }

  def sqlFieldsQuery4[T,T2,T3,T4](cache: IgniteCache[_,_], q: String): Option[(Array[T],Array[T2],Array[T3],Array[T4])] = {
    AutoClose.autoClose(cache.query(new SqlFieldsQuery(q)))(getCols4[T,T2,T3,T4]).toOption
  }

  def getCols2[T,T2](cursor: FieldsQueryCursor[java.util.List[_]]): (Array[T], Array[T2]) = {
    val b: Array[mutable.ArrayBuilder[Any]] = Array.fill[mutable.ArrayBuilder[Any]](2)(Array.newBuilder[Any])
    cursor.iterator().asScala.foreach{l =>
      b(0) += l.get(0)
      b(1) += l.get(0)
    }

    (
      b(0).result().asInstanceOf[Array[T]],
      b(1).result().asInstanceOf[Array[T2]]
    )
  }

  def getCols3[T,T2,T3](cursor: FieldsQueryCursor[java.util.List[_]]): (Array[T], Array[T2], Array[T3]) = {
    val b: Array[mutable.ArrayBuilder[Any]] = Array.fill[mutable.ArrayBuilder[Any]](3)(Array.newBuilder[Any])
    cursor.iterator().asScala.foreach{l =>
      for (i <- 0 until 3) { b(i) += l.get(i) }
    }

    (
      b(0).result().asInstanceOf[Array[T]],
      b(1).result().asInstanceOf[Array[T2]],
      b(2).result().asInstanceOf[Array[T3]]
    )
  }

  def getCols4[T,T2,T3,T4](cursor: FieldsQueryCursor[java.util.List[_]]): (Array[T], Array[T2], Array[T3], Array[T4]) = {
    val b: Array[mutable.ArrayBuilder[Any]] = Array.fill[mutable.ArrayBuilder[Any]](4)(Array.newBuilder[Any])
    cursor.iterator().asScala.foreach{l =>
      for (i <- 0 until 4) { b(i) += l.get(i) }
    }

    (
      b(0).result().asInstanceOf[Array[T]],
      b(1).result().asInstanceOf[Array[T2]],
      b(2).result().asInstanceOf[Array[T3]],
      b(3).result().asInstanceOf[Array[T4]]
    )
  }
}
