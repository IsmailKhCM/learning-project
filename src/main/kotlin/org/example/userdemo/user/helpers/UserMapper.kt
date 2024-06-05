package org.example.userdemo.user.helpers

import org.example.userdemo.user.dtos.CreateUserDTO
import org.example.userdemo.user.dtos.ReadUserDTO
import org.example.userdemo.user.enums.UserStatus
import org.example.userdemo.user.model.User
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UserMapper @Autowired constructor(
    private val modelMapper: ModelMapper
) {

    fun dtoToUser(dto: CreateUserDTO): User {
         val user = modelMapper.map(dto, User::class.java)
        user.status = UserStatus.PENDING
        return user
    }

    fun userToDto(user: User): ReadUserDTO {
        return modelMapper.map(user, ReadUserDTO::class.java)
    }
}
