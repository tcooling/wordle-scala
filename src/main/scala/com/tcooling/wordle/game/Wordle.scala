package com.tcooling.wordle.game

import cats.{Monad, Parallel}
import cats.data.NonEmptySet
import cats.effect.kernel.Sync
import cats.syntax.all.*
import cats.implicits.*
import cats.effect.std.Console
import cats.effect.{ExitCode, Resource}
import com.tcooling.wordle.game.WordleFSM.State
import com.tcooling.wordle.input.{GuessInputConnector, UserInputGuessConnector}
import com.tcooling.wordle.model.FSM.{Exit, Start}
import com.tcooling.wordle.model.WordsParserError.{EmptyFileError, FileParseError, InvalidWordsError}
import com.tcooling.wordle.model.{FSM, TargetWord, WordGuess, WordleConfig, WordsParserError}
import com.tcooling.wordle.parser.{FileReader, WordsParser}
import com.tcooling.wordle.util.RandomWord

import scala.annotation.tailrec

trait Wordle[F[_]] {
  def startGame: F[ExitCode]
}

// TODO: cancellable? e.g. user presses ctrl + c
object Wordle {
  def apply[F[_] : Monad : Parallel : Console](
      config: WordleConfig,
      wordsParser: WordsParser[F],
      wordleFSM: WordleFSM[F],
      randomWord: RandomWord[F]
  ): Wordle[F] = new Wordle[F] {

    override def startGame: F[ExitCode] =
      wordsParser.parseWords().flatMap {
        case Left(_)      => ExitCode.Error.pure
        case Right(words) => randomWord.chooseRandomWord(words).flatMap(gameLoop(words, _)).as(ExitCode.Success)
      }

    private def gameLoop(allWords: NonEmptySet[String], targetWord: TargetWord): F[State] = {
      val wordleFSMNextState = wordleFSM.nextState(allWords, targetWord)

      def loop(state: FSM, guesses: List[WordGuess]): F[State] = state match {
        case Exit => (Exit -> guesses).pure
        case _    => wordleFSMNextState(state, guesses).flatMap(loop.tupled)
      }

      loop(state = Start, guesses = List.empty[WordGuess])
    }
  }

  def live[F[_] : Sync : Console](config: WordleConfig): Wordle[F] = {
    val fileReader: FileReader[F]                   = FileReader.apply()
    val wordsParser: WordsParser[F]                 = WordsParser.live(config, fileReader)
    val guessInputConnector: GuessInputConnector[F] = UserInputGuessConnector.apply()
    val wordleFSM: WordleFSM[F]                     = WordleFSM.apply(config, guessInputConnector)
    val randomWord: RandomWord[F]                   = RandomWord.apply()

    Wordle[F].apply(wordleConfig, wordsParser, wordleFSM, randomWord)
  }
}
