package com.example.app.grid;

import com.example.app.grid.SortModel;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public final class AgGridSort {
    private AgGridSort() {}

    public static Sort toSpringSort(List<SortModel> sortModels) {
        if (sortModels == null || sortModels.isEmpty()) return Sort.unsorted();
        List<Sort.Order> orders = new ArrayList<>();
        for (SortModel sm : sortModels) {
            Sort.Direction dir = "desc".equalsIgnoreCase(sm.getSort()) ? Sort.Direction.DESC : Sort.Direction.ASC;
            orders.add(new Sort.Order(dir, sm.getColId()));
        }
        return Sort.by(orders);
    }
}
