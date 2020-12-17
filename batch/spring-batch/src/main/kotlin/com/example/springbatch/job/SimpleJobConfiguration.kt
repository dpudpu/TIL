package com.example.springbatch.job

import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SimpleJobConfiguration(
        private val jobBuilderFactory: JobBuilderFactory,
        private val stepBuilderFactor: StepBuilderFactory
) {
    private val log = LoggerFactory.getLogger(SimpleJobConfiguration::class.java)

    @Bean
    fun simpleJob(simpleStep1: Step, simpleStep2: Step): Job = jobBuilderFactory.get("simpleJob")
            .start(simpleStep1)
            .next(simpleStep2)
            .build()

    @Bean
    @JobScope
    fun simpleStep1(
            @Value("#{jobParameters[requestDate]}") requestDate: String
    ): Step = stepBuilderFactor.get("simpleStep1")
            .tasklet { _, _ ->
                log.info(">>>>>> This is Simple Step1")
                log.info(">>>>> requestDate = {}", requestDate)
                RepeatStatus.FINISHED
            }
            .build()

    @Bean
    @JobScope
    fun simpleStep2(
            @Value("#{jobParameters[requestDate]}") requestDate: String
    ): Step = stepBuilderFactor.get("simpleStep2")
            .tasklet { _, _ ->
                log.info(">>>>>> This is Simple Step2")
                log.info(">>>>> requestDate = {}", requestDate)
                RepeatStatus.FINISHED
            }
            .build()
}
