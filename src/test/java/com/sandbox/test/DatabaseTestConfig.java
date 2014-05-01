package com.sandbox.test;

import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.no_ip.mikelue.jpa.test.dbunit.DbUnitAction;
import org.no_ip.mikelue.jpa.test.dbunit.DbUnitBuilder;
import org.no_ip.mikelue.jpa.test.dbunit.annotation.ReflectAnnotationDbUnitContext;
import org.no_ip.mikelue.jpa.test.liquibase.LiquibaseAction;
import org.no_ip.mikelue.jpa.test.liquibase.LiquibaseBuilder;
import org.no_ip.mikelue.jpa.test.springframework.AnnotationDataSetBuilder;
import org.no_ip.mikelue.jpa.test.testng.Action;
import org.no_ip.mikelue.jpa.test.testng.ChainedAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;

import static com.sandbox.config.DatabaseConfig.buildBasicDataSourceOfBoneCP;
import static org.dbunit.database.DatabaseConfig.PROPERTY_DATATYPE_FACTORY;
import static org.dbunit.database.DatabaseConfig.PROPERTY_ESCAPE_PATTERN;

import static org.no_ip.mikelue.jpa.test.springframework.DataSetBuilder.buildWithYaml;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

import com.jolbox.bonecp.BoneCPDataSource;

@Configuration
@ComponentScan({"", ""})
public class DatabaseTestConfig {
    /**
     * The default file of dataset for testing data({@value #DEFAULT_TEST_DATASET}).<p>
     */
    public final static String DEFAULT_TEST_DATASET = "classpath:DefaultTestDataSet.yaml";
    /**
     * The default file of dataset for testing data({@value #DEFAULT_TEST_DATASET}).<p>
     */
    public final static String DEFAULT_REMOVE_DATASET = "classpath:DefaultRemoveDataSet.yaml";

    private Logger logger = LoggerFactory.getLogger(DatabaseTestConfig.class);

	/**
	 * Override the default configuration from JNDI resource.<p>
	 */
    @Bean(name="dataSource", destroyMethod="close")
    public DataSource buildDataSource(
		Environment environment
	) {
		BoneCPDataSource dataSource = buildBasicDataSourceOfBoneCP(environment);

		dataSource.setPartitionCount(1);
		dataSource.setMinConnectionsPerPartition(2);
		dataSource.setMaxConnectionsPerPartition(10);
		dataSource.setDeregisterDriverOnClose(false);

		return dataSource;
    }


    /**
     * This action is used to upgrade schema in testing.<p>
     */
    @Bean(name="upgradeSchemaAction")
    public Action buildLiquibaseAction(
        DataSource dataSource
    ) {
        return new LiquibaseAction(
            LiquibaseBuilder.build(
                "src/main/liquibase/main.xml",
                new liquibase.resource.FileSystemResourceAccessor(),
                dataSource
            ),
            new org.no_ip.mikelue.jpa.test.liquibase.UpdateSchemaExecutor()
        );
    }

    /**
     * Builds the factory of data type(DBUnit) to support enum type in MySQL.<p>
     *
     * The name prefix with "enum_" is the enum in MySQL for this project.<p>
     */
    @Bean
    public MySqlDataTypeFactory buildMySQLDataTypeFactory()
    {
        return new MySqlDataTypeFactory();
    }

    /**
     * This action is used to refresh data in testing.<p>
     */
    @Bean(name="refreshTestDataAction") @Scope(SCOPE_PROTOTYPE)
    public Action buildDbUnitAction(
        DbUnitBuilder dbUnitBuilder,
        @Value(DEFAULT_TEST_DATASET) Resource defaultTestDataSetSource,
        @Value(DEFAULT_REMOVE_DATASET) Resource defaultRemoveDataSetSource
    ) {
		return new ChainedAction(
			/**
			 * Default creation of test data
			 */
			new DbUnitAction(
				dbUnitBuilder, org.dbunit.operation.DatabaseOperation.REFRESH,
				buildWithYaml(defaultTestDataSetSource)
			),
			// :~)
			/**
			 * Default removal of test data(built by trigger)
			 */
			new DbUnitAction(
				dbUnitBuilder, org.dbunit.operation.DatabaseOperation.DELETE,
				buildWithYaml(defaultRemoveDataSetSource)
			)
			// :~)
		);
    }

    @Bean
    public DbUnitBuilder dbUnitBuilder(
        DataSource dataSource, MySqlDataTypeFactory dataTypeFactory
    ) {
		DbUnitBuilder dbUnitBuilder = DbUnitBuilder.build(dataSource, dataTypeFactory);
		dbUnitBuilder.setRunAsTransaction(true);

        return dbUnitBuilder;
    }

    @Bean
    public ReflectAnnotationDbUnitContext buildReflectAnnotationDbUnitContext(
        DbUnitBuilder dbUnitBuilder, BeanFactory beanFactory
    ) {
        AnnotationDataSetBuilder dataSetBuilder = new AnnotationDataSetBuilder();
        dataSetBuilder.setBeanFactory(beanFactory);

        return new ReflectAnnotationDbUnitContext(
            dbUnitBuilder, dataSetBuilder
        );
    }

    /**
     * Build dataset from database(prototype).<p>
     *
     * This builder would exclude tables created by Liquibase.<p>
     */
    @Bean @Lazy
    public GlobalRollbackDataSet buildDatabaseDataSet(
		DataSource dataSource, MySqlDataTypeFactory dataTypeFactory,
		@Value(DEFAULT_TEST_DATASET) Resource defaultTestDataSetSource
	) throws SQLException, DataSetException
    {
        logger.info("Construct dataset of current state of database for rollback");

        IDatabaseConnection dbConn = null;

        try {
            dbConn = new org.dbunit.database.DatabaseDataSourceConnection(dataSource);
			dbConn.getConfig().setProperty(PROPERTY_DATATYPE_FACTORY, dataTypeFactory);
			dbConn.getConfig().setProperty(PROPERTY_ESCAPE_PATTERN, "`?`");

			return new GlobalRollbackDataSet(
				buildWithYaml(defaultTestDataSetSource)
			);
        } finally {
            if (dbConn != null) {
                dbConn.close();
            }
        }
    }

    @Bean
    public ApplicationListener<ContextRefreshedEvent> buildDatabaseTestDataProvider(
    ) {
        return new DatabaseTestDataProvider();
    }

}

/**
 * Prepares database after context has been initialized.<p>
 *
 * <ol>
 *  <li>Upgrade the schema of database</li>
 *  <li>Update the data for testing(clean and re-insert)</li>
 *  <li>Build the dataset which is used to rollback the status of database built by two previous actions</li>
 * </ol>
 */
class DatabaseTestDataProvider implements ApplicationListener<ContextRefreshedEvent>, Ordered {
    private Logger logger = LoggerFactory.getLogger(DatabaseTestDataProvider.class);

    @Inject @Named("upgradeSchemaAction")
    private Action upgradeSchemaAction;
    @Inject @Named("refreshTestDataAction")
    private Action refreshTestDataAction;
    @Inject
    private BeanFactory beanFactory;

    public DatabaseTestDataProvider() {}

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event)
    {
        logger.debug("Before prepare database...");

        /**
         * Upgrade the schema of database
         */
        upgradeSchemaAction.executeAction();
        logger.debug("Upgrade schema of database finished");
        // :~)

        /**
         * Update the data for testing(clean and re-insert)
         */
        refreshTestDataAction.executeAction();
        logger.debug("Refresh data for testing finished");
        // :~)

        logger.debug("Preparing database finished");

		beanFactory.getBean(GlobalRollbackDataSet.class);
    }

    @Override
    public int getOrder()
    {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
