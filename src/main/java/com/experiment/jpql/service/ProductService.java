package com.experiment.jpql.service;

import com.experiment.jpql.entity.Product;
import com.experiment.jpql.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * ProductService – contains business logic for product operations.
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // ─── CRUD ────────────────────────────────────────────────────────────────

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        existing.setName(updatedProduct.getName());
        existing.setCategory(updatedProduct.getCategory());
        existing.setPrice(updatedProduct.getPrice());
        existing.setDescription(updatedProduct.getDescription());
        existing.setStock(updatedProduct.getStock());
        return productRepository.save(existing);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    // ─── TASK 3a: findByCategory ─────────────────────────────────────────────

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryIgnoreCase(category);
    }

    public List<Product> getProductsByCategorySorted(String category) {
        return productRepository.findByCategoryIgnoreCaseOrderByPriceAsc(category);
    }

    // ─── TASK 3b: findByPriceBetween ─────────────────────────────────────────

    public List<Product> getProductsByPriceRange(Double minPrice, Double maxPrice) {
        return productRepository.findByPriceBetweenOrderByPriceAsc(minPrice, maxPrice);
    }

    // ─── TASK 4: JPQL queries ─────────────────────────────────────────────────

    public List<Product> getAllProductsSortedByPrice(String direction) {
        if ("desc".equalsIgnoreCase(direction)) {
            return productRepository.findAllSortedByPriceDesc();
        }
        return productRepository.findAllSortedByPriceAsc();
    }

    public List<Product> getExpensiveProducts(Double price) {
        return productRepository.findExpensiveProducts(price);
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.searchByNameKeyword(keyword);
    }

    public List<Product> filterProducts(String category, Double minPrice, Double maxPrice) {
        return productRepository.filterProducts(category, minPrice, maxPrice);
    }

    public List<Product> getLowStockProducts(Integer threshold) {
        return productRepository.findLowStockProducts(threshold);
    }

    public List<Map<String, Object>> getCategoryStats() {
        List<Object[]> raw = productRepository.countByCategory();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : raw) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("category", row[0]);
            entry.put("count", row[1]);
            result.add(entry);
        }
        return result;
    }

    public List<Product> getMostExpensivePerCategory() {
        return productRepository.findMostExpensivePerCategory();
    }
}
