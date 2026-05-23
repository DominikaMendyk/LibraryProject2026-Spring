package com.example.library.project.demo.entity.DTO;

public class UpdateReviewDTO {
    private Integer rating;
    private String comment;

    public UpdateReviewDTO(Integer rating, String comment){
        this.rating = rating;
        this.comment = comment;
    }

    public Integer getRating(){
        return rating;
    }

    public String getComment(){
        return comment;
    }
}
