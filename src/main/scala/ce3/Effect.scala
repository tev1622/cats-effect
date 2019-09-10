/*
 * Copyright 2019 Daniel Spiewak
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

package ce3

trait Effect[F[_]] extends Async[F] with Bracket[F, Throwable] {

  def to[G[_]]: PartiallyApplied[G]

  trait PartiallyApplied[G[_]] {
    def apply[A](fa: F[A])(implicit G: Async[G] with Bracket[G, Throwable]): G[A]
  }
}
