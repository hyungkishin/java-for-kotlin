package com.group.libraryapp.calculator

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


class JunitCalculatorTest {

    @Test
    fun addTest() {
        // Given
        val calculator = Calculator(5)

        // When
        calculator.add(3)

        // Then
        assertThat(calculator.number).isEqualTo(8)
    }

    @Test
    fun minusTest() {
        // Given
        val calculator = Calculator(5)

        // When
        calculator.minus(3)

        // Then
        assertThat(calculator.number).isEqualTo(2)
    }

    @Test
    fun multiplyTest() {
        // Given
        val calculator = Calculator(5)

        // When
        calculator.multiply(3)

        // Then
        assertThat(calculator.number).isEqualTo(15)
    }

    @Test
    fun divideTest() {
        // Given
        val calculator = Calculator(5)

        // When
        calculator.divide(3)

        // Then
        assertThat(calculator.number).isEqualTo(1)
    }

    @Test
    fun divideExceptionTest() {
        // given
        val calculator = Calculator(5)

        // when
        assertThrows<IllegalArgumentException> {
            calculator.divide(0)
        }.apply {
            assertThat(message).isEqualTo("0으로 나눌 수 없습니다")
        }

    }

}