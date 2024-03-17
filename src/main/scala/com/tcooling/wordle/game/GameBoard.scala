package com.tcooling.wordle.game

import com.tcooling.wordle.model.{NumberOfGuesses, WordGuess, WordLength}

import scala.annotation.tailrec

object GameBoard {

  private val StartSeparator: Char = '['
  private val EndSeparator: Char   = ']'

  /**
   * Generate the game board and put each user guess on the board in the correct colour
   */
  def generateGameBoard(wordLength: WordLength.Type,
                        numberOfGuesses: NumberOfGuesses.Type,
                        previousGuesses: List[WordGuess]): List[String] = {
    val emptyRow = List.fill(wordLength.value)(s"$StartSeparator $EndSeparator")

    @tailrec
    def loop(previousGuesses: List[WordGuess], numberOfGuesses: Int, gameBoard: List[String]): List[String] =
      if (numberOfGuesses == 0) gameBoard
      else
        previousGuesses match {
          case head :: tail => loop(tail, numberOfGuesses - 1, gameBoard :+ head.boardRow)
          case Nil          => loop(Nil, numberOfGuesses - 1, gameBoard :+ emptyRow.mkString)
        }

    loop(previousGuesses, numberOfGuesses.value, gameBoard = Nil)
  }

  extension (wordGuess: WordGuess) {

    /**
     * Given the [[WordGuess]], create the string to be printed. The only character that is displayed using a console
     * colour is the letter guess itself, which is between a start and end separator.
     */
    def boardRow: String = wordGuess.letterGuesses.map { letterGuess =>
      s"$StartSeparator${letterGuess.showWithColour}$EndSeparator"
    }.mkString
  }

}
