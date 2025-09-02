
package com.example.app.config;

import com.example.app.product.Product;
import com.example.app.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SampleDataLoader {

    @Bean
    CommandLineRunner seed(ProductRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                for (int i = 1; i <= 40; i++) {
                    repo.save(Product.builder()
                            .name("Product " + i)
                            .description("Sample product " + i)
                            .avail(i % 3 != 0)
                            .createdBy("system")
                            .modifiedBy("system")
                            .build());
                }
            }
        };
    }
}
