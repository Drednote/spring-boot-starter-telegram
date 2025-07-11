package io.github.drednote.telegram.datasource.session.mongo;

import io.github.drednote.telegram.datasource.session.UpdateInboxRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface MongoUpdateInboxRepository extends UpdateInboxRepository<MongoUpdateInbox>,
    MongoRepository<MongoUpdateInbox, Integer> {

}
