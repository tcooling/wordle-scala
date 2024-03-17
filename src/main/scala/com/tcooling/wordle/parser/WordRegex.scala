package com.tcooling.wordle.parser

import scala.util.matching.Regex

object WordRegex {

  // Match a word with just lower or uppercase a to z
  private val NoSpecialCharsWord: Regex = "([a-zA-Z])*".r

  /**
   * Validate a given input word against the regex, returning true if it matches
   */
  def validate(word: String): Boolean = word match {
    case NoSpecialCharsWord(_) => true
    case _                     => false
  }

}
