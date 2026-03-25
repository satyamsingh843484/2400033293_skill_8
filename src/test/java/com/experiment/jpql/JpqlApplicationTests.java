package com.experiment.jpql;

import com.experiment.jpql.entity.Product;
import com.experiment.jpql.repository.ProductRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JpqlApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    // ─── Repository Tests ────────────────────────────────────────────────────

    @Test
    @Order(1)
    void testFindByCategory() {
        List<Product> electronics = productRepository.findByCategoryIgnoreCase("Electronics");
        assertThat(electronics).isNotEmpty();
        electronics.forEach(p -> assertThat(p.getCategory()).isEqualToIgnoringCase("Electronics"));
    }

    @Test
    @Order(2)
    void testFindByPriceBetween() {
        List<Product> products = productRepository.findByPriceBetween(20.0, 100.0);
        assertThat(products).isNotEmpty();
        products.forEach(p -> {
            assertThat(p.getPrice()).isGreaterThanOrEqualTo(20.0);
            assertThat(p.getPrice()).isLessThanOrEqualTo(100.0);
        });
    }

    @Test
    @Order(3)
    void testJpqlSortedByPrice() {
        List<Product> sorted = productRepository.findAllSortedByPriceAsc();
        assertThat(sorted).isNotEmpty();
        for (int i = 1; i < sorted.size(); i++) {
            assertThat(sorted.get(i).getPrice()).isGreaterThanOrEqualTo(sorted.get(i - 1).getPrice());
        }
    }

    @Test
    @Order(4)
    void testJpqlExpensiveProducts() {
        List<Product> expensive = productRepository.findExpensiveProducts(200.0);
        assertThat(expensive).isNotEmpty();
        expensive.forEach(p -> assertThat(p.getPrice()).isGreaterThan(200.0));
    }

    @Test
    @Order(5)
    void testJpqlFilterProducts() {
        List<Product> filtered = productRepository.filterProducts("Electronics", 50.0, 500.0);
        assertThat(filtered).isNotEmpty();
        filtered.forEach(p -> {
            assertThat(p.getCategory()).isEqualToIgnoringCase("Electronics");
            assertThat(p.getPrice()).isBetween(50.0, 500.0);
        });
    }

    // ─── Controller/API Tests ─────────────────────────────────────────────────

    @Test
    @Order(6)
    void testGetAllProducts() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThan(0)));
    }

    @Test
    @Order(7)
    void testGetByCategory() throws Exception {
        mockMvc.perform(get("/products/category/Electronics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].category").value("Electronics"));
    }

    @Test
    @Order(8)
    void testFilterByPriceRange() throws Exception {
        mockMvc.perform(get("/products/filter?minPrice=20&maxPrice=100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(9)
    void testSortedProducts() throws Exception {
        mockMvc.perform(get("/products/sorted"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(10)
    void testExpensiveProducts() throws Exception {
        mockMvc.perform(get("/products/expensive/500"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(11)
    void testSearchByKeyword() throws Exception {
        mockMvc.perform(get("/products/search?keyword=laptop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(12)
    void testCategoryStats() throws Exception {
        mockMvc.perform(get("/products/stats/category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].category").exists())
                .andExpect(jsonPath("$[0].count").exists());
    }
}
