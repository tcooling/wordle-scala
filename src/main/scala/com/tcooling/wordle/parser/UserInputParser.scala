package com.tcooling.wordle.parser

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
      _ <- if (userInput.length == wordLength.value) Right(userInput) else Left(IncorrectLength(wordLength.value))
      guess <- WordRegex.validate[Either[UserInputError, UserInputGuess]](
        word = userInput,
        ifMatchesRegex = Right(UserInputGuess(userInput.value.toUpperCase)),
        ifDoesNotMatchRegex = Left(NonLetterCharacter)
      )
      validGuess <- if (allWords.contains(guess)) Right(guess) else Left(WordDoesNotExist)
    } yield validGuess

}
