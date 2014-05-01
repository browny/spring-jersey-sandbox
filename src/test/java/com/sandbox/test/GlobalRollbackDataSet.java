package com.sandbox.test;

import org.dbunit.dataset.CachedDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.filter.ExcludeTableFilter;

/**
 * This dataset is used to rollback to initialized state of database, globally.<p>
 *
 * This dataset should be initialized as a singleton object, which means that
 * any test uses this dataset is rollback to global state of database.<p>
 */
public class GlobalRollbackDataSet extends CachedDataSet
{
    /**
     * Initialize this dataset by any type of source dataset.<p>
     */
    public GlobalRollbackDataSet(IDataSet sourceDataSet) throws DataSetException
    {
        super(new FilteredDataSet(
            new ExcludeTableFilter(new String[] {
				"DATABASECHANGELOG", "DATABASECHANGELOGLOCK" // Tables from Liquibase
            }),
            sourceDataSet
        ));
    }
}
