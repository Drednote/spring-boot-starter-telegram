package io.github.drednote.telegram.datasource.session.mongo;

import static io.github.drednote.telegram.datasource.session.UpdateInboxStatus.IN_PROGRESS;
import static io.github.drednote.telegram.datasource.session.UpdateInboxStatus.NEW;

import io.github.drednote.telegram.datasource.session.AbstractUpdateInboxRepositoryAdapter;
import io.github.drednote.telegram.session.SessionProperties;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators.Filter;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.transaction.annotation.Transactional;

public class MongoUpdateInboxRepositoryAdapter extends AbstractUpdateInboxRepositoryAdapter<MongoUpdateInbox> {

    private static final Logger log = LoggerFactory.getLogger(MongoUpdateInboxRepositoryAdapter.class);

    private static final String STATUS = "status";
    private static final String UPDATED_AT = "updatedAt";
    private static final String CREATED_AT = "createdAt";
    private static final String ENTITY_ID = "entityId";

    private final FindAndModifyOptions findAndModifyOptions;
    private final MongoUpdateInboxRepository mongoUpdateInboxRepository;
    private final MongoTemplate mongoTemplate;

    public MongoUpdateInboxRepositoryAdapter(
        SessionProperties sessionProperties, MongoUpdateInboxRepository mongoUpdateInboxRepository,
        MongoTemplate mongoTemplate
    ) {
        super(sessionProperties, mongoUpdateInboxRepository, MongoUpdateInbox.class);
        this.mongoUpdateInboxRepository = mongoUpdateInboxRepository;
        this.mongoTemplate = mongoTemplate;
        this.findAndModifyOptions = FindAndModifyOptions.options().returnNew(true);
    }

    public MongoUpdateInboxRepositoryAdapter(
        SessionProperties sessionProperties, MongoUpdateInboxRepository mongoUpdateInboxRepository,
        MongoTemplate mongoTemplate, FindAndModifyOptions findAndModifyOptions
    ) {
        super(sessionProperties, mongoUpdateInboxRepository, MongoUpdateInbox.class);
        this.mongoUpdateInboxRepository = mongoUpdateInboxRepository;
        this.mongoTemplate = mongoTemplate;
        this.findAndModifyOptions = findAndModifyOptions;
    }

    @Override
    protected void persist(MongoUpdateInbox entity) {
        entity.setCreatedAt(Instant.now());
        mongoUpdateInboxRepository.insert(entity);
    }

    @Override
    protected Optional<MongoUpdateInbox> findWithMaxThreadsPerUser(int maxThreadsPerUser) {
        Aggregation agg = Aggregation.newAggregation(
            Aggregation.match(Criteria.where(STATUS).is(NEW)),
            Aggregation.lookup(getCollection(), ENTITY_ID, ENTITY_ID, "allEntities"),
            Aggregation.addFields().addField("inProgressEntities").withValue(
                Filter.filter("allEntities").as("e")
                    .by(ComparisonOperators.Eq.valueOf("e.status").equalToValue(IN_PROGRESS))
            ).build(),
            Aggregation.match(new Criteria().orOperator(
                Criteria.where("inProgressEntities").size(0), Criteria.where(ENTITY_ID).isNull())),
            Aggregation.sort(Sort.by(Sort.Direction.ASC, ENTITY_ID, "createdAt")),
            Aggregation.group(ENTITY_ID).first(Aggregation.ROOT).as("doc"),
            Aggregation.replaceRoot("doc"),
            Aggregation.project().andExclude("allEntities", "inProgressEntities")
        );

        AggregationResults<MongoUpdateInbox> results = mongoTemplate.aggregate(
            agg, MongoUpdateInbox.class, MongoUpdateInbox.class);
        List<MongoUpdateInbox> documents = results.getMappedResults();

        for (MongoUpdateInbox updateInbox : documents) {
            Query query = new Query();
            Integer updateId = updateInbox.getUpdateId();
            query.addCriteria(Criteria.where(STATUS).is(NEW).and("_id").is(updateId));
            query.with(Sort.by(Sort.Direction.ASC, CREATED_AT));

            Update update = new Update()
                .set(STATUS, IN_PROGRESS)
                .set(UPDATED_AT, Instant.now());

            MongoUpdateInbox andModify = mongoTemplate.findAndModify(query, update, findAndModifyOptions,
                MongoUpdateInbox.class);
            if (andModify != null) {
                return Optional.of(andModify);
            } else {
                log.debug("Skip {} due to another thread took to work", updateId);
            }
        }

        return Optional.empty();
    }

    @NotNull
    protected String getCollection() {
        Document document = MongoUpdateInbox.class.getDeclaredAnnotation(Document.class);
        if (document != null) {
            return document.collection();
        }
        throw new IllegalStateException("No collection annotation found");
    }

    @Override
    @Transactional
    public Optional<MongoUpdateInbox> findNextUpdate() {
        return super.findNextUpdate();
    }

    @Override
    protected Optional<MongoUpdateInbox> findNextWithoutLimit() {
        Query query = new Query();
        query.addCriteria(Criteria.where(STATUS).is(NEW));
        query.with(Sort.by(Sort.Direction.ASC, CREATED_AT));
        query.limit(1);

        Update update = new Update()
            .set(STATUS, IN_PROGRESS)
            .set(UPDATED_AT, Instant.now());

        return Optional.ofNullable(
            mongoTemplate.findAndModify(query, update, findAndModifyOptions, MongoUpdateInbox.class));
    }

    @Override
    protected List<MongoUpdateInbox> getIdleEntities(Instant date) {
        Query query = new Query();
        query.addCriteria(Criteria.where(STATUS).is(IN_PROGRESS).and(UPDATED_AT).lt(date));
        return mongoTemplate.find(query, MongoUpdateInbox.class);
    }
}
