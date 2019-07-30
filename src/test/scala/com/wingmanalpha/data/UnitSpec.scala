package com.afranzi.data

import org.scalatest.{FlatSpec, Inspectors, Matchers, OptionValues}

/**
  * FlatSpec - http://www.scalatest.org/user_guide/selecting_a_style
  */
abstract class UnitSpec extends FlatSpec with Matchers with OptionValues with Inspectors

