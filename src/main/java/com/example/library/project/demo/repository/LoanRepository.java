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
    List<Loan> findByUserAndReturnDateIsNotNull(User user);

    List<Loan> findByBookAndReturnDateIsNull(Book book);
    List<Loan> findByBookAndReturnDateIsNotNull(Book book);
}
