package com.example.demo.service;

import com.example.demo.model.AdvCampaignHistoryLog;
import com.example.demo.model.CalculateMetricsResponse;
import com.example.demo.model.Dimension;
import com.example.demo.model.Metric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CtrCalculator extends BaseMetricCalculator {

    @Autowired
    public CtrCalculator(final EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected CalculateMetricsResponse doCalculate(final List<AdvCampaignHistoryLog> found, final List<Dimension> groupBy) {
        if (CollectionUtils.isEmpty(groupBy)) {
            final String result = calculateForElements(found);
            return CalculateMetricsResponse.builder().metric(Metric.CTR).scalar(result).build();
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
        return CalculateMetricsResponse.builder().metric(Metric.CTR).grouped(grouped).build();

    }


    private Map<String, Object> calculateForGroup(final Map<String, List<AdvCampaignHistoryLog>> byCampaign) {
        return byCampaign.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> calculateForElements(entry.getValue())));
    }

    private String calculateForElements(final List<AdvCampaignHistoryLog> found) {
        final long totalClicks = found.stream().mapToLong(AdvCampaignHistoryLog::getClicks).sum();
        final double impressions = (double) found.stream().mapToLong(AdvCampaignHistoryLog::getImpressions).sum();
        return DecimalFormat.getInstance().format(totalClicks / impressions);
    }

    @Override
    public Metric getMetric() {
        return Metric.CTR;
    }

}
