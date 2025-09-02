
package com.example.app.product;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ProductGraphQLController {

    private final ProductService service;

    @QueryMapping
    public Product product(@Argument int id) {
        return service.get(id);
    }

    @QueryMapping
    public ProductPage products(@Argument int page,
                                @Argument int size,
                                @Argument String sortBy,
                                @Argument String sortDir,
                                @Argument String nameLike,
                                @Argument Boolean avail) {
        Page<Product> p = service.page(page, size, sortBy, sortDir, nameLike, avail);
        return new ProductPage(p.getTotalElements(), p.getNumber(), p.getSize(), p.getContent());
    }

    @MutationMapping
    public Product createProduct(@Argument ProductInput input) {
        Product p = Product.builder()
                .name(input.getName())
                .description(input.getDescription())
                .avail(input.getAvail() == null ? Boolean.TRUE : input.getAvail())
                .createdBy(input.getCreatedBy() == null ? "system" : input.getCreatedBy())
                .modifiedBy(input.getModifiedBy() == null ? "system" : input.getModifiedBy())
                .build();
        return service.create(p);
    }

    @MutationMapping
    public Product updateProduct(@Argument int id, @Argument ProductUpdateInput input) {
        Product p = Product.builder()
                .name(input.getName())
                .description(input.getDescription())
                .avail(input.getAvail())
                .modifiedBy(input.getModifiedBy() == null ? "system" : input.getModifiedBy())
                .build();
        return service.update(id, p);
    }

    @MutationMapping
    public Boolean deleteProduct(@Argument int id) {
        return service.delete(id);
    }

    @Data
    public static class ProductInput {
        private String name;
        private String description;
        private Boolean avail;
        private String createdBy;
        private String modifiedBy;
    }

    @Data
    public static class ProductUpdateInput {
        private String name;
        private String description;
        private Boolean avail;
        private String modifiedBy;
    }

    @Data
    public static class ProductPage {
        private final long total;
        private final int page;
        private final int size;
        private final java.util.List<Product> nodes;
    }
}
