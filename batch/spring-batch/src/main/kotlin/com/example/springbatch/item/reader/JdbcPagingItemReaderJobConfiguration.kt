package com.example.springbatch.item.reader

import com.example.springbatch.item.Pay
import org.slf4j.LoggerFactory
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JdbcPagingItemReader
import org.springframework.batch.item.database.Order
import org.springframework.batch.item.database.PagingQueryProvider
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.BeanPropertyRowMapper
import javax.sql.DataSource

@Configuration
class JdbcPagingItemReaderJobConfiguration(
        private val jobBuilderFactory: JobBuilderFactory,
        private val stepBuilderFactory: StepBuilderFactory,
        private val dataSource: DataSource
) {
    private val log = LoggerFactory.getLogger(JdbcPagingItemReaderJobConfiguration::class.java)
    private val chunkSize = 10

    @Bean
    fun jdbcPagingItemReaderJob() = jobBuilderFactory.get("jdbcPagingItemReaderJob")
            .start(jdbcPagingItemReaderStep())
            .build()

    @Bean
    fun jdbcPagingItemReaderStep() = stepBuilderFactory.get("jdbcPagingItemReaderStep")
            .chunk<Pay, Pay>(chunkSize) // Reader & Writer가 묶일 Chunk 트랜잭션 범위
            .reader(jdbcPagingItemReader())
            .writer(jdbcPagingItemWriter())
            .build()

    @Bean
    fun jdbcPagingItemReader(): JdbcPagingItemReader<Pay> {
        val parameterValues = mapOf<String, Any>("amount" to 2000) // 쿼리에 대한 매개 변수 eg: queryProvider.setWhereClause

        return JdbcPagingItemReaderBuilder<Pay>()
                .pageSize(chunkSize)
                .fetchSize(chunkSize) // Database에서 한번에 가져올 데이터 양
                .dataSource(dataSource)
                .rowMapper(BeanPropertyRowMapper<Pay>(Pay::class.java))
                .queryProvider(createQueryProvider())
                .parameterValues(parameterValues)
                .name("jdbcPagingItemReader")
                .build()
    }

    private fun jdbcPagingItemWriter() = ItemWriter<Pay> { list ->
        list.forEach { log.info("Current Pay={$it}") }
    }

    // SqlPagingQueryProviderFactoryBean을 통해 Datasource 설정값을 보고 적합한 Provider를 자동으로 선택
    @Bean
    fun createQueryProvider(): PagingQueryProvider =
            SqlPagingQueryProviderFactoryBean().apply {
                setDataSource(dataSource) // Database에 맞는 PagingQueryProvider를 선택하기 위해
                setSelectClause("id, amount, tx_name, tx_date_time")
                setFromClause("from pay")
                setWhereClause("where amount >= :amount")
                setSortKeys(mapOf<String, Order>("id" to Order.ASCENDING))
            }.getObject()
}
