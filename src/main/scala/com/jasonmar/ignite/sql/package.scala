package com.jasonmar.ignite

import javax.cache.Cache

import com.jasonmar.ignite.util.AutoClose
import org.apache.ignite.IgniteCache
import org.apache.ignite.cache.query.{FieldsQueryCursor, QueryCursor, SqlFieldsQuery, SqlQuery}

import scala.collection.JavaConverters._

package object sql {

  def sqlQuery[K,V](cache: IgniteCache[K,V], valueClass: Class[_], q: String): Option[Iterator[(K,V)]] = {
    AutoClose.autoClose(cache.query(new SqlQuery(valueClass, q)))(getEntries[K,V]).toOption
  }

  def getEntries[K,V](cursor: QueryCursor[Cache.Entry[Nothing,Nothing]]): Iterator[(K,V)] = {
    cursor.iterator().asScala.map{x =>
      (x.getKey.asInstanceOf[K], x.getValue.asInstanceOf[V])
    }
  }

  def sqlFieldsQuery[T](cache: IgniteCache[_,_], q: String): Option[Iterator[T]] = {
    AutoClose.autoClose(cache.query(new SqlFieldsQuery(q)))(getCol[T]).toOption
  }

  def sqlFieldsQuery2[T1,T2](cache: IgniteCache[_,_], q: String): Option[Iterator[(T1,T2)]] = {
    AutoClose.autoClose(cache.query(new SqlFieldsQuery(q)))(getCols[T1,T2]).toOption
  }

  def sqlFieldsQuery3[T1,T2,T3](cache: IgniteCache[_,_], q: String): Option[Iterator[(T1,T2,T3)]] = {
    AutoClose.autoClose(cache.query(new SqlFieldsQuery(q)))(getCols3[T1,T2,T3]).toOption
  }

  def sqlFieldsQuery4[T1,T2,T3,T4](cache: IgniteCache[_,_], q: String): Option[Iterator[(T1,T2,T3,T4)]] = {
    AutoClose.autoClose(cache.query(new SqlFieldsQuery(q)))(getCols4[T1,T2,T3,T4]).toOption
  }

  def getCol[T](cursor: FieldsQueryCursor[java.util.List[_]]): Iterator[T] = {
    cursor.iterator().asScala.map{_.get(0).asInstanceOf[T]}
  }

  def getCols[T1,T2](cursor: FieldsQueryCursor[java.util.List[_]]): Iterator[(T1,T2)] = {
    cursor.iterator().asScala.map{t =>
      (t.get(0).asInstanceOf[T1], t.get(1).asInstanceOf[T2])
    }
  }

  def getCols3[T1,T2,T3](cursor: FieldsQueryCursor[java.util.List[_]]): Iterator[(T1,T2,T3)] = {
    cursor.iterator().asScala.map{t =>
      (t.get(0).asInstanceOf[T1], t.get(1).asInstanceOf[T2], t.get(2).asInstanceOf[T3])
    }
  }

  def getCols4[T1,T2,T3,T4](cursor: FieldsQueryCursor[java.util.List[_]]): Iterator[(T1,T2,T3,T4)] = {
    cursor.iterator().asScala.map{t =>
      (
        t.get(0).asInstanceOf[T1],
        t.get(1).asInstanceOf[T2],
        t.get(2).asInstanceOf[T3],
        t.get(3).asInstanceOf[T4]
      )
    }
  }

}
