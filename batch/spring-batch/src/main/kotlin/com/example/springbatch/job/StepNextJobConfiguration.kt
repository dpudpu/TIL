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
class StepNextJobConfiguration(
        private val jobBuilderFactory: JobBuilderFactory,
        private val stepBuilderFactor: StepBuilderFactory
) {
    private val log = LoggerFactory.getLogger(StepNextJobConfiguration::class.java)

    @Bean
    fun stepNextJob(stepNext1: Step, stepNext2: Step): Job = jobBuilderFactory.get("stepNextJob")
            .start(stepNext1)
            .next(stepNext2)
            .build()

    @Bean
    fun stepNext1(): Step = stepBuilderFactor.get("nextStep1")
            .tasklet { _, _ ->
                log.info("------------ This is Next Step1")
                RepeatStatus.FINISHED
            }
            .build()

    @Bean
    fun stepNext2(): Step = stepBuilderFactor.get("nextStep2")
            .tasklet { _, _ ->
                log.info("------------ This is Next Step2")
                RepeatStatus.FINISHED
            }
            .build()
}
