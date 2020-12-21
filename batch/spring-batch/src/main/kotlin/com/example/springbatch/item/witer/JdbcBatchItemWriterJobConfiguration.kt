package com.example.springbatch.item.witer

import com.example.springbatch.item.Pay
import com.example.springbatch.item.reader.JdbcCursorItemReaderJobConfiguration
import org.slf4j.LoggerFactory
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.database.JdbcBatchItemWriter
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.BeanPropertyRowMapper
import javax.sql.DataSource

@Configuration
class JdbcBatchItemWriterJobConfiguration (
        private val jobBuilderFactory: JobBuilderFactory,
        private val stepBuilderFactory: StepBuilderFactory,
        private val dataSource: DataSource
) {
    private val log = LoggerFactory.getLogger(JdbcBatchItemWriterJobConfiguration::class.java)
    private val chunkSize = 10

    @Bean
    fun jdbcBatchItemWriterJob() = jobBuilderFactory.get("jdbcBatchItemWriterJob")
            .start(jdbcBatchItemWriterStep())
            .build()

     fun jdbcBatchItemWriterStep()= stepBuilderFactory.get("jdbcBatchItemWriterStep")
             .chunk<Pay, Pay>(chunkSize)
             .reader(jdbcBatchItemWriterReader())
             .writer(jdbcBatchItemWriter())
             .build()

    @Bean
    fun jdbcBatchItemWriterReader(): ItemReader<Pay> = JdbcCursorItemReaderBuilder<Pay>()
            .fetchSize(chunkSize) // Database에서 한번에 가져올 데이터 양
            .dataSource(dataSource)
            .rowMapper(BeanPropertyRowMapper<Pay>(Pay::class.java))
            .sql("SELECT id, amount, tx_name, tx_date_time FROM pay")
            .name("jdbcCursorItemReader")
            .build()

    /**
     * reader에서 넘어온 데이터를 하나씩 출력하는 writer
     */
    @Bean
    fun jdbcBatchItemWriter(): JdbcBatchItemWriter<Pay> = JdbcBatchItemWriterBuilder<Pay>()
            .dataSource(dataSource)
            .sql("insert into pay2(amount, tx_name, tx_date_time) values (:amount, :txName, :txDateTime)")
            .beanMapped() // Pojo 기반으로 Insert SQL의 Values를 매핑
            .build()
}
