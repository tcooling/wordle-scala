package com.tcooling.wordle.parser

import cats.{Applicative, Monad, Parallel}
import cats.implicits.*
import cats.effect.*
import cats.effect.std.Console
import cats.syntax.all.*
import cats.effect.Resource
import cats.data.{EitherT, NonEmptyList, NonEmptySet}
import com.tcooling.wordle.model.WordleConfig
import com.tcooling.wordle.model.{WordLength, WordsParserError}
import com.tcooling.wordle.model.WordsParserError.{EmptyFileError, FileParseError, InvalidWordsError}

trait WordsParser[F[_]] {
  def parseWords(): F[Either[WordsParserError, NonEmptySet[String]]]
}

object WordsParser {
  def apply[F[_] : Applicative : Monad : Parallel : MonadCancelThrow](config: WordleConfig,
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

  // TODO: add file reader to live method and use reader t
  def observed[F[_] : Monad : Console](config: WordleConfig, delegate: WordsParser[F]): WordsParser[F] =
    new WordsParser[F] {
      override def parseWords(): F[Either[WordsParserError, NonEmptySet[String]]] =
        delegate.parseWords().evalTap {
          case Left(FileParseError) => Console[F].errorln("Error parsing words file.")
          case Left(InvalidWordsError) =>
            Console[F].errorln("Error parsing words (possibly word length or special characters).")
          case Left(EmptyFileError) => Console[F].errorln("Empty words file error")
          case Right(allWords) =>
            Console[F].println(
              s"Successfully parsed ${config.filename}, read ${allWords.length} words of length ${config.wordLength.value}")
        }

      // TODO: trying to recreate fs2 evalTap, must exist already?
      // TODO: move to util
      extension [F[_] : Monad, T](valueF: F[T]) {
        def evalTap(f: T => F[Unit]): F[T] = valueF.flatMap { value =>
          f(value).as(value)
        }
      }
    }

  def live[F[_] : Monad : Parallel : MonadCancelThrow : Console](
      config: WordleConfig,
      fileReader: FileReader[F]
  ): WordsParser[F] = {
    val delegate = WordsParser.apply(config, fileReader)
    observed[F](config, delegate)
  }
}
