package com.example.app.grid;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SortModel {
    private String colId;
    private String sort; // asc|desc
}
