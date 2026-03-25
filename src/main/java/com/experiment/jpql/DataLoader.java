package com.experiment.jpql;

import com.experiment.jpql.entity.Product;
import com.experiment.jpql.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * DataLoader – seeds sample product data into the H2 in-memory database on startup.
 */
@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(ProductRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new Product("Laptop Pro 15",       "Electronics",  1299.99, "High-performance laptop with 16GB RAM",        25));
                repository.save(new Product("Wireless Mouse",      "Electronics",    29.99, "Ergonomic wireless mouse",                      120));
                repository.save(new Product("USB-C Hub",           "Electronics",    49.99, "7-in-1 USB-C hub with HDMI and USB 3.0",         75));
                repository.save(new Product("4K Monitor",          "Electronics",   599.99, "27-inch 4K IPS display",                         18));
                repository.save(new Product("Mechanical Keyboard", "Electronics",   149.99, "Tenkeyless mechanical keyboard",                 40));
                repository.save(new Product("Running Shoes",       "Footwear",       89.99, "Lightweight running shoes for all terrain",      60));
                repository.save(new Product("Hiking Boots",        "Footwear",      129.99, "Waterproof leather hiking boots",                35));
                repository.save(new Product("Sandals",             "Footwear",       34.99, "Comfortable everyday sandals",                   90));
                repository.save(new Product("T-Shirt Casual",      "Clothing",       19.99, "100% cotton casual t-shirt",                    200));
                repository.save(new Product("Denim Jeans",         "Clothing",       59.99, "Slim fit stretch denim jeans",                  110));
                repository.save(new Product("Winter Jacket",       "Clothing",      189.99, "Insulated waterproof winter jacket",             45));
                repository.save(new Product("Yoga Mat",            "Sports",         39.99, "Non-slip eco-friendly yoga mat",                 80));
                repository.save(new Product("Dumbbells Set",       "Sports",         99.99, "Adjustable dumbbell set 5-25kg",                 30));
                repository.save(new Product("Protein Powder",      "Nutrition",      49.99, "Whey protein chocolate flavour 1kg",             55));
                repository.save(new Product("Vitamin C 1000mg",    "Nutrition",      14.99, "High-strength vitamin C supplements",           150));
                repository.save(new Product("Desk Lamp",           "Home",           34.99, "LED desk lamp with adjustable brightness",       65));
                repository.save(new Product("Coffee Maker",        "Home",           79.99, "12-cup programmable coffee maker",               40));
                repository.save(new Product("Air Purifier",        "Home",          249.99, "HEPA air purifier for large rooms",              20));
                repository.save(new Product("Bluetooth Speaker",   "Electronics",    89.99, "Portable waterproof Bluetooth speaker",          70));
                repository.save(new Product("Smart Watch",         "Electronics",   299.99, "Fitness tracking smart watch with GPS",          50));

                System.out.println("✅ Sample data loaded: " + repository.count() + " products.");
            }
        };
    }
}
