package com.example.springbatch.item

import java.time.LocalDateTime
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

data class Pay(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
         var id: Long? = null,
         var amount: String = "",
         var txName: String = "",
         var txDateTime: LocalDateTime = LocalDateTime.now()
)

