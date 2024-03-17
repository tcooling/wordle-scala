package com.tcooling.wordle.model

final case class WordleConfig(
    filename: Filename.Type,
    wordLength: WordLength.Type,
    numberOfGuesses: NumberOfGuesses.Type
)
