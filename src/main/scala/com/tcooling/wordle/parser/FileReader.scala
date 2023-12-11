package com.tcooling.wordle.parser

import cats.effect.IO
import cats.effect.Resource
import com.tcooling.wordle.model.Filename

/**
 * Implement for a particular type of file, e.g. words .txt file
 */
trait FileReader {
  def getLines(filename: Filename): Resource[IO, List[String]]
}
