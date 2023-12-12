package com.tcooling.wordle.parser

import cats.effect.IO
import cats.effect.Resource
import com.tcooling.wordle.model.Filename

import scala.io.Source

object WordsReader extends FileReader {

  override def getLines(filename: Filename): Resource[IO, List[String]] = Resource
    .make(IO.blocking(Source.fromResource(filename.value)))(source => IO.delay(source.close()))
    .map(_.getLines().toList)

}
