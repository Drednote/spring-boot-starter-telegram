package io.github.drednote.telegram.support.mongo;

import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataMongoTest
@Import(MongoConfig.class)
@ActiveProfiles("test-mongo")
public abstract class MongoTest {
}
