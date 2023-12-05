package com.tcooling.wordle.game

import cats.implicits.toShow
import com.tcooling.wordle.model.LetterGuess.showLetterGuess
import com.tcooling.wordle.model.WordGuess

import scala.annotation.tailrec

object GameBoard {

  private val startSeparator: Char = '['
  private val endSeparator:   Char = ']'

  /**
   * Generate the game board and put each user guess on the board in the correct colour
   */
  def generateGameBoard(wordLength: Int, numberOfGuesses: Int, previousGuesses: List[WordGuess]): List[String] = {
    val emptyRow = List.fill(wordLength)(s"$startSeparator $endSeparator")

    @tailrec
    def loop(previousGuesses: List[WordGuess], numberOfGuesses: Int, gameBoard: List[String]): List[String] =
      if (numberOfGuesses == 0) gameBoard
      else
        previousGuesses match {
          case head :: tail => loop(tail, numberOfGuesses - 1, gameBoard :+ generateBoardRow(head))
          case Nil          => loop(Nil, numberOfGuesses - 1, gameBoard :+ emptyRow.mkString)
        }

    loop(previousGuesses, numberOfGuesses, gameBoard = Nil)
  }

  /**
   * Given the [[WordGuess]], create the string to be printed. The only character that is displayed using a console
   * colour is the letter guess itself, which is between a start and end separator.
   */
  def generateBoardRow(wordGuess: WordGuess): String = wordGuess.letterGuesses.map { letterGuess =>
    s"$startSeparator${letterGuess.show}$endSeparator"
  }.mkString

}
