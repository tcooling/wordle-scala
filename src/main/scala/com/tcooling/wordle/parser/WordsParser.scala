package com.tcooling.wordle.parser

import cats.{Applicative, Monad, Parallel}
import cats.implicits.*
import cats.effect.*
import cats.effect.std.Console
import cats.effect.Resource
import cats.data.{EitherT, NonEmptyList, NonEmptySet, ReaderT}
import com.tcooling.wordle.util.Syntax.evalTap
import com.tcooling.wordle.model.WordleConfig
import com.tcooling.wordle.model.{WordLength, WordsParserError}
import com.tcooling.wordle.model.WordsParserError.{EmptyFileError, FileParseError, InvalidWordsError}

trait WordsParser[F[_]] {
  def parseWords(): F[Either[WordsParserError, NonEmptySet[String]]]
}

object WordsParser {
  def apply[F[_] : Applicative : MonadCancelThrow : Parallel](config: WordleConfig,
                                                              fileReader: FileReader[F]): WordsParser[F] =
    new WordsParser[F] {
      override def parseWords(): F[Either[WordsParserError, NonEmptySet[String]]] = {
        // TODO: performance test if parTraverse is faster than not here
        val fileReaderR: Resource[F, List[String]] = fileReader.getLines(config.filename)
        for {
          lines         <- EitherT(fileReaderR.use(_.pure).attempt.map(_.leftMap(_ => FileParseError)))
          nonEmptyLines <- EitherT.fromEither(NonEmptyList.fromList(lines).map(_.toNes).toRight(EmptyFileError))
          validatedLines <- EitherT(
            nonEmptyLines.toSortedSet.toList
              .parTraverse(parseLine)
              .map(maybeValidLines =>
                NonEmptyList.fromList(maybeValidLines.flatten).map(_.toNes).toRight(InvalidWordsError)))
        } yield validatedLines
      }.value

      private def parseLine(line: String): F[Option[String]] = {
        val validatedLine =
          if (line.length == config.wordLength.value && WordRegex.validate(line)) {
            Some(line.toUpperCase)
          } else {
            None
          }
        validatedLine.pure
      }
    }

  def observed[F[_] : Monad : Console](
      delegate: WordsParser[F]
  ): ReaderT[F, WordleConfig, WordsParser[F]] =
    ReaderT { config =>
      new WordsParser[F] {

        import config.{filename, wordLength}

        override def parseWords(): F[Either[WordsParserError, NonEmptySet[String]]] =
          delegate.parseWords().evalTap {
            case Left(FileParseError) => Console[F].errorln("Error parsing words file.")
            case Left(InvalidWordsError) =>
              Console[F].errorln("Error parsing words (possibly word length or special characters).")
            case Left(EmptyFileError) => Console[F].errorln("Empty words file error")
            case Right(allWords) =>
              Console[F].println(
                s"Successfully parsed ${filename}, read ${allWords.length} words of length ${wordLength.value}")
          }
      }.pure
    }

  def live[F[_] : Applicative : MonadCancelThrow : Parallel : Console](
      fileReader: FileReader[F]
  ): ReaderT[F, WordleConfig, WordsParser[F]] =
    for {
      config <- ReaderT((config: WordleConfig) => config.pure)
      delegate <- {
        val aaa = WordsParser
          .observed(delegate = WordsParser[F].apply(config, fileReader))
          .local[WordleConfig](identity)
        aaa
      } // TODO: surely this is pointless?
    } yield delegate
}
