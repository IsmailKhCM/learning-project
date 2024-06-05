package org.example.userdemo.auditlog.repositories

import org.example.userdemo.auditlog.model.AuditLog

import org.springframework.data.jpa.repository.JpaRepository

import org.springframework.stereotype.Repository

@Repository
interface AuditLogDAO : JpaRepository<AuditLog, Long>