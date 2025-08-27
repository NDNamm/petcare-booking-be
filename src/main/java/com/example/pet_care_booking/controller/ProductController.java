package com.example.pet_care_booking.controller;


import com.example.pet_care_booking.dto.ApiResponse;
import com.example.pet_care_booking.dto.ProductDTO;
import com.example.pet_care_booking.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    @GetMapping("")
    public ApiResponse<Page<ProductDTO>> getAllProduct(@RequestParam(required = false) String name,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "6") int size) {

        ApiResponse<Page<ProductDTO>> apiResponse = new ApiResponse<>();
        apiResponse.setData(productService.getAllProducts(name, page, size));
        return apiResponse;
    }

    @GetMapping("/product-details/{slug}")
    public ResponseEntity<ProductDTO> findBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(productService.findBySlug(slug));
    }

    @PostMapping(value = "/add/{cateId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductDTO> addProduct(@RequestPart("productDTO") String productJson,
                                              @RequestPart("image") MultipartFile[] image,
                                              @PathVariable Long cateId) {
        ApiResponse<ProductDTO> apiResponse = new ApiResponse<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            ProductDTO request = mapper.readValue(productJson, ProductDTO.class);
            productService.addProduct(cateId, request, image);
            apiResponse.setMessage("Thêm product thành công");
            return apiResponse;
        } catch (Exception e) {
            apiResponse.setMessage("Error: " + e.getMessage());
            return apiResponse;
        }
    }

    @PutMapping("/update/{id}")
    public ApiResponse<ProductDTO> updateProduct(@PathVariable Long id,
                                                 @RequestPart("productDTO") String productJson,
                                                 @RequestPart(value = "image", required = false) MultipartFile[] image) {
        ApiResponse<ProductDTO> apiResponse = new ApiResponse<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            ProductDTO request = mapper.readValue(productJson, ProductDTO.class);
            productService.updateProduct(id, request, image);
            apiResponse.setMessage("Update product thành công");
            return apiResponse;
        } catch (Exception e) {
            apiResponse.setMessage("Error: " + e.getMessage());
            return apiResponse;
        }
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<ProductDTO> deleteProduct(@PathVariable Long id) {

        ApiResponse<ProductDTO> apiResponse = new ApiResponse<>();
        productService.deleteProduct(id);
        apiResponse.setMessage("Delete product thành công");
        return apiResponse;

    }

    @GetMapping("/select/{cateId}")
    public ApiResponse<Page<ProductDTO>> selectProductByCateId(@PathVariable Long cateId,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "6") int size) {

        ApiResponse<Page<ProductDTO>> apiResponse = new ApiResponse<>();
        apiResponse.setData(productService.searchProductByCateId(cateId, page, size));
        return apiResponse;
    }

    @GetMapping("{productId}")
    public ApiResponse<ProductDTO> getProduct(@PathVariable Long productId) {
        ApiResponse<ProductDTO> apiResponse = new ApiResponse<>();
        apiResponse.setData(productService.getProductById(productId));
        return apiResponse;
    }


}
