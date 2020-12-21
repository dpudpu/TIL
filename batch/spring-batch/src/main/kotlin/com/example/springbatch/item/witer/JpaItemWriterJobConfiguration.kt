package com.example.springbatch.item.witer

import com.example.springbatch.item.Pay
import com.example.springbatch.item.Pay2
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.database.JpaItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManagerFactory


@Configuration
class JpaItemWriterJobConfiguration(
        private val jobBuilderFactory: JobBuilderFactory,
        private val stepBuilderFactory: StepBuilderFactory,
        private val emf: EntityManagerFactory
) {
    private val chunkSize = 10

    @Bean
    fun jpaItemWriterJob(): Job = jobBuilderFactory["jpaItemWriterJob"]
            .start(jpaItemWriterStep())
            .build()

    @Bean
    fun jpaItemWriterStep(): Step = stepBuilderFactory["jpaItemWriterStep"]
            .chunk<Pay, Pay2>(chunkSize)
            .reader(jpaItemWriterReader())
            .processor(jpaItemProcessor())
            .writer(jpaItemWriter())
            .build()

    @Bean
    fun jpaItemWriterReader(): JpaPagingItemReader<Pay> = JpaPagingItemReaderBuilder<Pay>()
            .name("jpaItemWriterReader")
            .entityManagerFactory(emf)
            .pageSize(chunkSize)
            .queryString("SELECT p FROM Pay p")
            .build()

    @Bean
    fun jpaItemProcessor() = ItemProcessor<Pay, Pay2> {
        Pay2(amount = it.amount, txName = it.txName, txDateTime = it.txDateTime)
    }

    @Bean //  JpaItemWriter 는 넘어온 Item을 그대로 entityManger.merge()로 테이블에 반영을 하기 때문에 Entity를 받아야 한다.
    fun jpaItemWriter(): JpaItemWriter<Pay2> = JpaItemWriter<Pay2>().apply { setEntityManagerFactory(emf) }
}
