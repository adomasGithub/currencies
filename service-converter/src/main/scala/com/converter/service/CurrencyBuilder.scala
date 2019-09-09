package com.converter.service

import com.converter.service.model.Model._

object CurrencyBuilder {

  def build(baseCurrencyRates: Map[CurrencyCode, CurrencyRate]): Map[CurrencyPair, CurrencyRate] = {
    val currencyCodes = baseCurrencyRates.keySet

    (for {
      from <- currencyCodes
      to <- currencyCodes
      if from != to
      fromRate = baseCurrencyRates(from)
      toRate = baseCurrencyRates(to)
    } yield {
      Map(
        ((from, to), toRate / fromRate),
        ((to, from), fromRate / toRate)
      )
    }).flatten.toMap
  }
}
