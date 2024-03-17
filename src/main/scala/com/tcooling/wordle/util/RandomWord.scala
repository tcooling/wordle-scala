package com.tcooling.wordle.util

import com.tcooling.wordle.model.TargetWord
import cats.implicits._
import cats.data.NonEmptySet
import cats.effect.kernel.Sync
import cats.effect.std.Random

object RandomWord {

  def chooseRandomWord[F[_] : Sync](words: NonEmptySet[String]): F[TargetWord] =
    for {
      random          <- Random.scalaUtilRandom[F]
      randomWordIndex <- random.nextIntBounded(words.length)
      word = words.toNonEmptyList.toList(randomWordIndex)
    } yield TargetWord.apply(word)

}
