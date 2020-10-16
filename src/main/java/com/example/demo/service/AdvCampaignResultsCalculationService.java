package com.example.demo.service;

import com.example.demo.model.Dimension;
import com.example.demo.model.FilterOption;
import com.example.demo.model.Metric;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvCampaignResultsCalculationService {

    private Map<Metric, MetricCalculator> metricCalculators;

    public AdvCampaignResultsCalculationService(final List<MetricCalculator> metricCalculators) {
        this.metricCalculators = metricCalculators.stream().collect(Collectors.toMap(MetricCalculator::getMetric, calculator -> calculator));
    }


    public ResponseEntity<Object> calculateMetricValue(final Metric metric, final List<Dimension> groupBy, final List<FilterOption> filters) {
        final MetricCalculator metricCalculator = metricCalculators.get(metric);
        return metricCalculator == null ? ResponseEntity.unprocessableEntity().build() : ResponseEntity.ok(metricCalculator.calculate(groupBy, filters));
    }
}
