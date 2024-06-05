//package org.example.userdemo.shared.listeners
//
//import jakarta.persistence.*
//import org.example.userdemo.auditlog.annotations.Audit
//import org.example.userdemo.auditlog.model.AuditLog
//
//import org.example.userdemo.shared.service.AuditLogService
//import org.hibernate.Session
//import org.hibernate.engine.spi.EntityEntry
//import org.hibernate.engine.spi.SharedSessionContractImplementor
//import org.hibernate.persister.entity.EntityPersister
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.stereotype.Component
//import org.slf4j.LoggerFactory
//import kotlin.reflect.full.findAnnotation
//import kotlin.reflect.full.memberProperties
//
//@Component
//class AuditEntityListener {
//
//    @PersistenceContext
//    private lateinit var entityManager: EntityManager
//
//    @Autowired
//    private lateinit var auditLogService: AuditLogService
//
//    private val logger = LoggerFactory.getLogger(AuditEntityListener::class.java)
//
//    @PreUpdate
//    fun onPreUpdate(entity: Any) {
//        val entityClass = entity::class
//        val entityName = entityClass.simpleName ?: ""
//
//        val session = entityManager.unwrap(Session::class.java)
//        val entityEntry = getEntityEntry(session, entity)
//
//        if (entityEntry != null) {
//            val oldState = entityEntry.loadedState as Array<Any?>?
//            val persister = entityEntry.persister
//
//            entityClass.memberProperties.forEach { prop ->
//                try {
//                    val auditAnnotation = prop.findAnnotation<Audit>()
//                    if (auditAnnotation != null && oldState != null) {
//                        val fieldName = auditAnnotation.fieldName.ifEmpty { prop.name }
//                        val oldValue = getOldValue(oldState, persister, fieldName)?.toString()
//                        val newValue = prop.get(entity)?.toString()
//
//                        if (oldValue != newValue) {
//                            val auditLog = AuditLog(
//                                entityName = entityName,
//                                fieldName = fieldName,
//                                oldValue = oldValue,
//                                newValue = newValue
//                            )
//                            auditLogService.saveAuditLog(auditLog)  // Save each audit log as it's created
//                        }
//                    }
//                } catch (e: Exception) {
//                    logger.error("Error auditing field ${prop.name} of entity $entityName", e)
//                }
//            }
//        }
//    }
//
//
//
//    private fun getOldValue(oldState: Array<Any?>, entityPersister: EntityPersister, fieldName: String): Any? {
//        val propertyIndex = entityPersister.getPropertyNames().indexOf(fieldName)
//        return if (propertyIndex != -1 && propertyIndex < oldState.size) oldState[propertyIndex] else null
//    }
//}