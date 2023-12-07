package com.tcooling.wordle.model

/**
 * Possible errors from parsing the words file
 */
enum WordsParserError {
  case FileParseError, EmptyFileError, InvalidWordsError
}
