import org.scalatest.FlatSpec
import com.stacktome.customsqlfunc.CustomSqlFunc._

class CustomSqlFuncSpec extends FlatSpec {
  "containsTags" should "work" in {
    assert(containsTags("1,2,3", "2"))
    assert(!containsTags("1,2,,3", "22"))
    assert(containsTags("3,7", "22,3,,,1"))
    assert(containsTags("3", "3"))
    assert(!containsTags("", "3"))
    assert(!containsTags("3", ""))
    assert(!containsTags("", ""))
    assert(!containsTags(null, null))
  }

  "containsAllTags" should "work" in {
    assert(containsAllTags("1,2,3", "2"))
    assert(containsAllTags("1,2,,3", "2,1,,,"))
    assert(!containsAllTags("1,2,3", "22"))
    assert(!containsAllTags("33", "22,3,1"))
    assert(containsAllTags("3", "3"))
    assert(!containsAllTags("", "3"))
    assert(!containsAllTags("3", ""))
    assert(!containsAllTags("", ""))
    assert(!containsTags(null, null))
  }

}
