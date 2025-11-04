package com.example.shoppingmall.controller;

import com.example.shoppingmall.domain.product.Product;
import com.example.shoppingmall.domain.product.dto.ProductSearchCond;
import com.example.shoppingmall.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final ProductMapper productMapper;

    @GetMapping("/")
    public String home(
            ProductSearchCond cond,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ){

        System.out.println("cond.getKeyword() = " + cond.getKeyword());
        int offset = (page - 1) * size;

        List<Product> products = productMapper.findBycondPaged(cond, offset, size);

        int totalCount = productMapper.countByCond(cond);

        int totalPages = (int) Math.ceil((double) totalCount / size);

        model.addAttribute("products", products);
        model.addAttribute("cond", cond);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", totalPages);
        return "index";
    }
}
