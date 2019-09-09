package com.converter.service

object CurrencyReader {

  def read(name: String): Map[String, BigDecimal] = {
    (for {
      line <- scala.io.Source.fromResource(name).getLines()
      Array(currencyCode, currencyRate) = line.split(",")
    } yield {
      (currencyCode, BigDecimal(currencyRate))
    }).toMap
  }
}
