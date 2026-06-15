package com.example.library.project.demo.repository;
import com.example.library.project.demo.entity.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends CrudRepository<Book,Integer> {
    Optional<Book> findByIsbn(String isbn);
    Optional<Book> findByBookId(Integer bookId);
    void deleteByIsbn(String isbn);
    List<Book> findAll();
}
