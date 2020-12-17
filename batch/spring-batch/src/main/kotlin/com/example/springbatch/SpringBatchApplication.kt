package com.example.springbatch

import org.slf4j.LoggerFactory
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import javax.annotation.PostConstruct

@EnableBatchProcessing
@SpringBootApplication
class SpringBatchApplication {
private val log = LoggerFactory.getLogger(SpringBatchApplication::class.java)

    @Value("\${spring.batch.job.names:NONE}")
    private lateinit var jobNames: String

    @PostConstruct
    fun validateJobNames() {
        log.info("jobNames : {}", jobNames)
        check(!(jobNames.isEmpty() || jobNames == "NONE")) { "spring.batch.job.names=job1,job2 형태로 실행을 원하는 Job을 명시해야만 합니다!" }
    }

}
fun main(args: Array<String>) {
    runApplication<SpringBatchApplication>(*args)
}
