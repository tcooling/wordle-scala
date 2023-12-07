package com.tcooling.wordle.model

opaque type TargetWord = String

object TargetWord {
  def apply(value: String): TargetWord                 = value
  extension (targetWord: TargetWord) def value: String = targetWord
}
