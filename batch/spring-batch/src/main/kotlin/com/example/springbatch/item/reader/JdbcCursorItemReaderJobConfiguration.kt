package com.example.springbatch.item.reader

import com.example.springbatch.item.Pay
import org.slf4j.LoggerFactory
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.BeanPropertyRowMapper
import javax.sql.DataSource

@Configuration
class JdbcCursorItemReaderJobConfiguration(
        private val jobBuilderFactory: JobBuilderFactory,
        private val stepBuilderFactory: StepBuilderFactory,
        private val dataSource: DataSource
) {
    private val log = LoggerFactory.getLogger(JdbcCursorItemReaderJobConfiguration::class.java)
    private val chunkSize = 10

    @Bean
    fun jdbcCursorItemReaderJob() = jobBuilderFactory.get("jdbcCursorItemReaderJob")
            .start(jdbcCursorItemReaderStep())
            .build()

    @Bean
    fun jdbcCursorItemReaderStep() = stepBuilderFactory.get("jdbcCursorItemReaderStep")
            .chunk<Pay, Pay>(chunkSize)
            .reader(jdbcCursorItemReader())
            .writer(jdbcCursorItemWriter())
            .build()

    @Bean
    fun jdbcCursorItemReader(): ItemReader<Pay> = JdbcCursorItemReaderBuilder<Pay>()
            .fetchSize(chunkSize)
            .dataSource(dataSource)
            .rowMapper(BeanPropertyRowMapper(Pay::class.java))
            .sql("SELECT id, amout, tx_name, tx_date_time FROM pay")
            .name("jdbcCursorItemReader")
            .build()

    private fun jdbcCursorItemWriter() = ItemWriter<Pay> { list ->
        list.forEach { log.info("Current Pay={$it}") }
    }

}
