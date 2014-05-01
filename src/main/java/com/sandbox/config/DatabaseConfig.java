package com.sandbox.config;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import static org.springframework.context.annotation.AdviceMode.ASPECTJ;

import com.jolbox.bonecp.BoneCPDataSource;

@Configuration
@EnableTransactionManagement(mode=ASPECTJ)
@ComponentScan(basePackages="com.sandbox.dao")
public class DatabaseConfig
{
	private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

	public final static String BEAN_NAME_DEFAULT_TX_TEMPLATE = "defaultTxTemplate";


	public static BoneCPDataSource buildBasicDataSourceOfBoneCP(Environment environment)
	{
		String databaseUrl = String.format(
			"jdbc:mysql://%1$s:3306/%2$s?%3$s",
			environment.getProperty("database.host"),
			environment.getProperty("database.name"),
			environment.getProperty("database.parameter")
		);

		logger.debug("Database URL: {}", databaseUrl);

		BoneCPDataSource dataSource = new BoneCPDataSource();

		dataSource.setJdbcUrl(databaseUrl);
		dataSource.setDriverClass(environment.getProperty("database.driver"));
		dataSource.setUsername(environment.getProperty("database.username"));
		dataSource.setPassword(environment.getProperty("database.password"));

		return dataSource;
	}

	@Bean(name = "dataSource", destroyMethod = "close")
	public DataSource buildDataSource(Environment environment)
	{
		BoneCPDataSource dataSource = buildBasicDataSourceOfBoneCP(environment);

		/**
		 * Number of pooling
		 */
		dataSource.setPartitionCount(2);
		dataSource.setMinConnectionsPerPartition(15);
		dataSource.setMaxConnectionsPerPartition(75);
		dataSource.setAcquireIncrement(3);
		// :~)

		/**
		 * Timeout/age of pooling
		 */
		dataSource.setIdleMaxAgeInMinutes(30);
		dataSource.setIdleConnectionTestPeriodInMinutes(240);
		dataSource.setConnectionTimeoutInMs(5000);
		// :~)

		dataSource.setDefaultAutoCommit(true);
		dataSource.setDeregisterDriverOnClose(true);

		return dataSource;
	}

	@Bean(name = "entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean buildEntityManager(
		@Named("dataSource") DataSource dataSource)
	{
		LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactory.setDataSource(dataSource);
		entityManagerFactory
			.setJpaDialect(new org.springframework.orm.jpa.vendor.HibernateJpaDialect());

		/**
		 * Setup the configuration of Hibernate
		 */
		Map<String, String> hibernateProperties = new HashMap<String, String>();
		hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
		hibernateProperties.put("hibernate.archive.autodetection", "class");
		hibernateProperties.put("hibernate.id.new_generator_mappings", "true");
		hibernateProperties.put("hibernate.format_sql", "true");
		entityManagerFactory.setJpaPropertyMap(hibernateProperties);
		// :~)

		return entityManagerFactory;
	}

	@Bean(name = "transactionManager")
	public JpaTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory)
	{
		return new JpaTransactionManager();
	}

	@Bean(name = BEAN_NAME_DEFAULT_TX_TEMPLATE)
	public TransactionTemplate buildDataSourceTxTemplate(JpaTransactionManager dataSourceTxManager)
	{
		return new TransactionTemplate(dataSourceTxManager);
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor()
	{
		return new PersistenceExceptionTranslationPostProcessor();
	}

}
