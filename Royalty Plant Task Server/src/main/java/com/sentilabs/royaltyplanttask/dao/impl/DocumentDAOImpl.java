package com.sentilabs.royaltyplanttask.dao.impl;

import com.sentilabs.royaltyplanttask.dao.interfaces.DocumentDAO;
import org.hibernate.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by sentipy on 13/07/15.
 */
@Component
public class DocumentDAOImpl implements DocumentDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean lockRowInDatabaseById(Long documentId) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createSQLQuery("select * from documents d where d.id = :documentId for update nowait");
        query.setParameter("documentId", documentId);
        try {
            query.list();
        }
        catch (Throwable t) {
            return false;
        }
        return true;
        /*MapSqlParameterSource map = new MapSqlParameterSource()
                .addValue("documentId", documentId);
        try {
            namedParameterJdbcOperations
                    .queryForList("select * from documents d where d.id = :documentId for update nowait"
                            , map);
        }
        catch (Throwable t) {
            return false;
        }
        return true;*/
    }
}
