package com.tcooling.wordle.model

import cats.Show
import cats.implicits.toShow
import com.tcooling.wordle.model.LetterGuess.{CorrectGuess, IncorrectGuess, WrongPositionGuess}
import com.tcooling.wordle.game.GameBoard.{endSeparator, startSeparator}

final case class WordGuess(letterGuesses: List[LetterGuess])

object WordGuess {
  implicit val showWordGuess: Show[WordGuess] = Show.show(_.letterGuesses.map(_.letter).mkString)

  /**
   * From the user input word guess, create the [[WordGuess]] model. At this stage we have validated the length of the
   * target word and length of the guess so they can be zipped up and compared.
   */
  def apply(userInput: String, targetWord: String): WordGuess =
    WordGuess(userInput.zip(targetWord).toList.map {
      case (letterGuess, target) if letterGuess == target       => CorrectGuess(letterGuess)
      case (letterGuess, _) if targetWord.contains(letterGuess) => WrongPositionGuess(letterGuess)
      case (letterGuess, _)                                     => IncorrectGuess(letterGuess)
    })
}

extension (wordGuess: WordGuess) {

  /**
   * Given the [[WordGuess]], create the string to be printed. The only character that is displayed using a console
   * colour is the letter guess itself, which is between a start and end separator.
   */
  def boardRow: String = wordGuess.letterGuesses.map { letterGuess =>
    s"$startSeparator${letterGuess.show}$endSeparator"
  }.mkString
}
