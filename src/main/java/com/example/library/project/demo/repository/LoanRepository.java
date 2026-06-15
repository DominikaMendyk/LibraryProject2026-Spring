package com.example.library.project.demo.repository;
import com.example.library.project.demo.entity.Book;
import com.example.library.project.demo.entity.Loan;
import com.example.library.project.demo.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends CrudRepository<Loan,Integer> {
    Optional<Loan> findByBookAndUserAndReturnDateIsNull(Book book, User user);
    List<Loan> findByUserAndReturnDateIsNull(User user);
    List<Loan> findByUser_UserIdAndReturnDateIsNull(Integer userId);
    List<Loan> findByUser_UserIdAndReturnDateIsNotNull(Integer userId);
    List<Loan> findByUser(User user);

    List<Loan> findByBookAndReturnDateIsNull(Book book);
    List<Loan> findByBook_BookIdAndReturnDateIsNull(Integer bookId);
    List<Loan> findByBookAndReturnDateIsNotNull(Book book);
    Optional<Loan> findFirstByBookAndUserAndReturnDateIsNullOrderByLoanDateAsc(Book book, User user);
    boolean existsByBook_BookIdAndUser_UserIdAndReturnDateIsNull(
            Integer bookId,
            Integer userId
    );
    boolean existsByBook_BookIdAndUser_UserId(
            Integer bookId,
            Integer userId
    );

    void deleteByBook_BookId(Integer bookId);

    void deleteByUser_UserId(Integer userId);

    List<Loan> findLoansByBook_BookId(Integer bookId);


    List<Loan> findAllByBookAndUserAndReturnDateIsNull(Book book, User user);
}
