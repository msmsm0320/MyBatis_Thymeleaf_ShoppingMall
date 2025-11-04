package com.example.shoppingmall.domain.product;

import lombok.Data;

@Data
public class Product {
    private Long id;
    private Long categoryId;
    private String name;
    private Integer price;
    private Integer stock;
    private String description;
    private String mainImage;
}
