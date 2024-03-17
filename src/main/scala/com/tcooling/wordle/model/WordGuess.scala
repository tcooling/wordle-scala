package com.tcooling.wordle.model

import cats.Show
import cats.implicits.toShow
import com.tcooling.wordle.model.LetterGuess.{Correct, Incorrect, WrongPosition}

final case class WordGuess(letterGuesses: List[LetterGuess])

object WordGuess {

  /**
   * From the user input word guess, create the [[WordGuess]] model. At this stage we have validated the length of the
   * target word and length of the guess so they can be zipped up and compared.
   */
  def apply(userInput: UserInputGuess.Type, targetWord: TargetWord.Type): WordGuess =
    WordGuess(userInput.value.zip(targetWord.value).toList.map {
      case (letterGuess, target) if letterGuess == target             => Correct(letterGuess)
      case (letterGuess, _) if targetWord.value.contains(letterGuess) => WrongPosition(letterGuess)
      case (letterGuess, _)                                           => Incorrect(letterGuess)
    })

  given Show[WordGuess] = Show.show(_.letterGuesses.map(_.letter.show).mkString)

}
