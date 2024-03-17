package com.tcooling.wordle.parser

import cats.syntax.all.*
import cats.data.NonEmptySet
import com.tcooling.wordle.model.{UserInputError, UserInputGuess, WordLength}
import com.tcooling.wordle.model.UserInputError.{IncorrectLength, NonLetterCharacter, WordDoesNotExist}

object UserInputParser {

  /**
   * Validate that the user input is of the correct length, only contains valid characters and is a valid word.
   */
  def parseGuess(allWords: NonEmptySet[String],
                 userInput: UserInputGuess,
                 wordLength: WordLength): Either[UserInputError, UserInputGuess] =
    for {
      _ <-
        if (userInput.value.length == wordLength.value) userInput.asRight else IncorrectLength(wordLength.value).asLeft
      guess <- WordRegex.validate[Either[UserInputError, UserInputGuess]](
        word = userInput,
        ifMatchesRegex = UserInputGuess(userInput.value.toUpperCase).asRight,
        ifDoesNotMatchRegex = NonLetterCharacter.asLeft
      )
      validGuess <- if (allWords.contains(guess.value)) guess.asRight else WordDoesNotExist.asLeft
    } yield validGuess

}
