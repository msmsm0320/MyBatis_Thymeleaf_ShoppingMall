package com.example.shoppingmall.mapper;

import com.example.shoppingmall.domain.product.Product;
import com.example.shoppingmall.domain.product.dto.ProductSearchCond;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {
    Product findById(@Param("id") Long id);

    int insert(Product product);

    int update(Product product);

    void deleteById(@Param("id") Long id);

    List<Product> findBycondPaged(
            @Param("cond") ProductSearchCond cond,
            @Param("offset") int offset,
            @Param("size") int size
    );

    int countByCond(@Param("cond") ProductSearchCond cond);

}
