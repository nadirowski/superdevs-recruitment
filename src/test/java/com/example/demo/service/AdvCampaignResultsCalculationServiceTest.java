package com.example.demo.service;

import com.example.demo.model.CalculateMetricsResponse;
import com.example.demo.model.Dimension;
import com.example.demo.model.FilterOption;
import com.example.demo.model.Metric;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

class AdvCampaignResultsCalculationServiceTest {

    private static final CalculateMetricsResponse CALCULATE_RESPONSE = CalculateMetricsResponse.builder().metric(Metric.CTR).scalar("1").build();
    private AdvCampaignResultsCalculationService service;

    @BeforeEach
    public void setUp() {
        final MetricCalculator fakeCalculator = Mockito.mock(MetricCalculator.class);
        Mockito.when(fakeCalculator.getMetric()).thenReturn(Metric.CTR);
        Mockito.when(fakeCalculator.calculate(Mockito.anyList(), Mockito.anyList()))
                .thenReturn(CALCULATE_RESPONSE);
        service = new AdvCampaignResultsCalculationService(List.of(fakeCalculator));
    }

    @Test
    void shouldDelegateToCalculator() {
        final List<Dimension> groupBy = List.of(Dimension.CAMPAIGN);
        final List<FilterOption> filters = new ArrayList<>();

        final ResponseEntity<CalculateMetricsResponse> result = service.calculateMetricValue(Metric.CTR, groupBy, filters);

        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(result.getBody()).isEqualTo(CALCULATE_RESPONSE);
    }

    @Test
    void shouldReturnUnprocessableEntityForUnsupportedMetric() {
        final ResponseEntity<CalculateMetricsResponse> result = service.calculateMetricValue(Metric.TOTAL_CLICKS, null, null);

        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

}