package com.example.springbatch.job

import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.Step
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.job.flow.FlowExecutionStatus
import org.springframework.batch.core.job.flow.JobExecutionDecider
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*


/**
Step에서 조건별 흐름 처리시 담당하는 역할이 2개 이상이 됩니다.
실제 해당 Step이 처리해야할 로직외에도 분기처리를 시키기 위해 ExitStatus 조작이 필요합니다.
- 다양한 분기 로직 처리의 어려움
- ExitStatus를 커스텀하게 고치기 위해선 Listener를 생성하고 Job Flow에 등록하는 등 번거로움이 존재합니다.

Reference: https://jojoldu.tistory.com/328?category=902551
 */
@Configuration
class DeciderJobConfiguration(
        private val jobBuilderFactory: JobBuilderFactory,
        private val stepBuilderFactor: StepBuilderFactory
) {
    private val log = LoggerFactory.getLogger(DeciderJobConfiguration::class.java)

    @Bean
    fun deciderJob(): Job = jobBuilderFactory.get("deciderJob")
            .start(startStep())
            .next(decider()) // 홀수 | 짝수 구분
            .from(decider()) // decider의 상태가
            .on("ODD") // ODD라면
            .to(oddStep()) // oddStep로 간다.
            .from(decider()) // decider의 상태가
            .on("EVEN") // ODD라면
            .to(evenStep()) // evenStep로 간다.
            .end() // builder 종료
            .build()

    @Bean
    fun startStep(): Step = stepBuilderFactor.get("startStep")
            .tasklet { _, _ ->
                log.info("------------ Start")
                RepeatStatus.FINISHED
            }
            .build()

    @Bean
    fun evenStep(): Step = stepBuilderFactor.get("evenStep")
            .tasklet { _, _ ->
                log.info("------------ 짝수입니다")
                RepeatStatus.FINISHED
            }
            .build()

    @Bean
    fun oddStep(): Step = stepBuilderFactor.get("oddStep")
            .tasklet { _, _ ->
                log.info("------------ 홀수입니다")
                RepeatStatus.FINISHED
            }
            .build()

    @Bean
    fun decider() = JobExecutionDecider { _: JobExecution, _: StepExecution? ->
        val randomNumber: Int = Random().nextInt(50) + 1
        log.info("------------- 랜덤숫자: {}", randomNumber)
        if (randomNumber % 2 == 0)
            FlowExecutionStatus("EVEN")
        else
            FlowExecutionStatus("ODD")
    }

//    inner class OddDecider : JobExecutionDecider {
//        override fun decide(jobExecution: JobExecution, stepExecution: StepExecution?): FlowExecutionStatus {
//            val rand = Random()
//            val randomNumber: Int = rand.nextInt(50) + 1
//            log.info("------------- 랜덤숫자: {}", randomNumber)
//            return if (randomNumber % 2 == 0) {
//                FlowExecutionStatus("EVEN")
//            } else {
//                FlowExecutionStatus("ODD")
//            }
//        }
//    }
}
