package com.example.demo.service;

import com.example.demo.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ImpressionsOverTimeCalculator extends BaseMetricCalculator {

    @Autowired
    public ImpressionsOverTimeCalculator(final EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected CalculateMetricsResponse doCalculate(final List<AdvCampaignHistoryLog> found, final List<Dimension> groupBy) {
        if (CollectionUtils.isEmpty(groupBy)) {
            final List<TimeseriesValue> timeseries = calculateForElements(found);
            return CalculateMetricsResponse.builder().metric(Metric.IMPRESSIONS_OVER_TIME).timeseries(timeseries).build();
        }
        final Map<Dimension, Map<String, Object>> grouped = new HashMap<>();
        for (final Dimension grouping : groupBy) {
            switch (grouping) {
                case CAMPAIGN:
                    final Map<String, List<AdvCampaignHistoryLog>> byCampaign = found.stream().collect(Collectors.groupingBy(AdvCampaignHistoryLog::getCampaign));
                    grouped.put(grouping, calculateForGroup(byCampaign));
                    break;
                case DATASOURCE:
                    final Map<String, List<AdvCampaignHistoryLog>> byDatasource = found.stream().collect(Collectors.groupingBy(AdvCampaignHistoryLog::getDatasource));
                    grouped.put(grouping, calculateForGroup(byDatasource));
                case TIME:
                    final Map<String, List<AdvCampaignHistoryLog>> byDate = found.stream().collect(Collectors.groupingBy(AdvCampaignHistoryLog::getDaily));
                    grouped.put(grouping, calculateForGroup(byDate));
                    break;
            }
        }
        return CalculateMetricsResponse.builder().metric(Metric.IMPRESSIONS_OVER_TIME).grouped(grouped).build();
    }

    private Map<String, Object> calculateForGroup(final Map<String, List<AdvCampaignHistoryLog>> byCampaign) {
        return byCampaign.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> calculateForElements(entry.getValue())));
    }

    private List<TimeseriesValue> calculateForElements(final List<AdvCampaignHistoryLog> elements) {
        return elements.stream().map(elem -> {
            final TimeseriesValue value = new TimeseriesValue();
            value.setDate(elem.getCreationDate());
            value.setScalar(String.valueOf(elem.getImpressions()));
            return value;
        }).sorted(Comparator.comparing(TimeseriesValue::getDate)).collect(Collectors.toList());
    }

    @Override
    public Metric getMetric() {
        return Metric.IMPRESSIONS_OVER_TIME;
    }

}
