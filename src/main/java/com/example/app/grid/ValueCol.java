package com.example.app.grid;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ValueCol {
    private String id;
    private String field;
    private String aggFunc;
}
