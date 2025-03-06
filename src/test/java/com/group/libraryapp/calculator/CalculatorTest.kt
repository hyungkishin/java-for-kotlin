package com.group.libraryapp.calculator

fun main() {
    val calculatorTest = CalculatorTest()
    calculatorTest.addTest()
    calculatorTest.minusTest()
    calculatorTest.multiplyTest()
}

class CalculatorTest {

    fun addTest() {
        // Given
        val calculator = Calculator(5)

        // When
        calculator.add(3)

        // Then
        if (calculator.number != 8) {
            throw IllegalArgumentException()
        }
    }

    fun minusTest() {
        // Given
        val calculator = Calculator(5)

        // When
        calculator.minus(3)

        // Then
        if (calculator.number != 2) {
            throw IllegalArgumentException()
        }
    }

    fun multiplyTest() {
        // Given
        val calculator = Calculator(5)

        // When
        calculator.multiply(3)

        // Then
        if (calculator.number != 15) {
            throw IllegalArgumentException()
        }
    }

}