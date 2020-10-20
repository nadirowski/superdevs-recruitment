package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CalculateMetricsResponse {

    private Metric metric;
    private String scalar;
    private Map<Dimension, Map<String, Object>> grouped;
    private List<TimeseriesValue> timeseries;
}
