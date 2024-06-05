package org.example.userdemo.user.dtos

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank


data class CreateUserDTO(
  @field:NotBlank(message = "First name must not be blank")
  var firstName: String,

  @field:NotBlank(message = "Last name must not be blank")
  var lastName: String,

  @field:NotBlank(message = "Email must not be blank")
  @field:Email(message = "Email must be a valid email address")

  var email: String,

  var balance: Long = 0,
)
