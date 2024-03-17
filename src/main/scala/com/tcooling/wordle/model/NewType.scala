package com.tcooling.wordle.model

/**
 * See: https://contributors.scala-lang.org/t/improve-opaque-types/4786/12
 */
trait NewType[A] {
  opaque type Type = A
  def apply(a: A): Type            = a
  extension (a: Type) def value: A = a
}

sealed abstract class StringNewType extends NewType[String]
sealed abstract class IntNewType    extends NewType[Int]

object Filename        extends StringNewType
object WordLength      extends IntNewType
object NumberOfGuesses extends IntNewType
object UserInputGuess  extends StringNewType
object TargetWord      extends StringNewType
