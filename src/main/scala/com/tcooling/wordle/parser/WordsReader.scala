package com.tcooling.wordle.parser

import com.tcooling.wordle.model.Filename
import scala.io.Source
import scala.util.Try

object WordsReader extends FileReader {

  override def getLines(filename: Filename): Try[List[String]] =
    Try {
      val bufferedSource = Source.fromResource(filename.value)
      val lines          = bufferedSource.getLines().toList
      bufferedSource.close()
      lines
    }

}
