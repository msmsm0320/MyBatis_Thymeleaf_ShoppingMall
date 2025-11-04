package com.example.shoppingmall.domain.product.dto;

import lombok.Data;

@Data
public class ProductSearchCond {
    private Long categoryId; // 카테고리로 필터
    private String keyword; // 상품명/설명 검색
    private String sort; // 정렬: latest, priceAsc, priceDesc
}
