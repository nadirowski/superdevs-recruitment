package com.example.demo.service;

import com.example.demo.model.AdvCampaignHistoryLog;
import com.example.demo.model.CalculateMetricsResponse;
import com.example.demo.model.Dimension;
import com.example.demo.model.Metric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TotalClicksCalculator extends BaseMetricCalculator {

    @Autowired
    public TotalClicksCalculator(final EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected CalculateMetricsResponse doCalculate(final List<AdvCampaignHistoryLog> found, final List<Dimension> groupBy) {
        if (CollectionUtils.isEmpty(groupBy)) {
            final long totalClicks = found.stream().mapToLong(AdvCampaignHistoryLog::getClicks).sum();
            return CalculateMetricsResponse.builder().metric(Metric.TOTAL_CLICKS).scalar(String.valueOf(totalClicks)).build();
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
        return CalculateMetricsResponse.builder().metric(Metric.TOTAL_CLICKS).grouped(grouped).build();
    }

    private Map<String, Object> calculateForGroup(final Map<String, List<AdvCampaignHistoryLog>> byCampaign) {
        return byCampaign.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> calculateSum(entry.getValue())));
    }

    private String calculateSum(final List<AdvCampaignHistoryLog> elements) {
        return String.valueOf(elements.stream().mapToLong(AdvCampaignHistoryLog::getClicks).sum());
    }

    @Override
    public Metric getMetric() {
        return Metric.TOTAL_CLICKS;
    }

}
