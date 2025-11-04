package com.example.shoppingmall.domain.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductForm {

    @NotBlank(message = "이름을 등록해주세요.")
    private String name;

    @NotNull(message = "가격을 등록해주세요")
    @Min(0)
    private Integer price;

    @NotNull(message = "재고를 등록해주세요")
    @Min(0)
    private Integer stock;

    private Long categoryId;
    private String description;
    private String mainImage;
}
