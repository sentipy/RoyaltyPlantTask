package com.sentilabs.royaltyplanttask.dao.interfaces;

/**
 * Created by sentipy on 13/07/15.
 */
public interface DocumentDAO {

    boolean lockRowInDatabaseById(Long documentId);
}
