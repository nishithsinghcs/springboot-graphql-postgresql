package com.example.app.grid;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FilterModel {
    private String filterType; // text|number|date|set|boolean
    private String type;       // equals|contains|...
    private String filter;     // single value as string
    private String dateFrom;   // for date
    private String dateTo;     // for date inRange
    private List<String> values; // for set
    private String operator;   // AND|OR
    private Condition condition1;
    private Condition condition2;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Condition {
        private String type;
        private String filter;
        private String dateFrom;
        private String dateTo;
    }
}
