package org.example.userdemo.user.dtos

import java.time.Instant

data class ReadUserDTO (
  val id: Long? = null,
  val firstName: String? = null,
  val lastName: String? = null,
  val email: String? = null,
  val balance: Long? = null,
  val status: String? = null,
  val creationTime: Instant? = null,
)
