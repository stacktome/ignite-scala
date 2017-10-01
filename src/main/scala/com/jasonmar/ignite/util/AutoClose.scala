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

package com.jasonmar.ignite.util

import scala.util.{Failure, Success, Try}

object AutoClose {

  /**
    * https://www.phdata.io/try-with-resources-in-scala/
    * @param resource a resource which needs to be closed to avoid a memory leak
    * @param onComplete function to be applied to resource after work is completed
    * @param f function which uses the resource
    * @tparam A resource type
    * @tparam B return type
    * @return
    */
  def onComplete[A, B](resource: A)(onComplete: A => Unit)(f: A => B): Try[B] = {
    try {
      Success(f(resource))
    } catch {
      case e: Exception =>
        Failure(e)
    } finally {
      try {
        if (resource != null) {
          onComplete(resource)
        }
      } catch {
        case e: Exception =>
          println(e) // should be logged
      }
    }
  }

  def autoClose[A<:AutoCloseable, B](closeable: A)(f: A => B): Try[B] = {
    onComplete(closeable)(_.close())(f)
  }

  /** Useful for CLI applications
    *
    */
  def autoCloseWithShutdownHook[A<:AutoCloseable, B](closeable: A)(f: A => B): Try[B] = {
    Runtime.getRuntime.addShutdownHook(new Thread(){
      override def run(): Unit = {
        try {
          System.out.println("Closing")
          closeable.close()
        } catch {
          case e: InterruptedException =>
        }
      }
    })
    onComplete(closeable)(_.close())(f)
  }
}
