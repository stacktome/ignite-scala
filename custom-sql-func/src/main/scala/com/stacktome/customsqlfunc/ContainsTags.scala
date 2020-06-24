package com.stacktome.customsqlfunc
import org.apache.ignite.cache.query.annotations.QuerySqlFunction

import scala.util.matching.Regex

trait ContainsTags {
  private val splitTagsRegex: Regex = "\\s*,\\s*".r

  @QuerySqlFunction def sqr(x: Int): Int = x * x

  @QuerySqlFunction def containsTags(listString: String, tagsString: String): Boolean = {
    if (listString != null && tagsString != null) {
      val list: Set[String] = splitTagsRegex.split(listString).toSet.filter(_.nonEmpty)
      val tags: Set[String] = splitTagsRegex.split(tagsString).toSet.filter(_.nonEmpty)
      (list intersect tags).nonEmpty
    } else false
  }

  @QuerySqlFunction def containsAllTags(listString: String, tagsString: String): Boolean = {
    if (listString != null && tagsString != null) {
      val list: Set[String] = splitTagsRegex.split(listString).toSet.filter(_.nonEmpty)
      val tags: Set[String] = splitTagsRegex.split(tagsString).toSet.filter(_.nonEmpty)
      tags.forall(list.contains) && tags.nonEmpty
    } else false
  }
}
