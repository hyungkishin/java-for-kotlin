package com.group.libraryapp.service.book

import com.group.libraryapp.domain.book.Book
import com.group.libraryapp.domain.book.BookRepository
import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistory
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.dto.book.request.BookLoanRequest
import com.group.libraryapp.dto.book.request.BookRequest
import com.group.libraryapp.dto.book.request.BookReturnRequest
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BookServiceTest @Autowired constructor(
    private val bookService: BookService,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
    private val userLoanHistoryRepository: UserLoanHistoryRepository,
) {

    @AfterEach
    fun clean() {
        bookRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    @DisplayName("책 이름을 저장한다")
    fun saveBookTest() {
        // Given
        val request = BookRequest("마인드셋")

        // When
        bookService.saveBook(request)

        // Then
        val books = bookRepository.findAll()
        assertThat(books).hasSize(1)
        assertThat(books[0].name).isEqualTo("마인드셋")
    }

    @Test
    @DisplayName("특정 책을 대출한다")
    fun loanBookTest() {
        bookRepository.save(Book("마인드셋"))
        val savedUser = userRepository.save(User("신형기", 33))
        val request = BookLoanRequest("신형기", "마인드셋")

        // When
        bookService.loanBook(request)

        // Then
        val results = userLoanHistoryRepository.findAll()

        assertAll(
            { assertThat(results).hasSize(1) },
            { assertThat(results[0].bookName).isEqualTo("마인드셋") },
            { assertThat(results[0].user.id).isEqualTo(savedUser.id) },
            { assertThat(results[0].isReturn).isEqualTo(false) },
        )
    }

    @Test
    @DisplayName("이미 대여된 책 대출할 경우 예외가 발생한다")
    fun loanBookExceptionTest() {
        bookRepository.save(Book("마인드셋"))
        val savedUser = userRepository.save(User("신형기", 33))
        userLoanHistoryRepository.save(UserLoanHistory(savedUser, "마인드셋", false))

        val request = BookLoanRequest("신형기", "마인드셋")

        // When & Then
        assertThrows<IllegalArgumentException> {
            bookService.loanBook(request)
        }.message.contentEquals("진작 대출되어 있는 책입니다")
    }

    @Test
    @DisplayName("대여된 책을 반납한다")
    fun returnBookTest() {
        // Given
        val savedUser = userRepository.save(User("신형기", 33))
        userLoanHistoryRepository.save(UserLoanHistory(savedUser, "마인드셋", false))
        val request = BookReturnRequest("신형기", "마인드셋")

        // When
        bookService.returnBook(request)

        // Then
        val results = userLoanHistoryRepository.findAll()

        assertThat(results).hasSize(1)
        assertThat(results[0].isReturn).isTrue
    }

}