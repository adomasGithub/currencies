package com.converter.service.model

object Model {

  type CurrencyCode = String
  type CurrencyRate = BigDecimal
  type CurrencyPair = (CurrencyCode, CurrencyCode)
}
