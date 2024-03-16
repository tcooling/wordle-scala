package com.tcooling.wordle.parser

import cats.effect.IO
import cats.effect.Resource
import com.tcooling.wordle.model.Filename

import scala.io.Source



object WordsReader[F[_]] extends FileReader[F] {

  override def getLines(filename: Filename): Resource[F, List[String]] = Resource
    .make(IO.blocking(Source.fromResource(filename.value)))(source => IO.delay(source.close()))
    .map(_.getLines().toList)

}
