# Experiment 8 – Spring Boot JPQL & Query Methods

## Overview
This project demonstrates product searching, filtering, and sorting using Spring Data JPA's derived query methods and JPQL `@Query` annotations.

---

## Project Structure
```
src/main/java/com/experiment/jpql/
├── JpqlApplication.java              # Main entry point
├── DataLoader.java                   # Seeds 20 sample products on startup
├── entity/
│   └── Product.java                  # JPA Entity (@Entity, @Table)
├── repository/
│   └── ProductRepository.java        # Derived query methods + JPQL @Query
├── service/
│   └── ProductService.java           # Business logic layer
└── controller/
    └── ProductController.java        # REST API endpoints
```

---

## How to Run
```bash
mvn spring-boot:run
```
**Server:** `http://localhost:8080`  
**H2 Console:** `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:productdb`
- Username: `sa` | Password: *(leave blank)*

---

## Query Methods Reference

### Derived Query Methods (Auto-generated SQL)
| Method | Generated Query |
|---|---|
| `findByCategoryIgnoreCase(category)` | `WHERE LOWER(category) = LOWER(?)` |
| `findByPriceBetween(min, max)` | `WHERE price BETWEEN ? AND ?` |
| `findByCategoryIgnoreCaseOrderByPriceAsc(cat)` | `WHERE LOWER(category) = ... ORDER BY price ASC` |

### JPQL @Query Methods
| Method | JPQL |
|---|---|
| `findAllSortedByPriceAsc()` | `SELECT p FROM Product p ORDER BY p.price ASC` |
| `findExpensiveProducts(price)` | `SELECT p FROM Product p WHERE p.price > :price` |
| `filterProducts(cat, min, max)` | Dynamic filter with optional params |
| `searchByNameKeyword(kw)` | `WHERE LOWER(p.name) LIKE LOWER(CONCAT('%',:kw,'%'))` |

---

## API Endpoints & Postman Tests

### ✅ Get All Products
**GET** `http://localhost:8080/products`

---

### ✅ Task 5a – Get by Category
**GET** `http://localhost:8080/products/category/Electronics`  
**GET** `http://localhost:8080/products/category/Clothing`  
**GET** `http://localhost:8080/products/category/electronics` *(case-insensitive)*  
**GET** `http://localhost:8080/products/category/Electronics?sorted=true` *(sorted by price)*

**Sample Response:**
```json
[
  { "id": 1, "name": "Laptop Pro 15", "category": "Electronics", "price": 1299.99 },
  { "id": 2, "name": "Wireless Mouse", "category": "Electronics", "price": 29.99 }
]
```

---

### ✅ Task 5b – Filter Products
**GET** `http://localhost:8080/products/filter?minPrice=50&maxPrice=200`  
**GET** `http://localhost:8080/products/filter?minPrice=100&maxPrice=500&category=Electronics`  
**GET** `http://localhost:8080/products/filter?category=Clothing`

**Sample Response:**
```json
[
  { "id": 2, "name": "Wireless Mouse", "category": "Electronics", "price": 29.99 },
  { "id": 3, "name": "USB-C Hub",      "category": "Electronics", "price": 49.99 }
]
```

---

### ✅ Task 5c – Sorted Products
**GET** `http://localhost:8080/products/sorted`           *(ascending, default)*  
**GET** `http://localhost:8080/products/sorted?direction=asc`  
**GET** `http://localhost:8080/products/sorted?direction=desc`

**Sample Response (asc):**
```json
[
  { "id": 15, "name": "Vitamin C 1000mg", "price": 14.99 },
  { "id": 9,  "name": "T-Shirt Casual",   "price": 19.99 },
  ...
  { "id": 1,  "name": "Laptop Pro 15",    "price": 1299.99 }
]
```

---

### ✅ Task 5d – Expensive Products
**GET** `http://localhost:8080/products/expensive/500`  
**GET** `http://localhost:8080/products/expensive/100`  
**GET** `http://localhost:8080/products/expensive/1000`

**Sample Response (price > 500):**
```json
[
  { "id": 4, "name": "4K Monitor",  "price": 599.99 },
  { "id": 1, "name": "Laptop Pro 15", "price": 1299.99 }
]
```

---

### ✅ Extra – Search by Keyword
**GET** `http://localhost:8080/products/search?keyword=laptop`  
**GET** `http://localhost:8080/products/search?keyword=pro`

---

### ✅ Extra – Low Stock Alert
**GET** `http://localhost:8080/products/low-stock`            *(threshold=10 default)*  
**GET** `http://localhost:8080/products/low-stock?threshold=30`

---

### ✅ Extra – Category Statistics
**GET** `http://localhost:8080/products/stats/category`

**Sample Response:**
```json
[
  { "category": "Clothing",     "count": 3 },
  { "category": "Electronics",  "count": 8 },
  { "category": "Footwear",     "count": 3 }
]
```

---

### ✅ CRUD
| Method | URL | Body |
|---|---|---|
| POST | `/products` | `{"name":"X","category":"Y","price":9.99,"stock":10}` |
| PUT  | `/products/{id}` | Same JSON |
| DELETE | `/products/{id}` | — |

---

## GitHub
```bash
git init
git add .
git commit -m "Experiment 8: Spring Boot JPQL and Query Methods"
git remote add origin <your-repo-url>
git push -u origin main
```
