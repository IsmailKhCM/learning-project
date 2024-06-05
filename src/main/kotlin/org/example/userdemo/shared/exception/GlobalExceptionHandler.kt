package org.example.userdemo.shared.exception

import jakarta.persistence.EntityNotFoundException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.http.ResponseEntity
import org.springframework.web.context.request.WebRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.example.userdemo.shared.response.ApiResponse

@ControllerAdvice
class GlobalExceptionHandler {

  // Handle validation exceptions specifically
  @ExceptionHandler(MethodArgumentNotValidException::class)
  fun handleValidationExceptions(
    ex: MethodArgumentNotValidException,
    request: WebRequest
  ): ResponseEntity<ApiResponse<Any>> {
    val errors = ex.bindingResult.fieldErrors.joinToString("; ") { error ->
      "${error.field}: ${error.defaultMessage}"
    }
    val response = ApiResponse(success = false, data = null as Any?, message = "Validation failed: $errors")
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
  }

  // Handle not found exceptions
  @ExceptionHandler(NoSuchElementException::class, EntityNotFoundException::class)
  fun handleNotFoundException(ex: Exception): ResponseEntity<ApiResponse<Any>> {
    val response = ApiResponse(success = false, data = null as Any?, message = ex.message ?: "Resource not found")
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
  }

  // Handle generic exceptions
  @ExceptionHandler(Exception::class)
  fun handleGeneralException(ex: Exception): ResponseEntity<ApiResponse<Any>> {
    val response =
      ApiResponse(success = false, data = null as Any?, message = "An unexpected error occurred: ${ex.message}")
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
  }
}


