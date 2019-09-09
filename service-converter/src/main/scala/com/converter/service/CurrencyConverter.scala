package com.converter.service

import com.converter.service.model.Model.{CurrencyCode, CurrencyPair, CurrencyRate}

import scala.math.BigDecimal.RoundingMode
import scala.util.{Failure, Success, Try}

class CurrencyConverter(
  currencies: Map[CurrencyPair, CurrencyRate]
) {

  def convert(
    amount: BigDecimal,
    fromCurrency: CurrencyCode,
    toCurrency: CurrencyCode
  ): Try[BigDecimal] = {

    val from = fromCurrency.toUpperCase
    val to = toCurrency.toUpperCase

    (amount, from, to) match {
      case (a, _, _) if a <= 0 =>
        Failure(new IllegalArgumentException("Amount should be > 0"))
      case (_, f, t) if f == t =>
        Success(amount)
      case (_, f, t) if !currencies.isDefinedAt((f, t)) =>
        Failure(new IllegalArgumentException(s"Currencies convertion is not allowed for $f -> $t"))
      case _ =>
        Success(calculate(amount, from, to))
    }
  }

  private def calculate(
    amount: BigDecimal,
    fromCurrency: String,
    toCurrency: String
  ): BigDecimal = {
    currencies((fromCurrency, toCurrency)) * amount
  }
}
