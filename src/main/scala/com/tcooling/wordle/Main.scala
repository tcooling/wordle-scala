package com.tcooling.wordle

import com.tcooling.wordle.game.Wordle
import com.tcooling.wordle.input.UserInputGuessConnector
import com.tcooling.wordle.model.WordleConfig
import com.tcooling.wordle.parser.WordsReader
import com.tcooling.wordle.util.RandomWord

object Main extends App {

  private val filename: String     = "words.txt"
  private val wordLength: Int      = 5
  private val numberOfGuesses: Int = 6

  private val config = WordleConfig(
    filename = filename,
    wordLength = wordLength,
    numberOfGuesses = numberOfGuesses
  )

  new Wordle(config, WordsReader, RandomWord.chooseRandomWord, UserInputGuessConnector).startGame()

}
