package com.tcooling.wordle.input

import cats.data.NonEmptySet
import com.tcooling.wordle.input.ComputerInputGuessConnector.Letters
import com.tcooling.wordle.model.{LetterGuess, WordGuess, WordleConfig}

import scala.collection.breakOut
import scala.util.Random

object ComputerInputGuessConnector {
  final case class Letters(
    correctPosition:   Map[Char, Int],
    incorrectPosition: Map[Char, Int],
    notPresent:        Set[Char]
  )
}

final class ComputerInputGuessConnector(wordSet: NonEmptySet[String], wordleConfig: WordleConfig) extends GuessInputConnector {

  override def getUserInput(previousGuesses: List[WordGuess]): String = previousGuesses match {
    case Nil => firstGuess()
    case _   => secondGuess(previousGuesses)
  }

  private def firstGuess(): String = "audio"

  private def secondGuess(previousGuesses: List[WordGuess]): String = {
    println("SECOND GUESS")
    val letterPositions = getLetterPositions(previousGuesses)

    // Need to choose a word

    println(s"Number of words: ${wordSet.length}")

    // Filter out all words containing the letters that are not present anywhere
    val wordsWithoutNotPresentLetters = wordSet.filterNot(_.exists(letterPositions.notPresent.contains))

    println(s"Number of words with none of the not present letters: ${wordsWithoutNotPresentLetters.size}")

    // TODO: only use a double letter if no other options

    if (letterPositions.correctPosition.nonEmpty) {
      val abc = wordsWithoutNotPresentLetters.filter(containsLettersAtPosition(_, letterPositions.correctPosition))
      val qwe = abc.filterNot(containsLettersAtPosition(_, letterPositions.incorrectPosition))
      qwe.toList(Random.nextInt(qwe.size))
    } else {
      wordsWithoutNotPresentLetters.toList(Random.nextInt(wordsWithoutNotPresentLetters.size))
    }
  }

  private def containsLettersAtPosition(word: String, letterPositions: Map[Char, Int]): Boolean =
    letterPositions.forall { case (letter, index) =>
      word(index) == letter
    }

  private def getLetterPositions(guesses: List[WordGuess]): Letters = Letters(
    correctPosition = guesses.flatMap(_.lettersInCorrectPosition)(breakOut),
    incorrectPosition = guesses.flatMap(_.lettersInWrongPosition)(breakOut),
    notPresent = guesses.flatMap(_.lettersNotPresent).toSet
  )

  private def lettersInPosition(guesses: List[WordGuess], predicate: LetterGuess => Boolean): Map[Char, Int] =
    guesses.flatMap(_.letterGuesses.zipWithIndex.flatMap {
      case (letterGuess, index) if predicate(letterGuess) => Some(letterGuess.letter -> index)
      case _                                              => None
    })(breakOut)

}
