package com.example.demo.service;

import com.example.demo.model.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
class CtrCalculatorTest {
    @Autowired
    private CtrCalculator ctrCalculator;

    @Test
    void shouldCalculateCtr() {
        final FilterOption googleAdsFilter = new FilterOption();
        googleAdsFilter.setDimension(Dimension.DATASOURCE);
        googleAdsFilter.setOperator(FilterOperator.EQUALS);
        googleAdsFilter.setOperatorValues(List.of("Google Ads"));

        final FilterOption campaignFilter = new FilterOption();
        campaignFilter.setDimension(Dimension.CAMPAIGN);
        campaignFilter.setOperator(FilterOperator.EQUALS);
        campaignFilter.setOperatorValues(List.of("Adventmarkt Touristik"));

        final CalculateMetricsResponse calculated = ctrCalculator.calculate(null, List.of(googleAdsFilter, campaignFilter));

        Assertions.assertThat(calculated.getMetric()).isEqualTo(Metric.CTR);
        Assertions.assertThat(calculated.getScalar()).isEqualTo("0.003");
    }

    @Test
    void shouldCalculateCtrGrouped() {
        final FilterOption googleAdsFilter = new FilterOption();
        googleAdsFilter.setDimension(Dimension.DATASOURCE);
        googleAdsFilter.setOperator(FilterOperator.EQUALS);
        googleAdsFilter.setOperatorValues(List.of("Google Ads"));

        final CalculateMetricsResponse calculated = ctrCalculator.calculate(List.of(Dimension.CAMPAIGN), List.of(googleAdsFilter));

        Assertions.assertThat(calculated.getMetric()).isEqualTo(Metric.CTR);
        final Map<String, Object> groupedByCampaign = calculated.getGrouped().get(Dimension.CAMPAIGN);
        Assertions.assertThat(groupedByCampaign).isNotEmpty();
        Assertions.assertThat(groupedByCampaign.get("Adventmarkt Touristik")).isEqualTo("0.003");
        Assertions.assertThat(groupedByCampaign.get("RM|SP Gadgets")).isEqualTo("0.004");
    }

}