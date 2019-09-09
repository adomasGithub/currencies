package com.converter

import com.converter.service.{CurrencyBuilder, CurrencyReader}
import org.scalatest.FunSuite

class CurrencyBuilderSuite extends FunSuite {

  test("build currency pairs of 3") {
    val currencyRates: Map[String, BigDecimal] = Map(
      "EUR" -> 1,
      "USD" -> 1,
      "GBP" -> 1
    )

    val currencyPairs = CurrencyBuilder.build(currencyRates)

    assert(currencyPairs.size == 6)

    assert(currencyPairs.get("EUR", "EUR").isEmpty)
    assert(currencyPairs.get("USD", "USD").isEmpty)
    assert(currencyPairs.get("GBP", "GBP").isEmpty)

    assert(currencyPairs("EUR", "USD") === 1)
    assert(currencyPairs("EUR", "GBP") === 1)
    assert(currencyPairs("EUR", "USD") === 1)
  }

  test("build currency pairs from resource file") {
    val currencyPairs = CurrencyBuilder.build(CurrencyReader.read("currencies.csv"))
    assert(currencyPairs.size === 30)

    val uniqueFromCurrencyCodes = currencyPairs.keySet.groupBy {
      case (from, _) =>
        from
    }
    assert(uniqueFromCurrencyCodes.size === 6)

    val uniqueToCurrencyCodes = currencyPairs.keySet.groupBy {
      case (_, to) =>
        to
    }
    assert(uniqueToCurrencyCodes.size === 6)
  }
}

