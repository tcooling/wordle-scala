package com.tcooling.wordle.parser

import cats.data.NonEmptySet
import com.tcooling.wordle.model.UserInputError
import com.tcooling.wordle.model.UserInputError.{IncorrectLength, NonLetterCharacter, WordDoesNotExist}

object UserInputParser {

  /**
   * Validate that the user input is of the correct length, only contains valid characters and is a valid word.
   */
  def parseGuess(allWords: NonEmptySet[String], userInput: String, wordLength: Int): Either[UserInputError, String] =
    for {
      _ <- if (userInput.length == wordLength) Right(userInput) else Left(IncorrectLength(wordLength))
      guess <- WordRegex.validate[Either[UserInputError, String]](
        word = userInput,
        ifMatchesRegex = Right(userInput.toUpperCase),
        ifDoesNotMatchRegex = Left(NonLetterCharacter)
      )
      validGuess <- if (allWords.contains(guess)) Right(guess) else Left(WordDoesNotExist)
    } yield validGuess

}
