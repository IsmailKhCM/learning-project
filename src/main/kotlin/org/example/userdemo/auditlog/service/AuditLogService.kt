package org.example.userdemo.shared.service

import jakarta.transaction.Transactional
import org.example.userdemo.auditlog.model.AuditLog
import org.example.userdemo.auditlog.repositories.AuditLogDAO

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AuditLogService @Autowired constructor(private val auditLogDao: AuditLogDAO) {
    fun saveAuditLog(auditLog: AuditLog): AuditLog {
        return auditLogDao.save(auditLog)
    }
}