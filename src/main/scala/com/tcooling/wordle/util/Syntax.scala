package com.tcooling.wordle.util

import cats.syntax.all.*
import cats.Monad

object Syntax {

  extension [F[_] : Monad, T](valueF: F[T]) {
    def evalTap(f: T => F[Unit]): F[T] = valueF.flatMap { value =>
      f(value).as(value)
    }
  }

  extension [L, R](predicate: Boolean) {
    def toEither(left: L, right: R): Either[L, R] = if (predicate) Right(right) else Left(left)
  }

}
