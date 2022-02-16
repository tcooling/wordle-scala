package com.tcooling.wordle.model

import cats.Show
import com.tcooling.wordle.model.LetterGuess.{CorrectGuess, IncorrectGuess, WrongPositionGuess}

import scala.collection.breakOut

final case class WordGuess(letterGuesses: List[LetterGuess]) {
  def lettersInCorrectPosition: Map[Char, Int] =
    letterGuesses.zipWithIndex.collect { case (x: CorrectGuess, index) => x.letter -> index }(breakOut)

  def lettersInWrongPosition: Map[Char, Int] =
    letterGuesses.zipWithIndex.collect { case (x: WrongPositionGuess, index) => x.letter -> index }(breakOut)

  def lettersNotPresent: Set[Char] =
    letterGuesses.collect { case x: IncorrectGuess => x.letter }.toSet
}

object WordGuess {
  implicit val showWordGuess: Show[WordGuess] = Show.show(_.letterGuesses.map(_.letter).mkString)

  /**
   * From the user input word guess, create the [[WordGuess]] model. At this stage we have validated the length
   * of the target word and length of the guess so they can be zipped up and compared.
   */
  def apply(userInput: String, targetWord: String): WordGuess =
    WordGuess(userInput.zip(targetWord).toList.map {
      case (letterGuess, target) if letterGuess == target       => CorrectGuess(letterGuess)
      case (letterGuess, _) if targetWord.contains(letterGuess) => WrongPositionGuess(letterGuess)
      case (letterGuess, _)                                     => IncorrectGuess(letterGuess)
    })
}
