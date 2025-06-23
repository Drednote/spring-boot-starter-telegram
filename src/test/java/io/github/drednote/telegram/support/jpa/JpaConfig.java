package io.github.drednote.telegram.support.jpa;

import io.github.drednote.telegram.datasource.session.jpa.JpaUpdateInbox;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@TestConfiguration
@EnableJpaRepositories(basePackageClasses = JpaConfig.class)
@EntityScan(basePackageClasses = JpaUpdateInbox.class)
public class JpaConfig {
}
