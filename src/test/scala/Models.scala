import org.apache.ignite.cache.query.annotations.QuerySqlField

import scala.annotation.meta.field

case class Boo(@(QuerySqlField @field)(index = true) d: Int, @(QuerySqlField @field)(index = true) fooId: Long = 1)
case class Foo(@(QuerySqlField @field)(index = true) name: String, @(QuerySqlField @field)(index = true) id: Long = 1)
