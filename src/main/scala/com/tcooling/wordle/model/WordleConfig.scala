package com.tcooling.wordle.model

final case class WordleConfig(
  filename:        String,
  wordLength:      Int,
  numberOfGuesses: Int
)
