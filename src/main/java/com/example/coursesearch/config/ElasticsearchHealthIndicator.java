package com.example.coursesearch.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.cluster.HealthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticsearchHealthIndicator implements HealthIndicator {

    private final ElasticsearchClient elasticsearchClient;

    @Override
    public Health health() {
        try {
            HealthResponse healthResponse = elasticsearchClient.cluster().health();
            
            log.debug("Elasticsearch cluster health: {}", healthResponse.status());
            
            return Health.up()
                    .withDetail("cluster_name", healthResponse.clusterName())
                    .withDetail("status", healthResponse.status())
                    .withDetail("number_of_nodes", healthResponse.numberOfNodes())
                    .withDetail("active_primary_shards", healthResponse.activePrimaryShards())
                    .withDetail("active_shards", healthResponse.activeShards())
                    .build();
                    
        } catch (Exception e) {
            log.error("Elasticsearch health check failed", e);
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
