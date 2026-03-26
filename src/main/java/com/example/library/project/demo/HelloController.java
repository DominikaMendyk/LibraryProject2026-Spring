package com.example.library.project.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    //Most Front Part of the Server

    @GetMapping("/hello")
    public String sayHello(@RequestParam(defaultValue = "World") String name) {
        return "Hello " + name + "!";
    }

    @GetMapping("/add")
    public Integer addNumbers(@RequestParam Integer a, @RequestParam Integer b) {
        return a+b;
    }

    @GetMapping("/generate_random")
    public Integer randomFromRange(@RequestParam(defaultValue = "0") Integer min, @RequestParam(defaultValue ="1") Integer max) {
        if (min > max) {
            throw new IllegalArgumentException("min > max");
        }
        return (int) (Math.random() * (max - min + 1)) + min;
    }
}
