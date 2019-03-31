package uk.gov.hmcts.ccd.definition.store;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel.INFO;

import com.zaxxer.hikari.HikariDataSource;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import uk.gov.hmcts.ccd.definition.store.database.RoutingDataSource;

@Configuration
@Profile("!test")
@ConditionalOnProperty(name="replicas.datasource.enabled")
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties masterDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource masterDataSource() {
        return masterDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean
    @Primary
    @ConfigurationProperties("replicas.datasource")
    public DataSourceProperties replicasDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("replicas.datasource.hikari")
    public DataSource replicasDataSource() {
        return replicasDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean
    @Primary
    public DataSource dataSource(@Value("${replicas.datasource.debug.enabled}") boolean queryLoggingEnabled) {
        RoutingDataSource routingDataSource = new RoutingDataSource();

        DataSource masterDataSource = queryLoggingEnabled ? proxied(masterDataSource(), "master-data-source") : masterDataSource();
        DataSource replicaDataSource = queryLoggingEnabled? proxied(replicasDataSource(), "replicas-data-source") : replicasDataSource();

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(RoutingDataSource.Route.MASTER, masterDataSource);
        targetDataSources.put(RoutingDataSource.Route.REPLICA, replicaDataSource);

        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(replicaDataSource);

        return routingDataSource;
    }

    private DataSource proxied(DataSource dataSource, String name) {
        return ProxyDataSourceBuilder
            .create(dataSource)
            .name(name)
            .countQuery()
            .logQueryBySlf4j(INFO)
            .build();
    }

}
