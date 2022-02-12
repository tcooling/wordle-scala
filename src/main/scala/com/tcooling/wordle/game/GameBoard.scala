package com.tcooling.wordle.game

import com.tcooling.wordle.model.{WordGuess, WordleConfig}

import scala.annotation.tailrec

object GameBoard {

  /**
   * Generate the game board and put each user guess on the board in the correct colour
   */
  def generateGameBoard(config: WordleConfig, previousGuesses: List[WordGuess]): List[String] = {
    val row = List.fill(config.wordLength)(config.startSeparator + " " + config.endSeparator)

    @tailrec
    def loop(previousGuesses: List[WordGuess], numberOfGuesses: Int, gameBoard: List[String]): List[String] =
      if (numberOfGuesses == 0) gameBoard
      else
        previousGuesses match {
          case head :: tail => loop(tail, numberOfGuesses - 1, gameBoard :+ generateBoardRow(config, head))
          case Nil          => loop(Nil, numberOfGuesses - 1, gameBoard :+ row.mkString)
        }

    loop(previousGuesses, config.numberOfGuesses, gameBoard = Nil)
  }

  /**
   * Given the [[WordGuess]], create the string to be printed. The only character that is displayed using a console
   * colour is the letter guess itself, which is between a start and end separator. The console needs to be reset to
   * prevent subsequent characters using the previously used colour.
   */
  def generateBoardRow(config: WordleConfig, wordGuess: WordGuess): String =
    wordGuess.letterGuesses.map { lg =>
      config.startSeparator + lg.consoleColour + lg.letter + Console.RESET + config.endSeparator
    }.mkString

  def printGameBoard(gameBoardRows: List[String]): Unit = println(gameBoardRows.mkString("\n"))

}
