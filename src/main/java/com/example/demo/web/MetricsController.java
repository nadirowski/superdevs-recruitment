package com.example.demo.web;

import com.example.demo.model.CalculateMetricsRequest;
import com.example.demo.model.CalculateMetricsResponse;
import com.example.demo.service.AdvCampaignResultsCalculationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/metrics")
@RequiredArgsConstructor
@Api("Metrics calculation api")
public class MetricsController {


    private final AdvCampaignResultsCalculationService service;

    /**
     * While this is kind of a search request GET method is not suitable to be used here due to complex query object
     * that might not fit in the HTTP GET request (might be too long).
     * Using POST allows us to create complex query object, and potentially cache the response to be used in subsequent queries
     *
     * @param request - complex query object containing data that is requested
     * @return list of requested metrics
     */
    @PostMapping
    @ApiOperation("Calculate metrics for given data")
    public ResponseEntity<CalculateMetricsResponse> calculateMetrics(@RequestBody final CalculateMetricsRequest request) {
        return service.calculateMetricValue(request.getMetric(), request.getGroupBy(), request.getFilters());

    }
}
