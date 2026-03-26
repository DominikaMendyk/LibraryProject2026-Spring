package com.example.library.project.demo.repository;
import com.example.library.project.demo.entity.Review;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends CrudRepository<Review,Integer> {
    List<Review> findByBook_BookId(Integer bookId);
    List<Review> findByUser_UserId(Integer userId);
}
