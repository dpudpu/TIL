package com.example.springbatch.item

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Pay(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
         var id: Long? = null,
         var amount: String = "",
         var txName: String = "",
         var txDateTime: LocalDateTime = LocalDateTime.now()
)

