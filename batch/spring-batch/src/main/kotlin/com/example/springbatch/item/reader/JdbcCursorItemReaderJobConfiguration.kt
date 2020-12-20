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

// https://jojoldu.tistory.com/336?category=902551
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
            .chunk<Pay, Pay>(chunkSize) // Reader & Writer가 묶일 Chunk 트랜잭션 범위
            .reader(jdbcCursorItemReader())
            .writer(jdbcCursorItemWriter())
            .build()

    @Bean
    fun jdbcCursorItemReader(): ItemReader<Pay> = JdbcCursorItemReaderBuilder<Pay>()
            .fetchSize(chunkSize) // Database에서 한번에 가져올 데이터 양
            .dataSource(dataSource)
            .rowMapper(BeanPropertyRowMapper<Pay>(Pay::class.java))
            .sql("SELECT id, amount, tx_name, tx_date_time FROM pay")
            .name("jdbcCursorItemReader")
            .build()

    private fun jdbcCursorItemWriter() = ItemWriter<Pay> { list ->
        list.forEach { log.info("Current Pay={$it}") }
    }

}
