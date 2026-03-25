package com.experiment.jpql.controller;

import com.experiment.jpql.entity.Product;
import com.experiment.jpql.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ProductController – REST API for product search, filter, and sort.
 *
 * Endpoints:
 *  GET  /products                          – All products
 *  GET  /products/{id}                     – Single product
 *  POST /products                          – Create product
 *  PUT  /products/{id}                     – Update product
 *  DELETE /products/{id}                   – Delete product
 *  GET  /products/category/{category}      – TASK 5a: By category
 *  GET  /products/filter                   – TASK 5b: Filter by price range (+ optional category)
 *  GET  /products/sorted                   – TASK 5c: All sorted by price
 *  GET  /products/expensive/{price}        – TASK 5d: More expensive than price
 *  GET  /products/search?keyword=          – Search by name keyword
 *  GET  /products/low-stock?threshold=     – Low stock products
 *  GET  /products/stats/category           – Category counts
 *  GET  /products/top-per-category         – Most expensive per category
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ─── CRUD Endpoints ───────────────────────────────────────────────────────

    /**
     * GET /products
     * Returns all products.
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    /**
     * GET /products/{id}
     * Returns a single product by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Product not found with id: " + id)));
    }

    /**
     * POST /products
     * Creates a new product.
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        Product saved = productService.saveProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * PUT /products/{id}
     * Updates an existing product.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id,
                                            @Valid @RequestBody Product product) {
        try {
            Product updated = productService.updateProduct(id, product);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /products/{id}
     * Deletes a product by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(Map.of("message", "Product with id " + id + " deleted successfully."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ─── TASK 5a: /products/category/{category} ──────────────────────────────

    /**
     * GET /products/category/{category}
     * Returns all products in the specified category.
     * Uses derived query: findByCategoryIgnoreCase()
     *
     * Example: GET /products/category/Electronics
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "false") boolean sorted) {

        List<Product> products = sorted
                ? productService.getProductsByCategorySorted(category)
                : productService.getProductsByCategory(category);

        return ResponseEntity.ok(products);
    }

    // ─── TASK 5b: /products/filter ───────────────────────────────────────────

    /**
     * GET /products/filter?minPrice=&maxPrice=&category=
     * Filters products by price range and optional category.
     * Uses JPQL @Query filterProducts().
     *
     * Examples:
     *   GET /products/filter?minPrice=100&maxPrice=500
     *   GET /products/filter?minPrice=100&maxPrice=500&category=Electronics
     *   GET /products/filter?category=Clothing
     */
    @GetMapping("/filter")
    public ResponseEntity<List<Product>> filterProducts(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String category) {

        // If filtering by price range only (no JPQL needed)
        if (category == null && minPrice != null && maxPrice != null) {
            return ResponseEntity.ok(productService.getProductsByPriceRange(minPrice, maxPrice));
        }

        // Full JPQL filter (handles nulls internally)
        List<Product> filtered = productService.filterProducts(category, minPrice, maxPrice);
        return ResponseEntity.ok(filtered);
    }

    // ─── TASK 5c: /products/sorted ───────────────────────────────────────────

    /**
     * GET /products/sorted?direction=asc|desc
     * Returns all products sorted by price.
     * Uses JPQL @Query findAllSortedByPriceAsc() / findAllSortedByPriceDesc().
     *
     * Examples:
     *   GET /products/sorted
     *   GET /products/sorted?direction=desc
     */
    @GetMapping("/sorted")
    public ResponseEntity<List<Product>> getSortedProducts(
            @RequestParam(defaultValue = "asc") String direction) {
        return ResponseEntity.ok(productService.getAllProductsSortedByPrice(direction));
    }

    // ─── TASK 5d: /products/expensive/{price} ────────────────────────────────

    /**
     * GET /products/expensive/{price}
     * Returns all products more expensive than the given price.
     * Uses JPQL @Query findExpensiveProducts().
     *
     * Example: GET /products/expensive/500
     */
    @GetMapping("/expensive/{price}")
    public ResponseEntity<List<Product>> getExpensiveProducts(@PathVariable Double price) {
        return ResponseEntity.ok(productService.getExpensiveProducts(price));
    }

    // ─── Extra Endpoints ─────────────────────────────────────────────────────

    /**
     * GET /products/search?keyword=laptop
     * Searches products by name keyword (JPQL LIKE).
     */
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(
            @RequestParam String keyword) {
        return ResponseEntity.ok(productService.searchProducts(keyword));
    }

    /**
     * GET /products/low-stock?threshold=10
     * Returns products with stock below threshold.
     */
    @GetMapping("/low-stock")
    public ResponseEntity<List<Product>> getLowStockProducts(
            @RequestParam(defaultValue = "10") Integer threshold) {
        return ResponseEntity.ok(productService.getLowStockProducts(threshold));
    }

    /**
     * GET /products/stats/category
     * Returns count of products per category.
     */
    @GetMapping("/stats/category")
    public ResponseEntity<List<Map<String, Object>>> getCategoryStats() {
        return ResponseEntity.ok(productService.getCategoryStats());
    }

    /**
     * GET /products/top-per-category
     * Returns the most expensive product in each category.
     */
    @GetMapping("/top-per-category")
    public ResponseEntity<List<Product>> getMostExpensivePerCategory() {
        return ResponseEntity.ok(productService.getMostExpensivePerCategory());
    }
}
