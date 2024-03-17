package com.tcooling.wordle.model

import cats.Show
import cats.implicits.toShow
import com.tcooling.wordle.model.{TargetWord, UserInputGuess}
import com.tcooling.wordle.model.LetterGuess.{Correct, Incorrect, WrongPosition}
import com.tcooling.wordle.game.GameBoard.{EndSeparator, StartSeparator}

final case class WordGuess(letterGuesses: List[LetterGuess])

object WordGuess {
  // TODO: is this used?
  implicit val showWordGuess: Show[WordGuess] = Show.show(_.letterGuesses.map(_.letter).mkString)

  /**
   * From the user input word guess, create the [[WordGuess]] model. At this stage we have validated the length of the
   * target word and length of the guess so they can be zipped up and compared.
   */
  def apply(userInput: UserInputGuess, targetWord: TargetWord): WordGuess =
    WordGuess(userInput.value.zip(targetWord.value).toList.map {
      case (letterGuess, target) if letterGuess == target             => Correct(letterGuess)
      case (letterGuess, _) if targetWord.value.contains(letterGuess) => WrongPosition(letterGuess)
      case (letterGuess, _)                                           => Incorrect(letterGuess)
    })
}

extension (wordGuess: WordGuess) {

  // TODO: should this be a show?
  /**
   * Given the [[WordGuess]], create the string to be printed. The only character that is displayed using a console
   * colour is the letter guess itself, which is between a start and end separator.
   */
  def boardRow: String = wordGuess.letterGuesses.map { letterGuess =>
    s"$StartSeparator${letterGuess.show}$EndSeparator"
  }.mkString
}
