package org.example.userdemo.jobs

import org.example.userdemo.user.enums.UserStatus
import org.example.userdemo.user.service.UserService
import org.springframework.stereotype.Component
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.springframework.scheduling.annotation.Scheduled
import java.time.LocalDateTime
import java.time.ZoneId

@Component
class UpdateUserStatusJob(
    private val userService: UserService
) : Job {

    companion object {
        private const val MINUTES_AGO = 1L
    }

    override fun execute(context: JobExecutionContext?) {
        updateUserStatuses()
    }

    @Scheduled(cron = "0 * * * * ?")
    fun scheduleUpdateUserStatuses() {
        updateUserStatuses()
    }

    private fun updateUserStatuses() {
        val timeThreshold = LocalDateTime.now().minusMinutes(MINUTES_AGO)
        val dateTimeLimit = timeThreshold.atZone(ZoneId.systemDefault()).toInstant()

        val usersToUpdate = userService.findPendingUsersOlderThan(dateTimeLimit)
        usersToUpdate.forEach { user ->
            userService.updateStatus(user.id, UserStatus.REJECTED)
        }
    }
}