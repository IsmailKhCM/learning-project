package org.example.userdemo.user.controller

import org.example.userdemo.shared.response.ApiResponse
import org.example.userdemo.user.dtos.CreateUserDTO
import org.example.userdemo.user.dtos.ReadUserDTO
import org.example.userdemo.user.dtos.UpdateUserDTO
import org.example.userdemo.user.service.UserService
import org.example.userdemo.dao.SortDirection
import org.example.userdemo.user.exceptions.UserAlreadyExistsException
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.logging.Logger

@RestController
@RequestMapping("/user")
class UserController(private val userService: UserService) {

  private val logger = Logger.getLogger(UserController::class.java.name)

  @GetMapping("/{id}")
  fun getUserById(@PathVariable id: Long): ResponseEntity<ApiResponse<ReadUserDTO>> {
    return try {
      val userDTO = userService.findOne(id)
      ResponseEntity.ok(ApiResponse(success = true, data = userDTO, message = null))
    } catch (e: Exception) {
      logger.severe("Error retrieving user: ${e.message}")
      ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiResponse(success = false, data = null, message = "Error retrieving user: ${e.message}"))
    }
  }

  @PostMapping
  fun createUser(@RequestBody @Validated createUserDTO: CreateUserDTO): ResponseEntity<ApiResponse<ReadUserDTO>> {
    logger.info("Creating user with data: $createUserDTO")
    return try {
      val savedUser = userService.create(createUserDTO)
      ResponseEntity.ok(ApiResponse(success = true, data = savedUser, message = null))
    } catch (e: UserAlreadyExistsException) {
      logger.severe("Error saving user: ${e.message}")
      ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ApiResponse(success = false, data = null, message = e.message))
    } catch (e: Exception) {
      logger.severe("Error saving user: ${e.message}")
      ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse(success = false, data = null, message = "Error saving user: ${e.message}"))
    }
  }

  @GetMapping
  fun listAllUsers(
    @RequestParam filterParams: Map<String, String>,
    @RequestParam(defaultValue = "0") pageNumber: Int,
    @RequestParam(defaultValue = "10") pageSize: Int,
    @RequestParam(defaultValue = "id") sortBy: String,
    @RequestParam(defaultValue = "asc") sortDirection: String
  ): ResponseEntity<ApiResponse<Page<ReadUserDTO>>> {
    return try {
      val users = userService.findAll(filterParams, pageNumber, pageSize, sortBy, SortDirection.valueOf(sortDirection.uppercase()))
      ResponseEntity.ok(ApiResponse(success = true, data = users, message = null))
    } catch (e: Exception) {
      logger.severe("Error listing users: ${e.message}")
      ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse(success = false, data = null, message = "Error listing users: ${e.message}"))
    }
  }

  @PutMapping("/{id}")
  fun updateUser(@PathVariable id: Long, @RequestBody @Validated updateUserDTO: UpdateUserDTO): ResponseEntity<ApiResponse<ReadUserDTO>> {
    return try {
      val updatedUser = userService.update(id, updateUserDTO)
      ResponseEntity.ok(ApiResponse(success = true, data = updatedUser, message = null))
    } catch (e: Exception) {
      logger.severe("Error updating user: ${e.message}")
      ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse(success = false, data = null, message = "Error updating user: ${e.message}"))
    }
  }

  @DeleteMapping("/{id}")
  fun deleteUser(@PathVariable id: Long): ResponseEntity<ApiResponse<Any>> {
    return try {
      userService.deleteById(id)
      ResponseEntity.ok(ApiResponse(success = true, data = null, message = "User deleted successfully"))
    } catch (e: Exception) {
      logger.severe("Error deleting user: ${e.message}")
      ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse(success = false, data = null, message = "Error deleting user: ${e.message}"))
    }
  }
}