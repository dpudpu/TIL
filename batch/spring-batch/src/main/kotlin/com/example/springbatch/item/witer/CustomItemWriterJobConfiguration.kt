package com.example.springbatch.item.witer


import com.example.springbatch.item.Pay
import com.example.springbatch.item.Pay2
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManagerFactory


@Configuration
class CustomItemWriterJobConfiguration(
        private val jobBuilderFactory: JobBuilderFactory,
        private val stepBuilderFactory: StepBuilderFactory,
        private val emf: EntityManagerFactory
) {
    private val chunkSize = 10


    @Bean
    fun customItemWriterJob() = jobBuilderFactory["customItemWriterJob"]
            .start(customItemWriterStep())
            .build()

    @Bean
    fun customItemWriterStep() = stepBuilderFactory["customItemWriterStep"]
            .chunk<Pay, Pay2>(chunkSize)
            .reader(customItemWriterReader())
            .processor(customItemWriterProcessor())
            .writer(customItemWriter())
            .build()

    @Bean
    fun customItemWriterReader(): JpaPagingItemReader<Pay> = JpaPagingItemReaderBuilder<Pay>()
            .name("customItemWriterReader")
            .entityManagerFactory(emf)
            .pageSize(chunkSize)
            .queryString("SELECT p FROM Pay p")
            .build()

    @Bean
    fun customItemWriterProcessor() = ItemProcessor<Pay, Pay2> {
        Pay2(amount = it.amount, txName = it.txName, txDateTime = it.txDateTime)
    }

    @Bean
    fun customItemWriter(): ItemWriter<Pay2> = ItemWriter<Pay2> { items ->
        items.forEach { item -> println(">>>>> $item") }
    }
}

