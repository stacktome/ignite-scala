package com.jasonmar.ignite

import javax.cache.Cache

import com.jasonmar.ignite.util.AutoClose
import org.apache.ignite.IgniteCache
import org.apache.ignite.cache.query.{FieldsQueryCursor, QueryCursor, SqlQuery}

import scala.util.Try
import scala.collection.JavaConverters._

package object sql {

  def getAllQ[K,V](cache: IgniteCache[K,V], valueClass: Class[_], q: String): Option[Iterator[(K,V)]] = {
    AutoClose.autoClose(cache.query(new SqlQuery(valueClass, q)))(getAll[K,V]).toOption
  }

  def getOneQ[K,V](cache: IgniteCache[K,V], valueClass: Class[_], q: String): Option[(K,V)] = {
    AutoClose.autoClose(cache.query(new SqlQuery(valueClass, q)))(getOne[K,V]).toOption.flatten
  }

  def getOne[K,V](cursor: QueryCursor[Cache.Entry[Nothing,Nothing]]): Option[(K,V)] = {
    Try{
      val x = cursor.iterator().next()
      (x.getKey.asInstanceOf[K],x.getValue.asInstanceOf[V])
    }.toOption
  }

  def getAll[K,V](cursor: QueryCursor[Cache.Entry[Nothing,Nothing]]): Iterator[(K,V)] = {
    cursor.iterator().asScala.map{x =>
      (x.getKey.asInstanceOf[K], x.getValue.asInstanceOf[V])
    }
  }

  def getCol[T](cursor: FieldsQueryCursor[java.util.List[_]]): Option[Iterator[T]] = {
    val it = cursor
      .iterator()
      .asScala
      .map{_.get(0).asInstanceOf[T]}
    if (it.hasNext) Some(it)
    else None
  }

  def getCols[T1,T2](cursor: FieldsQueryCursor[java.util.List[_]]): Option[Iterator[(T1,T2)]] = {
    val it = cursor.iterator().asScala.map{t => (t.get(0).asInstanceOf[T1], t.get(1).asInstanceOf[T2])}
    if (it.hasNext) Some(it)
    else None
  }

}
