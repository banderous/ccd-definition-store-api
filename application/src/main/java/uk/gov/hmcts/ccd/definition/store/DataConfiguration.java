package uk.gov.hmcts.ccd.definition.store;

import com.zaxxer.hikari.HikariDataSource;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

import java.util.concurrent.TimeUnit;

import static net.ttddyy.dsproxy.listener.logging.CommonsLogLevel.INFO;

@Configuration
public class DataConfiguration {

    @Bean
    public DataSource customDataSource(DataSourceProperties properties) {

        final HikariDataSource dataSource = (HikariDataSource) properties
            .initializeDataSourceBuilder().type(HikariDataSource.class).build();
        if (properties.getName() != null) {
            dataSource.setPoolName(properties.getName());
        }
        return ProxyDataSourceBuilder.create(dataSource)
            .name("proxy")
            .logSlowQueryToSysOut(5, TimeUnit.MILLISECONDS)
            .multiline()
            .build();
    }
}
