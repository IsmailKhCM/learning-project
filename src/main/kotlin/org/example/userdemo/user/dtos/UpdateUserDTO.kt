package org.example.userdemo.user.dtos

import jakarta.validation.constraints.Email
import org.example.userdemo.user.enums.UserStatus

data class UpdateUserDTO(
  val firstName: String?,
  val lastName: String?,
  @field:Email(message = "Email must be a valid email address")
  val email: String?,
  val balance: Long?,
  val status: UserStatus?
)
