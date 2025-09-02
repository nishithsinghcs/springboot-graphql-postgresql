package com.example.app.grid;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AgGridResponse<T> {
    private java.util.List<T> rows;
    private long lastRow;
}
