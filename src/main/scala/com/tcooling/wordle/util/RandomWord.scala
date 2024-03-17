package com.tcooling.wordle.util

import com.tcooling.wordle.model.TargetWord
import cats.implicits.*
import cats.data.NonEmptySet
import cats.effect.kernel.Sync
import cats.effect.std.Random

trait RandomWord[F[_]] {
  def chooseRandomWord(words: NonEmptySet[String]): F[TargetWord.Type]
}

object RandomWord {

  def apply[F[_] : Sync](): RandomWord[F] = new RandomWord[F] {
    override def chooseRandomWord(words: NonEmptySet[String]): F[TargetWord.Type] =
      for {
        random          <- Random.scalaUtilRandom[F]
        randomWordIndex <- random.nextIntBounded(words.length)
        word = words.toNonEmptyList.toList(randomWordIndex)
      } yield TargetWord(word)
  }

}
