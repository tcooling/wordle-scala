package com.tcooling.wordle.util

import cats.data.NonEmptySet

import scala.util.Random

object RandomWord {

  def chooseRandomWord(words: NonEmptySet[String]): String = {
    val randomInteger = Random.nextInt(words.length)
    words.toNonEmptyList.toList(randomInteger)
  }

}
