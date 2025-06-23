package io.github.drednote.telegram.datasource.session;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface UpdateInboxRepository<T extends UpdateInbox> extends CrudRepository<T, Integer> {
}
