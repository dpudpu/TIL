package com.example.springbatch.item.reader

import com.example.springbatch.item.Pay
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManagerFactory

@Configuration
class JpaPagingItemReaderJobConfiguration(
        private val jobBuilderFactory: JobBuilderFactory,
        private val stepBuilderFactory: StepBuilderFactory,
        private val emf: EntityManagerFactory
) {
    private val log = LoggerFactory.getLogger(JpaPagingItemReaderJobConfiguration::class.java)
    private val chunkSize = 10

    @Bean
    fun jpaPagingItemReaderJob(): Job = jobBuilderFactory["jpaPagingItemReaderJob"]
            .start(jpaPagingItemReaderStep())
            .build()

    @Bean
    fun jpaPagingItemReaderStep(): Step = stepBuilderFactory["jpaPagingItemReaderStep"]
            .chunk<Pay, Pay>(chunkSize)
            .reader(jpaPagingItemReader())
            .writer(jpaPagingItemWriter())
            .build()

    @Bean
    fun jpaPagingItemReader(): JpaPagingItemReader<Pay> = JpaPagingItemReaderBuilder<Pay>()
            .name("jpaPagingItemReader")
            .entityManagerFactory(emf)
            .pageSize(chunkSize)
            .queryString("SELECT p FROM Pay p WHERE p.amount >= 2000")
            .build()

    private fun jpaPagingItemWriter() = ItemWriter<Pay> { list ->
        list.forEach { log.info("Current Pay={$it}") }
    }
}
