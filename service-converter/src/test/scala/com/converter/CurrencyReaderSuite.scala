package com.converter

import com.converter.service.CurrencyReader
import org.scalatest.FunSuite

class CurrencyReaderSuite extends FunSuite {

  test("read currency rates from resource file") {

    val currencyRates = CurrencyReader.read("currencies.csv")

    assert(currencyRates.size === 6)
  }
}
