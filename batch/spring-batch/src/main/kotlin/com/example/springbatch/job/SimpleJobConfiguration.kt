package com.example.springbatch.job

import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SimpleJobConfiguration(
        val jobBuilderFactory: JobBuilderFactory,
        val stepBuilderFactor: StepBuilderFactory
) {
    private val log = LoggerFactory.getLogger(SimpleJobConfiguration::class.java)

    @Bean
    fun simpleJob(): Job = jobBuilderFactory.get("simpleJob")
            .start(simpleStep1())
            .build()

    @Bean
    fun simpleStep1(): Step = stepBuilderFactor.get("simpleStep1")
            .tasklet { _, _ ->
                log.info(">>>>>> This is Simple Step1")
                RepeatStatus.FINISHED
            }
            .build()
}
