package org.example.userdemo.shared.response

data class ApiResponse<T>(
  val success: Boolean,
  val data: T?,
  val message: String?
)
