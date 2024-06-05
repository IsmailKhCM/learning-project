package org.example.userdemo.auditlog.model

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "audit_log")
data class AuditLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "entity_name", nullable = false)
    val entityName: String,

    @Column(name = "entity_id", nullable = false)
    val entityId: Long,

    @Column(name = "field_name", nullable = false)
    val fieldName: String,

    @Column(name = "old_value", nullable = false)
    val oldValue: String?,

    @Column(name = "new_value", nullable = false)
    val newValue: String?,

    @CreationTimestamp
    @Column(name = "timestamp", nullable = false)
    val timestamp: LocalDateTime = LocalDateTime.now()
)
