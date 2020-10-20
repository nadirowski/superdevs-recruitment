package com.example.demo.web;

import com.example.demo.model.*;
import com.example.demo.service.AdvCampaignResultsCalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

class MetricsControllerTest {
    private final AdvCampaignResultsCalculationService service = Mockito.mock(AdvCampaignResultsCalculationService.class);
    private MetricsController controller;

    @BeforeEach
    public void setUp() {
        controller = new MetricsController(service);
    }

    @Test
    void shouldDelegateToService() {
        final CalculateMetricsRequest request = new CalculateMetricsRequest();
        final List<FilterOption> filters = new ArrayList<>();
        filters.add(new FilterOption(Dimension.DATASOURCE, FilterOperator.EQUALS, List.of("Google Ads")));
        request.setFilters(filters);
        request.setMetric(Metric.CTR);
        request.setGroupBy(List.of(Dimension.CAMPAIGN));

        controller.calculateMetrics(request);

        Mockito.verify(service).calculateMetricValue(request.getMetric(), request.getGroupBy(), request.getFilters());
    }

}