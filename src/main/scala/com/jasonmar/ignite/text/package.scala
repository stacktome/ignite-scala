package com.jasonmar.ignite

import javax.cache.Cache
import com.jasonmar.ignite.util.AutoClose
import org.apache.ignite.IgniteCache
import org.apache.ignite.cache.query.TextQuery
import org.apache.ignite.client.ClientCache

import scala.collection.JavaConverters._
import scala.reflect.ClassTag
import scala.util.Try

package object text {

  /** Provides a wrapper around TextQuery which automatically closes the cursor after transforming the result set
    *
    * @param cache cache to be queries
    * @param q Ignite SQL Query
    * @return
    */
  def textClientQuery[K, V](cache: ClientCache[_, _], q: String)(
      implicit tag: ClassTag[V]): Try[Array[Cache.Entry[K, V]]] = {
    val txtQuery: TextQuery[K, V] = new TextQuery(tag.runtimeClass, q)
    Try(cache.query(txtQuery).getAll.asScala.toArray)
  }

  /** Provides a wrapper around TextQuery which automatically closes the cursor after transforming the result set
    *
    * @param cache cache to be queries
    * @param q Ignite SQL Query
    * @return
    */
  def textQuery[K, V](cache: IgniteCache[_, _], q: String)(implicit tag: ClassTag[V]): Try[Array[Cache.Entry[K, V]]] = {
    val txtQuery: TextQuery[K, V] = new TextQuery(tag.runtimeClass, q)
    Try(cache.query(txtQuery).getAll.asScala.toArray)
  }
}
