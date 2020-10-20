package com.example.demo.service;

import com.example.demo.model.*;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class BaseMetricCalculator implements MetricCalculator {

    private final EntityManager entityManager;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    protected BaseMetricCalculator(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public CalculateMetricsResponse calculate(final List<Dimension> groupBy, final List<FilterOption> filters) {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        final CriteriaQuery<AdvCampaignHistoryLog> query = cb.createQuery(AdvCampaignHistoryLog.class);
        final Root<AdvCampaignHistoryLog> root = query.from(AdvCampaignHistoryLog.class);
        CriteriaQuery<AdvCampaignHistoryLog> select = query.select(root);
        if (!CollectionUtils.isEmpty(filters)) {
            try {
                select = addFilters(select, filters, cb, root);
            } catch (final ParseException e) {
                throw new IllegalArgumentException("Invalid date format for filter value");
            }
        }
        final List<AdvCampaignHistoryLog> found = entityManager.createQuery(select).getResultList();
        return doCalculate(found, groupBy);
    }

    protected abstract CalculateMetricsResponse doCalculate(List<AdvCampaignHistoryLog> found, List<Dimension> groupBy);

    private CriteriaQuery<AdvCampaignHistoryLog> addFilters(final CriteriaQuery<AdvCampaignHistoryLog> select, final List<FilterOption> filters, final CriteriaBuilder cb, final Root<AdvCampaignHistoryLog> root) throws ParseException {
        return select.where(toPredicates(filters, cb, root));
    }

    private Predicate[] toPredicates(final List<FilterOption> filters, final CriteriaBuilder cb, final Root<AdvCampaignHistoryLog> root) throws ParseException {
        final List<Predicate> predicates = new ArrayList<>();
        for (final FilterOption filter : filters) {
            predicates.add(toPredicate(filter, cb, root));
        }
        return predicates.toArray(new Predicate[0]);
    }

    private Predicate toPredicate(final FilterOption filter, final CriteriaBuilder cb, final Root<AdvCampaignHistoryLog> root) throws ParseException {
        final FilterOperator operator = filter.getOperator();
        switch (operator) {
            case EQUALS:
                return cb.equal(getField(filter.getDimension(), root), filter.getOperatorValues().get(0));
            case GREATER_THAN:
                return getGtPredicate(filter, cb, root);
            case LESS_THAN:
                return getLtPredicate(filter, cb, root);

        }
        throw new IllegalStateException("Unsupported operator " + operator);
    }

    @SuppressWarnings("unchecked")
    private Predicate getGtPredicate(final FilterOption filter, final CriteriaBuilder cb, final Root<AdvCampaignHistoryLog> root) throws ParseException {
        if (filter.getDimension() == Dimension.TIME) {
            return cb.greaterThan((Expression<Date>) getField(filter.getDimension(), root), sdf.parse(filter.getOperatorValues().get(0)));
        }
        return cb.gt((Expression<Number>) getField(filter.getDimension(), root), Integer.parseInt(filter.getOperatorValues().get(0)));
    }

    @SuppressWarnings("unchecked")
    private Predicate getLtPredicate(final FilterOption filter, final CriteriaBuilder cb, final Root<AdvCampaignHistoryLog> root) throws ParseException {
        if (filter.getDimension() == Dimension.TIME) {
            return cb.lessThan((Expression<Date>) getField(filter.getDimension(), root), sdf.parse(filter.getOperatorValues().get(0)));
        }
        return cb.lt((Expression<Number>) getField(filter.getDimension(), root), Integer.parseInt(filter.getOperatorValues().get(0)));
    }

    private Expression<?> getField(final Dimension dimension, final Root<AdvCampaignHistoryLog> root) {
        switch (dimension) {
            case CAMPAIGN:
                return root.get("campaign");
            case DATASOURCE:
                return root.get("datasource");
            case TIME:
                return root.get("creationDate");
        }
        throw new IllegalStateException("Not able to filter using dimension " + dimension);
    }
}
