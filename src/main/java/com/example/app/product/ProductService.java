package com.example.app.product;

import com.example.app.grid.AgGridPage;
import com.example.app.grid.AgGridSort;
import com.example.app.grid.AgGridSpecifications;
import com.example.app.grid.AgGridRequest;
import com.example.app.grid.AgGridResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repo;

    public Product create(Product p) { return repo.save(p); }

    public Product update(Integer id, Product upd) {
        return repo.findById(id).map(ex -> {
            ex.setName(upd.getName());
            ex.setDescription(upd.getDescription());
            ex.setAvail(upd.getAvail());
            ex.setModifiedBy(upd.getModifiedBy() == null ? "system" : upd.getModifiedBy());
            return repo.save(ex);
        }).orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
    }

    public boolean delete(Integer id) {
        if (repo.existsById(id)) { repo.deleteById(id); return true; }
        return false;
    }

    public Product get(Integer id) { return repo.findById(id).orElse(null); }

    // GraphQL page
    public Page<Product> page(int page, int size, String sortBy, String sortDir, String nameLike, Boolean avail) {
        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC,
                        (sortBy == null || sortBy.isBlank()) ? "createdDate" : sortBy)
        );

        Specification<Product> spec = (root, q, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            if (nameLike != null && !nameLike.isBlank()) {
                ps.add(cb.like(cb.lower(root.get("name")), "%" + nameLike.toLowerCase() + "%"));
            }
            if (avail != null) {
                ps.add(cb.equal(root.get("avail"), avail));
            }
            return cb.and(ps.toArray(new Predicate[0]));
        };

        return repo.findAll(spec, pageable);
    }

    // ag-Grid style (callable from a GraphQL resolver if you want)
    public AgGridResponse<Product> grid(AgGridRequest req) {
        int size = AgGridPage.size(req.getStartRow(), req.getEndRow(), 100);
        int page = AgGridPage.page(req.getStartRow(), size);
        Sort sort = AgGridSort.toSpringSort(req.getSortModel());
        Specification<Product> spec = AgGridSpecifications.fromFilterModel(req.getFilterModel());

        Page<Product> pageResult = repo.findAll(spec, PageRequest.of(page, size, sort));
        return AgGridResponse.<Product>builder()
                .rows(pageResult.getContent())
                .lastRow(pageResult.getTotalElements())
                .build();
    }
}
