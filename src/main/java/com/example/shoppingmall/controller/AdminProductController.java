package com.example.shoppingmall.controller;

import com.example.shoppingmall.domain.product.Product;
import com.example.shoppingmall.domain.product.dto.ProductForm;
import com.example.shoppingmall.mapper.ProductMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.Files.createDirectories;

@Controller
@RequiredArgsConstructor
public class AdminProductController {
    private final ProductMapper productMapper;

    // 1. 등록 폼 열기
    @GetMapping("/admin/products/new")
    public String showCreateForm(Model model){
        model.addAttribute("form", new ProductForm());
        return "admin/product-form";
    }

    // 2. 등록 처리
    @PostMapping("/admin/products/new")
    public String create(
            @Valid @ModelAttribute("form") ProductForm form,
            BindingResult bindingResult,
            MultipartFile image
    ) throws IOException{
        if(bindingResult.hasErrors()){
            return "admin/product-form";
        }

        String storeFileName = null;

        if(image != null && !image.isEmpty()){
            // 원본 파일명
            String originalFileName = image.getOriginalFilename();
            // 저장용 파일명 (중복 방지)
            String ext = "";
            if(originalFileName != null && originalFileName.contains(".")) {
                ext = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            storeFileName = System.currentTimeMillis() + ext;

            // 실제 저장 경로
            Path savePath = Paths.get("uploads").toAbsolutePath();
            createDirectories(savePath);
            image.transferTo(savePath.resolve(storeFileName));
        }

        Product product = new Product();

        product.setName(form.getName());
        product.setCategoryId(form.getCategoryId());
        product.setDescription(form.getDescription());
        product.setPrice(form.getPrice());
        product.setStock(form.getStock());
        product.setDescription(form.getDescription());
        product.setMainImage(storeFileName);

        productMapper.insert(product);

        return "redirect:/";
    }

    // 3. 상품 수정 처리
    @GetMapping("/admin/products/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model){
        Product product = productMapper.findById(id);

        ProductForm productForm = new ProductForm();
        productForm.setName(product.getName());
        productForm.setPrice(product.getPrice());
        productForm.setStock(product.getStock());
        productForm.setCategoryId(product.getCategoryId());
        productForm.setDescription(product.getDescription());
        productForm.setMainImage(product.getMainImage());


        model.addAttribute("form", productForm);
        model.addAttribute("productId", id);
        return "admin/product-edit-form";
    }

    @PostMapping("/admin/products/{id}/edit")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") ProductForm form,
            BindingResult bindingResult,
            @RequestParam(value = "image", required = false) MultipartFile image
            ) throws IOException {

        if (bindingResult.hasErrors()) {
            return "admin/product-edit-form";
        }

        Product existing = productMapper.findById(id);

        // 2) DB 저장
        Product product = new Product();
        product.setId(id);
        product.setName(form.getName());
        product.setPrice(form.getPrice());
        product.setStock(form.getStock());
        product.setCategoryId(form.getCategoryId());
        product.setDescription(form.getDescription());

        if(image != null && !image.isEmpty()) {
            if(existing.getMainImage() != null) {
                Path uploadPath = Paths.get("uploads").toAbsolutePath();
                Files.deleteIfExists(uploadPath.resolve(existing.getMainImage()));
            }

            String originalFilename = image.getOriginalFilename();
            String ext = "";
            if(originalFilename != null && originalFilename.contains(".")) {
                ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String storedFileName = System.currentTimeMillis() + ext;

            Path uploadPath = Paths.get("uploads").toAbsolutePath();
            Files.createDirectories(uploadPath);
            image.transferTo(uploadPath.resolve(storedFileName));

            product.setMainImage(storedFileName);
        } else {
            product.setMainImage(existing.getMainImage());
        }

      productMapper.update(product);

      return "redirect:/";
    }

    //4. 삭제 처리
    @PostMapping("/admin/products/{id}/delete")
    public String delete(@PathVariable Long id) throws IOException{
        Product product = productMapper.findById(id);
        productMapper.deleteById(id);

        if(product != null && product.getMainImage() != null){
            Path uploadPath = Paths.get("uploads").toAbsolutePath();
            Path target = uploadPath.resolve(product.getMainImage());

            System.out.println("삭제 시도 : " + target);

            Files.deleteIfExists(target);
        }
        return "redirect:/";
    }
}
