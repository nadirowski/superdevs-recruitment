package com.example.demo.service;

import com.example.demo.model.CalculateMetricsResponse;
import com.example.demo.model.Dimension;
import com.example.demo.model.FilterOption;
import com.example.demo.model.Metric;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdvCampaignResultsCalculationService {

    private final Map<Metric, MetricCalculator> metricCalculators;

    public AdvCampaignResultsCalculationService(final List<MetricCalculator> metricCalculators) {
        this.metricCalculators = metricCalculators.stream().collect(Collectors.toMap(MetricCalculator::getMetric, calculator -> calculator));
    }


    public ResponseEntity<CalculateMetricsResponse> calculateMetricValue(final Metric metric, final List<Dimension> groupBy, final List<FilterOption> filters) {
        final MetricCalculator metricCalculator = metricCalculators.get(metric);
        if (metricCalculator == null) {
            log.warn("Received request to calculate metric {} but no metric calculator is implemented for it", metric);
        }
        return metricCalculator == null ? ResponseEntity.unprocessableEntity().build() : ResponseEntity.ok(metricCalculator.calculate(groupBy, filters));
    }
}
