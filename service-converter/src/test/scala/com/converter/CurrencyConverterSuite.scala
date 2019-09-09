package com.converter

import com.converter.service.{CurrencyBuilder, CurrencyConverter, CurrencyReader}
import org.scalatest.FunSuite

import scala.math.BigDecimal.RoundingMode
import scala.util.{Failure, Random, Success}

class CurrencyConverterSuite extends FunSuite {

  private val baseCurrencies = CurrencyReader.read("currencies.csv")
  private val currencyConverter = new CurrencyConverter(CurrencyBuilder.build(baseCurrencies))
  private val RoundingScale = 18

  test("should not convert when currency codes are wrong") {
    val Failure(exception1) = currencyConverter.convert(123, "xxxxxx", "GBP")
    assert(exception1.isInstanceOf[IllegalArgumentException])

    val Failure(exception2) = currencyConverter.convert(1, "USD", "")
    assert(exception2.isInstanceOf[IllegalArgumentException])

    val Failure(exception3) = currencyConverter.convert(9.0009, "213", "-----")
    assert(exception3.isInstanceOf[IllegalArgumentException])
  }

  test("should not convert when amount less than zero") {

    val Failure(exception1) = currencyConverter.convert(-1, "EUR", "GBP")
    assert(exception1.isInstanceOf[IllegalArgumentException])

    val Failure(exception2) = currencyConverter.convert(-325454, "BTC", "USD")
    assert(exception2.isInstanceOf[IllegalArgumentException])

    val Failure(exception3) = currencyConverter.convert(-0.000000000000007, "ETH", "BTC")
    assert(exception3.isInstanceOf[IllegalArgumentException])
  }

  test("should not convert when amount equals zero") {
    val Failure(exception1) = currencyConverter.convert(0, "BTC", "BTC")
    assert(exception1.isInstanceOf[IllegalArgumentException])

    val Failure(exception2) = currencyConverter.convert(0.0, "EUR", "USD")
    assert(exception2.isInstanceOf[IllegalArgumentException])
  }

  test("should return given amount when currencies are the same") {
    val amount = 100.12345
    val currencyCode = "EUR"

    val Success(convertedAmount) = currencyConverter.convert(amount, currencyCode, currencyCode)
    assert(amount === convertedAmount)
  }

  test("convert all currencies from EUR") {

    val Success(eurToUsd) = currencyConverter.convert(1, "EUR", "USD")
    assert(eurToUsd === baseCurrencies("USD"))

    val Success(eurToGbp) = currencyConverter.convert(1, "EUR", "GBP")
    assert(eurToGbp === baseCurrencies("GBP"))

    val Success(eurToBtc) = currencyConverter.convert(1, "EUR", "BTC")
    assert(eurToBtc === baseCurrencies("BTC"))

  }

  test("convert EUR to USD") {
    val amount = 400.5

    val Success(convertedUsdAmount) = currencyConverter.convert(amount, "EUR", "USD")
    val Success(convertedEurAmount) = currencyConverter.convert(convertedUsdAmount, "USD", "EUR")

    assert(amount === convertedEurAmount.setScale(18, RoundingMode.HALF_UP))
  }

  test("convert EUR to GBP") {

    val amount = 10.1234567788990087867

    val Success(convertedGbpAmount) = currencyConverter.convert(amount, "EUR", "GBP")
    val Success(convertedEurAmount) = currencyConverter.convert(convertedGbpAmount, "GBP", "EUR")

    assert(amount === convertedEurAmount)
  }

  test("convert through all currencies and back to amount of the first currency") {

    val beginAmount: BigDecimal = 1234.56789

    val currencyCodes = baseCurrencies.keySet.zip(baseCurrencies.keySet.drop(1))

    val finalAmount = currencyCodes.foldRight(beginAmount) {
      case ((from, to), amount) =>
        val Success(convertedAmount) = currencyConverter.convert(amount, from, to)
        convertedAmount
    }

    val Success(convertedToBeginAmount) = currencyConverter.convert(finalAmount, baseCurrencies.keySet.last, baseCurrencies.keySet.head)
    assert(beginAmount === convertedToBeginAmount.setScale(18, RoundingMode.HALF_EVEN))
  }

  test("iterate 100000 convertions for all currency codes") {

    baseCurrencies.keySet.foreach { currencyCode =>

      val toCurrencyCodes = baseCurrencies.keySet.filterNot(_ == currencyCode)

      for (_ <- 1 to 100000) {
        val randomInt = Random.nextInt()

        val amount: BigDecimal = (if (randomInt < 0) randomInt * -1 else randomInt) + Random.nextDouble()
        toCurrencyCodes.foreach { toCurrencyCode =>
          val Success(convertedAmount) = currencyConverter.convert(amount, currencyCode, toCurrencyCode)
          val Success(convertedBackAmount) = currencyConverter.convert(convertedAmount, toCurrencyCode, currencyCode)
          assert(amount === convertedBackAmount.setScale(18, RoundingMode.HALF_EVEN))
        }
      }
    }
  }
}
