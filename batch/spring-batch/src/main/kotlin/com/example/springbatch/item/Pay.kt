package com.example.springbatch.item

import java.time.LocalDateTime
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

data class Pay(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private val id: Long? = null,
        private val amount: String,
        private val txName: String,
        private val txDateTime: LocalDateTime
)

