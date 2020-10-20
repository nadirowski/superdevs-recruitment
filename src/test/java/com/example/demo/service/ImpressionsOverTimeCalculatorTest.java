package com.example.demo.service;

import com.example.demo.model.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@SpringBootTest
class ImpressionsOverTimeCalculatorTest {
    @Autowired
    private ImpressionsOverTimeCalculator impressionsOverTimeCalculator;

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Test
    void shouldCalculateImpressions() {
        final FilterOption googleAdsFilter = new FilterOption();
        googleAdsFilter.setDimension(Dimension.DATASOURCE);
        googleAdsFilter.setOperator(FilterOperator.EQUALS);
        googleAdsFilter.setOperatorValues(List.of("Google Ads"));

        final FilterOption campaignFilter = new FilterOption();
        campaignFilter.setDimension(Dimension.CAMPAIGN);
        campaignFilter.setOperator(FilterOperator.EQUALS);
        campaignFilter.setOperatorValues(List.of("Adventmarkt Touristik"));

        final CalculateMetricsResponse calculated = impressionsOverTimeCalculator.calculate(null, List.of(googleAdsFilter, campaignFilter));

        Assertions.assertThat(calculated.getMetric()).isEqualTo(Metric.IMPRESSIONS_OVER_TIME);
        Assertions.assertThat(calculated.getTimeseries()).hasSize(43);
        Assertions.assertThat(calculated.getTimeseries().get(0).getScalar()).isEqualTo("22425");
        Assertions.assertThat(simpleDateFormat.format(calculated.getTimeseries().get(0).getDate())).isEqualTo("2019-11-12");
        Assertions.assertThat(calculated.getTimeseries().get(42).getScalar()).isEqualTo("7705");
        Assertions.assertThat(simpleDateFormat.format(calculated.getTimeseries().get(42).getDate())).isEqualTo("2019-12-24");
    }


    @Test
    void shouldCalculateImpressionsGrouped() {
        final FilterOption googleAdsFilter = new FilterOption();
        googleAdsFilter.setDimension(Dimension.DATASOURCE);
        googleAdsFilter.setOperator(FilterOperator.EQUALS);
        googleAdsFilter.setOperatorValues(List.of("Google Ads"));

        final CalculateMetricsResponse calculated = impressionsOverTimeCalculator.calculate(List.of(Dimension.CAMPAIGN), List.of(googleAdsFilter));

        Assertions.assertThat(calculated.getMetric()).isEqualTo(Metric.IMPRESSIONS_OVER_TIME);
        final Map<String, Object> groupedByCampaign = calculated.getGrouped().get(Dimension.CAMPAIGN);
        Assertions.assertThat(groupedByCampaign).isNotEmpty();
        final List<TimeseriesValue> timeseriesForCampaign = (List<TimeseriesValue>) groupedByCampaign.get("Adventmarkt Touristik");
        Assertions.assertThat(timeseriesForCampaign).hasSize(43);
        Assertions.assertThat(timeseriesForCampaign.get(0).getScalar()).isEqualTo("22425");
        Assertions.assertThat(simpleDateFormat.format(timeseriesForCampaign.get(0).getDate())).isEqualTo("2019-11-12");
        Assertions.assertThat(timeseriesForCampaign.get(42).getScalar()).isEqualTo("7705");
        Assertions.assertThat(simpleDateFormat.format(timeseriesForCampaign.get(42).getDate())).isEqualTo("2019-12-24");
    }

}