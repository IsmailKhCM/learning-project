package org.example.userdemo.user.service

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.example.userdemo.user.dtos.CreateUserDTO
import org.example.userdemo.user.dtos.ReadUserDTO
import org.example.userdemo.user.dtos.UpdateUserDTO
import org.example.userdemo.user.enums.UserStatus
import org.example.userdemo.user.helpers.UserMapper
import org.example.userdemo.user.model.User
import org.example.userdemo.user.repositories.UserDao
import org.example.userdemo.user.specifications.UserSpecifications
import org.example.userdemo.dao.SortDirection
import org.example.userdemo.user.exceptions.*
import org.hibernate.exception.ConstraintViolationException
import org.modelmapper.ModelMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import java.time.Instant

@Service
class UserService @Autowired constructor(
    private val userDAO: UserDao,
    private val userMapper: UserMapper,
    private val modelMapper: ModelMapper
) {

    private val logger = LoggerFactory.getLogger(UserService::class.java)

    fun findAll(
        filterParams: Map<String, Any>,
        pageNumber: Int,
        pageSize: Int,
        sortBy: String,
        sortDirection: SortDirection
    ): Page<ReadUserDTO> {
        logger.info("Fetching all users")
        val spec = UserSpecifications.withDynamicQuery(filterParams)
        logger.debug("Filter params: $filterParams")

        // Ensure the pageNumber is at least 1
        val adjustedPageNumber = if (pageNumber < 1) 1 else pageNumber

        // Fetch the users using custom pagination and sorting
        val usersList = userDAO.findAll(spec, adjustedPageNumber, pageSize, sortBy, sortDirection)

        // Get total count for pagination
        val totalCount = userDAO.count(spec)

        // Create a Page object manually
        val pageable = PageRequest.of(
            adjustedPageNumber - 1,
            pageSize,
            if (sortDirection == SortDirection.ASC) Sort.by(sortBy).ascending()
            else Sort.by(sortBy).descending()
        )
        val usersPage = PageImpl(usersList, pageable, totalCount)

        return usersPage.map(userMapper::userToDto)
    }

    @Transactional
    fun create(createUserDTO: CreateUserDTO): ReadUserDTO {
        val user = userMapper.dtoToUser(createUserDTO)
        logger.info("Creating user: ${user.email}")
        return try {
            userDAO.save(user)
            userMapper.userToDto(user)
        } catch (e: DataIntegrityViolationException) {
            if (e.cause is ConstraintViolationException) {
                throw UserAlreadyExistsException("A user with email ${user.email} already exists.")
            }
            throw e
        }
    }

    fun findOne(id: Long): ReadUserDTO {
        logger.info("Fetching user by ID: $id")
        return userDAO.findById(id)?.let { userMapper.userToDto(it) }
            ?: throw EntityNotFoundException("User not found for ID: $id")
    }

    fun findPendingUsersOlderThan(dateTimeLimit: Instant): List<User> {
        return userDAO.findPendingUsersOlderThan(dateTimeLimit)
    }

    @Transactional
    fun update(id: Long, updateUserDTO: UpdateUserDTO): ReadUserDTO? {
        logger.info("Updating user with ID: $id")
        return userDAO.findById(id)?.let {
            modelMapper.map(updateUserDTO, it)
            userDAO.save(it)
            userMapper.userToDto(it)
        } ?: run {
            logger.error("User not found with ID: $id")
            null
        }
    }

    @Transactional
    fun updateStatus(id: Long, status: UserStatus): ReadUserDTO {
        logger.info("Updating status for user with ID: $id")
        return userDAO.findById(id)?.let {
            it.status = status
            userDAO.save(it)
            userMapper.userToDto(it)
        } ?: throw EntityNotFoundException("User not found with ID: $id")
    }

    fun deleteById(id: Long) {
        logger.info("Deleting user with ID: $id")
        val user = userDAO.findById(id)
        if (user != null) {
            userDAO.delete(user)
        } else {
            throw EntityNotFoundException("User not found for ID: $id")
        }
    }
}