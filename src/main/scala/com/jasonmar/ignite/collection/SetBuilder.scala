/*
 * Copyright 2017 Jason Mar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jasonmar.ignite.collection

import org.apache.ignite.{Ignite, IgniteSet}

trait SetBuilder[T] extends CollectionBuilder[T] {
  def buildSet()(implicit ignite: Ignite): IgniteSet[T] = {
    ignite.set[T](name, buildConfig())
  }

  def getSet()(implicit ignite: Ignite): IgniteSet[T] = {
    ignite.set[T](name, null)
  }
}
