package com.experiment.jpql.repository;

import com.experiment.jpql.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ProductRepository – Spring Data JPA repository.
 *
 * Demonstrates:
 *  1. Derived Query Methods (method name → SQL)
 *  2. JPQL @Query for custom queries
 *  3. Named parameters with @Param
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ─── TASK 3a: Derived Query Method – findByCategory ─────────────────────

    /**
     * Finds all products matching the given category (case-insensitive).
     * Spring Data JPA auto-generates: SELECT p FROM Product p WHERE LOWER(p.category) = LOWER(:category)
     */
    List<Product> findByCategoryIgnoreCase(String category);

    /**
     * Finds products by exact category, ordered by price ascending.
     */
    List<Product> findByCategoryIgnoreCaseOrderByPriceAsc(String category);

    // ─── TASK 3b: Derived Query Method – findByPriceBetween ─────────────────

    /**
     * Finds products with price between minPrice and maxPrice (inclusive).
     * Auto-generated: SELECT p FROM Product p WHERE p.price BETWEEN :min AND :max
     */
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

    /**
     * Price between, ordered by price ascending.
     */
    List<Product> findByPriceBetweenOrderByPriceAsc(Double minPrice, Double maxPrice);

    // ─── TASK 4: JPQL @Query annotations ─────────────────────────────────────

    /**
     * JPQL: Fetch all products sorted by price (ascending).
     * Used for /products/sorted endpoint.
     */
    @Query("SELECT p FROM Product p ORDER BY p.price ASC")
    List<Product> findAllSortedByPriceAsc();

    /**
     * JPQL: Fetch all products sorted by price (descending).
     */
    @Query("SELECT p FROM Product p ORDER BY p.price DESC")
    List<Product> findAllSortedByPriceDesc();

    /**
     * JPQL: Fetch products more expensive than a given price.
     * Used for /products/expensive/{price} endpoint.
     */
    @Query("SELECT p FROM Product p WHERE p.price > :price ORDER BY p.price ASC")
    List<Product> findExpensiveProducts(@Param("price") Double price);

    /**
     * JPQL: Search products by name keyword (case-insensitive LIKE).
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchByNameKeyword(@Param("keyword") String keyword);

    /**
     * JPQL: Filter products by category AND price range combined.
     * Used for the /products/filter endpoint with all three params.
     */
    @Query("SELECT p FROM Product p WHERE " +
           "(:category IS NULL OR LOWER(p.category) = LOWER(:category)) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "ORDER BY p.price ASC")
    List<Product> filterProducts(
            @Param("category") String category,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice
    );

    /**
     * JPQL: Get count of products per category.
     */
    @Query("SELECT p.category, COUNT(p) FROM Product p GROUP BY p.category")
    List<Object[]> countByCategory();

    /**
     * JPQL: Find products with low stock (below threshold).
     */
    @Query("SELECT p FROM Product p WHERE p.stock < :threshold ORDER BY p.stock ASC")
    List<Product> findLowStockProducts(@Param("threshold") Integer threshold);

    /**
     * JPQL: Find the most expensive product in each category.
     */
    @Query("SELECT p FROM Product p WHERE p.price = " +
           "(SELECT MAX(p2.price) FROM Product p2 WHERE p2.category = p.category)")
    List<Product> findMostExpensivePerCategory();
}
