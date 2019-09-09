package com.converter.service

import scala.math.BigDecimal.RoundingMode
import scala.util.{Failure, Success}

object Service extends App {

  val RoundingScale = 18
  val currencyConverter = new CurrencyConverter(CurrencyBuilder.build(CurrencyReader.read("currencies.csv")))

  val scanner = new java.util.Scanner(System.in)
  while (true) {
    print("Enter conversion amount: ")
    val amount = scanner.nextBigDecimal()
    print("Enter from Currency code: ")
    val fromCurrencyCode = scala.io.StdIn.readLine()
    print("Enter to Currency code: ")
    val toCurrencyCode = scala.io.StdIn.readLine()

    currencyConverter.convert(amount, fromCurrencyCode, toCurrencyCode) match {
      case Success(convertedAmount) =>
        val roundedAmount = convertedAmount.setScale(RoundingScale, RoundingMode.HALF_EVEN)
        println(s"Converted from $fromCurrencyCode to $toCurrencyCode, $amount -> $roundedAmount")
      case Failure(throwable) =>
        println(s"Can't convert: ${throwable.getMessage}")
    }
  }
}
