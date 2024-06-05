package org.example.userdemo.shared.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
class JpaConfig {

  @Bean
  fun entityManagerFactory(dataSource: DataSource): LocalContainerEntityManagerFactoryBean {
    val factoryBean = LocalContainerEntityManagerFactoryBean()
    factoryBean.dataSource = dataSource
    factoryBean.jpaVendorAdapter = HibernateJpaVendorAdapter()
    factoryBean.setPackagesToScan("org.example.userdemo")
    return factoryBean
  }

  @Bean
  fun transactionManager(factoryBean: LocalContainerEntityManagerFactoryBean): JpaTransactionManager {
    val transactionManager = JpaTransactionManager()
    transactionManager.entityManagerFactory = factoryBean.`object`
    return transactionManager
  }
}
