package com.example.app.grid;

import lombok.*;
import java.util.List;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AgGridRequest {
    private Integer startRow;
    private Integer endRow;
    private List<SortModel> sortModel;
    private Map<String, FilterModel> filterModel;
    private List<GroupCol> rowGroupCols;
    private List<String> groupKeys;
    private Boolean pivotMode;
    private List<ValueCol> valueCols;
    private List<GroupCol> pivotCols;
}
