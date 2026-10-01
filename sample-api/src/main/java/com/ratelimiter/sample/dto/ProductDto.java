package com.ratelimiter.sample.dto;

import java.math.BigDecimal;

public class ProductDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stock;

    public ProductDto() {
    }

    public ProductDto(Long id, String name, BigDecimal price, Integer stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public static ProductDtoBuilder builder() {
        return new ProductDtoBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public static class ProductDtoBuilder {
        private Long id;
        private String name;
        private BigDecimal price;
        private Integer stock;

        public ProductDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ProductDtoBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ProductDtoBuilder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public ProductDtoBuilder stock(Integer stock) {
            this.stock = stock;
            return this;
        }

        public ProductDto build() {
            return new ProductDto(id, name, price, stock);
        }
    }
}
