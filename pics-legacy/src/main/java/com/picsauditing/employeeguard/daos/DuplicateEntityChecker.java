package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.validators.duplicate.DuplicateInfoProvider;
import com.picsauditing.util.Strings;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DuplicateEntityChecker {

    private static final Logger LOG = LoggerFactory.getLogger(DuplicateEntityChecker.class);

    @PersistenceContext
    private EntityManager em;

    public boolean isDuplicate(final DuplicateInfoProvider duplicateInfoProvider) {
        if (checkForInvalidDuplicateInfo(duplicateInfoProvider)) {
            throw new RuntimeException("In order to find duplicate entity, duplicate field info needs to be provided.");
        }

        Map<String, Object> indexableInfo = duplicateInfoProvider.getUniqueIndexable().getUniqueIndexableValues();

        int result = 1;
        try {
            TypedQuery<Long> query = em.createQuery(buildQueryForDuplicate(duplicateInfoProvider.getType(),
                    indexableInfo.keySet()), Long.class);
            query = setQueryParameters(query, duplicateInfoProvider.getUniqueIndexable().getId(), indexableInfo);

            result = query.getSingleResult().intValue();
        } catch (Exception e) {
            LOG.error("Error while performing duplicate check for {}", duplicateInfoProvider.getType().toString()
                    + Strings.EMPTY_STRING
                    + duplicateInfoProvider.getUniqueIndexable().getUniqueIndexableValues().toString());
        }

        return result > 0;
    }

    private boolean checkForInvalidDuplicateInfo(final DuplicateInfoProvider duplicateInfoProvider) {
        return duplicateInfoProvider == null || duplicateInfoProvider.getUniqueIndexable() == null
                || MapUtils.isEmpty(duplicateInfoProvider.getUniqueIndexable().getUniqueIndexableValues());
    }

    private String buildQueryForDuplicate(final Class<?> type, final Set<String> parameterNames) {
        StringBuilder queryString = new StringBuilder("SELECT COUNT(*) FROM ").append(type.getName())
                .append(" WHERE id != :id AND ");

        Iterator<String> iterator = parameterNames.iterator();
        String value = iterator.next();
        queryString.append(value).append(" = :").append(value);

        while (iterator.hasNext()) {
            value = iterator.next();
            queryString.append(" AND ").append(value).append(" = :").append(value);
        }

        return queryString.toString();
    }

    private TypedQuery<Long> setQueryParameters(final TypedQuery<Long> query,
                                                int id, final Map<String, Object> parameters) {
        query.setParameter("id", id);

        Set<String> parameterNames = parameters.keySet();
        for (String parameterName : parameterNames) {
            query.setParameter(parameterName, parameters.get(parameterName));
        }

        return query;
    }
}
