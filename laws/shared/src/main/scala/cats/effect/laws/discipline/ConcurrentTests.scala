/*
 * Copyright (c) 2017-2018 The Typelevel Cats-effect Project Developers
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

package cats
package effect
package laws
package discipline

import cats.data._
import cats.laws.discipline._
import cats.laws.discipline.SemigroupalTests.Isomorphisms
import org.scalacheck._, Prop.forAll

trait ConcurrentTests[F[_]] extends AsyncTests[F] {
  def laws: ConcurrentLaws[F]

  def concurrent[A: Arbitrary: Eq, B: Arbitrary: Eq, C: Arbitrary: Eq](
    implicit
    ArbFA: Arbitrary[F[A]],
    ArbFB: Arbitrary[F[B]],
    ArbFC: Arbitrary[F[C]],
    ArbFU: Arbitrary[F[Unit]],
    ArbFAtoB: Arbitrary[F[A => B]],
    ArbFBtoC: Arbitrary[F[B => C]],
    ArbT: Arbitrary[Throwable],
    CogenA: Cogen[A],
    CogenB: Cogen[B],
    CogenC: Cogen[C],
    CogenT: Cogen[Throwable],
    EqFA: Eq[F[A]],
    EqFB: Eq[F[B]],
    EqFC: Eq[F[C]],
    EqFU: Eq[F[Unit]],
    EqT: Eq[Throwable],
    EqFEitherTU: Eq[F[Either[Throwable, Unit]]],
    EqFEitherTA: Eq[F[Either[Throwable, A]]],
    EqEitherTFTA: Eq[EitherT[F, Throwable, A]],
    EqFABC: Eq[F[(A, B, C)]],
    EqFInt: Eq[F[Int]],
    iso: Isomorphisms[F]): RuleSet = {
    new RuleSet {
      val name = "concurrent"
      val bases = Nil
      val parents = Seq(async[A, B, C])
      val props = Seq(
        "async cancelable coherence" -> forAll(laws.asyncCancelableCoherence[A] _),
        "async cancelable receives cancel signal" -> forAll(laws.asyncCancelableReceivesCancelSignal[A] _),
        "start then join is identity" -> forAll(laws.startJoinIsIdentity[A] _),
        "join is idempotent" -> forAll(laws.joinIsIdempotent[A] _),
        "start.flatMap(_.cancel) is unit" -> forAll(laws.startCancelIsUnit[A] _),
        "uncancelable mirrors source" -> forAll(laws.uncancelableMirrorsSource[A] _),
        "uncancelable prevents cancelation" -> forAll(laws.uncancelablePreventsCancelation[A] _),
        "onCancelRaiseError mirrors source" -> forAll(laws.onCancelRaiseErrorMirrorsSource[A] _),
        "onCancelRaiseError terminates on cancel" -> forAll(laws.onCancelRaiseErrorTerminatesOnCancel[A] _),
        "onCancelRaiseError can cancel source" -> forAll(laws.onCancelRaiseErrorCanCancelSource[A] _),
        "race mirrors left winner" -> forAll(laws.raceMirrorsLeftWinner[A] _),
        "race mirrors right winner" -> forAll(laws.raceMirrorsRightWinner[A] _),
        "race cancels loser" -> forAll(laws.raceCancelsLoser[A, B] _),
        "race cancels both" -> forAll(laws.raceCancelsBoth[A, B, C] _),
        "race pair cancels loser" -> forAll(laws.racePairCancelsLoser[A, B] _),
        "race pair map2 coherence" -> forAll(laws.racePairMap2Coherence[A, B, C] _),
        "race pair cancels both" -> forAll(laws.racePairCancelsBoth[A, B, C] _)
      )
    }
  }
}

object ConcurrentTests {
  def apply[F[_]: Concurrent]: ConcurrentTests[F] = new ConcurrentTests[F] {
    def laws = ConcurrentLaws[F]
  }
}
