import io.getquill.{ImplicitQuery, Literal, MirrorSqlDialect, SqlMirrorContext}
import org.scalatest.FlatSpec

/**
  * Created by evaldas on 12/27/16.
  */
class IgniteTestSpec extends FlatSpec {

  val ctx = new SqlMirrorContext(MirrorSqlDialect, Literal)
  import ctx._

  case class Boo(val d: Int = 10) extends Embedded
  case class Foo(a: Int, b: String, c: Boolean, boo: Boo)
  case class Too(a: Int, d: Int)

  "Boo " should " allow simple select" in {
    assert(ctx.run(query[Boo]).string == "SELECT x.d FROM Boo x")
  }

  "Boo " should " allow col filter" in {
    assert(ctx.run(query[Boo].filter(_.d > 10)).string == "SELECT x1.d FROM Boo x1 WHERE x1.d > 10")
  }

  "Foo " should " allows embed queries" in {
    assert(ctx.run(query[Foo]).string == "SELECT x.a, x.b, x.c, x.d FROM Foo x")
  }

  "Boo and Too " should " be able to join" in {
    assert(
      ctx
        .run(query[Too].join(query[Boo]).on((t, b) => t.d == b.d))
        .string == "SELECT t.a, t.d, b.d FROM Too t INNER JOIN Boo b ON t.d = b.d")
  }

}
