import org.apache.ignite.cache.query.annotations.{QuerySqlField, QueryTextField}

import scala.annotation.meta.field

case class Boo(@(QuerySqlField @field)(index = true) d: Int, @(QuerySqlField @field)(index = true) fooId: Long = 1)
case class Foo(@(QuerySqlField @field)(index = true) name: String,
               @(QuerySqlField @field)(index = true) id: Long = 1,
               @(QueryTextField @field) desc: String = "")
