package com.ratelimiter.sample.controller;

import com.ratelimiter.sample.dto.ProductDto;
import com.ratelimiter.sample.dto.UserDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SampleApiController {

    @GetMapping("/users")
    public List<UserDto> getUsers() {
        return List.of(
                UserDto.builder().id(1L).name("Alice Smith").email("alice@example.com").role("ADMIN").build(),
                UserDto.builder().id(2L).name("Bob Jones").email("bob@example.com").role("USER").build(),
                UserDto.builder().id(3L).name("Charlie Brown").email("charlie@example.com").role("USER").build()
        );
    }

    @GetMapping("/products")
    public List<ProductDto> getProducts() {
        return List.of(
                ProductDto.builder().id(101L).name("Wireless Mouse").price(new BigDecimal("29.99")).stock(150).build(),
                ProductDto.builder().id(102L).name("Mechanical Keyboard").price(new BigDecimal("89.99")).stock(75).build(),
                ProductDto.builder().id(103L).name("4K Monitor").price(new BigDecimal("349.99")).stock(30).build()
        );
    }
}
