package com.tcooling.wordle.game

import com.tcooling.wordle.model.{boardRow, NumberOfGuesses, WordGuess, WordLength}
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers

final class GameBoardTest extends AnyWordSpecLike with Matchers {

  private val startSeparator: Char        = '['
  private val endSeparator: Char          = ']'
  private val wordLength: WordLength      = WordLength.apply(5)
  private val numGuesses: NumberOfGuesses = NumberOfGuesses.apply(6)
  private val targetWord: String          = "VAGUE"

  private val wordGuessF: String => WordGuess = WordGuess(_, targetWord)
  private val generateGameBoardF: List[WordGuess] => List[String] =
    GameBoard.generateGameBoard(wordLength, numGuesses, _)

  "GameBoard" should {
    "generate a board row" when {
      "none of the letters of the guess are in the target word" in {
        wordGuessF("QQQQQ").boardRow shouldBe createRow(Console.RED, "QQQQQ")
      }

      "all of the letters of the guess are in the target word in the correct position" in {
        wordGuessF(targetWord).boardRow shouldBe createRow(Console.GREEN, targetWord)
      }

      "all of the letters of the guess are in the target word in the wrong position" in {
        val incorrectPositionWord = "AVUGA"
        wordGuessF(incorrectPositionWord).boardRow shouldBe createRow(Console.YELLOW, incorrectPositionWord)
      }

      "there are a mix of letters that are valid, in the incorrect position and invalid" in {
        wordGuessF("HAVEN").boardRow shouldBe List(
          createSquare(Console.RED, 'H'),
          createSquare(Console.GREEN, 'A'),
          createSquare(Console.YELLOW, 'V'),
          createSquare(Console.YELLOW, 'E'),
          createSquare(Console.RED, 'N')
        ).mkString
      }
    }

    "generate the whole game board" when {
      val emptyRow = List.fill(wordLength.value)(s"$startSeparator $endSeparator").mkString

      "no guesses have been input" in {
        generateGameBoardF(Nil) shouldBe List.fill(numGuesses.value)(emptyRow)
      }

      "one guess has been input" in {
        generateGameBoardF(List(WordGuess(targetWord, targetWord))) shouldBe List(
          createRow(Console.GREEN, targetWord),
          emptyRow,
          emptyRow,
          emptyRow,
          emptyRow,
          emptyRow
        )
      }

      "all the guesses have been input" in {
        val words = List("UEAVG", "XXXXX", "QQQQQ", "WWWWW", "ZZZZZ", targetWord)
        generateGameBoardF(words.map(WordGuess(_, targetWord))) shouldBe List(
          createRow(Console.YELLOW, "UEAVG"),
          createRow(Console.RED, "XXXXX"),
          createRow(Console.RED, "QQQQQ"),
          createRow(Console.RED, "WWWWW"),
          createRow(Console.RED, "ZZZZZ"),
          createRow(Console.GREEN, targetWord)
        )
      }
    }
  }

  private def createRow(consoleColour: String, word: String): String =
    word.map(createSquare(consoleColour, _)).mkString

  private def createSquare(consoleColour: String, letter: Char): String =
    s"$startSeparator$consoleColour$letter${Console.RESET}$endSeparator"

}
