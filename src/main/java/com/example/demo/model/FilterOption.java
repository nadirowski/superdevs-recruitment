package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterOption {

    private Dimension dimension;
    private FilterOperator operator;
    private List<String> operatorValues;
}
