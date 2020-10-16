package com.example.demo.service;

import com.example.demo.model.Dimension;
import com.example.demo.model.FilterOption;
import com.example.demo.model.Metric;

import java.util.List;

public interface MetricCalculator {

    Metric getMetric();

    Object calculate(List<Dimension> groupBy, List<FilterOption> filters);
}
