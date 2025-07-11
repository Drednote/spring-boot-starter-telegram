package io.github.drednote.telegram.support.mongo;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@TestConfiguration
@EnableMongoRepositories(basePackageClasses = MongoConfig.class)
public class MongoConfig {
}
