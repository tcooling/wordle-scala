package com.tcooling.wordle.model

import cats.Show

final case class WordGuess(letterGuesses: List[LetterGuess])

object WordGuess {
  implicit val showWordGuess: Show[WordGuess] = Show.show(_.letterGuesses.map(_.letter).mkString)
}
