package com.group.libraryapp.service.book

import com.group.libraryapp.domain.book.Book
import com.group.libraryapp.domain.book.BookRepository
import com.group.libraryapp.domain.book.BookType
import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistory
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanStatus
import com.group.libraryapp.dto.book.request.BookLoanRequest
import com.group.libraryapp.dto.book.request.BookRequest
import com.group.libraryapp.dto.book.request.BookReturnRequest
import com.group.libraryapp.dto.book.response.BookStatResponse
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
        val request = BookRequest("마인드셋", BookType.COMPUTER)

        // When
        bookService.saveBook(request)

        // Then
        val books = bookRepository.findAll()
        assertThat(books).hasSize(1)
        assertThat(books[0].name).isEqualTo("마인드셋")
        assertThat(books[0].type).isEqualTo(BookType.COMPUTER)
    }

    @Test
    @DisplayName("특정 책을 대출한다")
    fun loanBookTest() {
        bookRepository.save(Book.fixture("마인드셋"))
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
            { assertThat(results[0].status).isEqualTo(UserLoanStatus.LOANED) },
        )
    }

    @Test
    @DisplayName("이미 대여된 책 대출할 경우 예외가 발생한다")
    fun loanBookExceptionTest() {
        bookRepository.save(Book.fixture("마인드셋"))
        val savedUser = userRepository.save(User("신형기", 33))
        userLoanHistoryRepository.save(UserLoanHistory.fixture(savedUser, "마인드셋"))

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
        userLoanHistoryRepository.save(UserLoanHistory.fixture(savedUser, "마인드셋"))
        val request = BookReturnRequest("신형기", "마인드셋")

        // When
        bookService.returnBook(request)

        // Then
        val results = userLoanHistoryRepository.findAll()

        assertThat(results).hasSize(1)
        assertThat(results[0].status).isEqualTo(UserLoanStatus.RETURNED)
    }

    @Test
    fun `책 대여 권수를 정상 확인한다`() {
        // Given
        val savedUser = userRepository.save(User("신형기", null))
        userLoanHistoryRepository.saveAll(listOf(
            UserLoanHistory.fixture(savedUser, "A"),
            UserLoanHistory.fixture(savedUser, "B", UserLoanStatus.RETURNED),
            UserLoanHistory.fixture(savedUser, "C", UserLoanStatus.RETURNED),
        ))

        // When
        val result = bookService.countLoanedBook()

        // Then
        assertThat(result).isEqualTo(1)
    }

    @Test
    fun `분야별 책 권수를 정상 확인한다`() {
        // Given
        bookRepository.saveAll(listOf(
            Book.fixture("A", BookType.COMPUTER),
            Book.fixture("B", BookType.COMPUTER),
            Book.fixture("C", BookType.SCIENCE),
        ))

        // When
        val results = bookService.getBookStatResponse()

        // Then
        assertThat(results).hasSize(2)
        assertCount(results, BookType.COMPUTER, 2)
        assertCount(results, BookType.SCIENCE, 1)
    }

    private fun assertCount(results: List<BookStatResponse>, type: BookType, count: Int) {
        assertThat(results.first { result -> result.type == type }.count).isEqualTo(count)
    }

}