package org.example.userdemo.dao

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Order
import org.example.userdemo.auditlog.annotations.Audit
import org.example.userdemo.auditlog.model.AuditLog
import org.example.userdemo.shared.interfaces.GenericDao
import org.example.userdemo.shared.service.AuditLogService
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Repository
import java.io.Serializable
import kotlin.reflect.KClass
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

enum class SortDirection {
  ASC, DESC
}

@Repository
abstract class AbstractDao<T : Any, ID : Serializable>(
  private val entityClass: KClass<T>
) : GenericDao<T, ID> {

  @PersistenceContext
  protected lateinit var entityManager: EntityManager

  private lateinit var auditLogService: AuditLogService

  @Autowired
  fun setAuditLogService(auditLogService: AuditLogService) {
    this.auditLogService = auditLogService
  }

  private val logger: Logger = LoggerFactory.getLogger(this::class.java)



  private fun getId(entity: T): ID {
    val idProperty = entity::class.memberProperties.firstOrNull { it.name == "id" }
    return idProperty?.call(entity) as ID
  }

  private fun getOldValue(entity: T, propertyName: String): Any? {
    val property = entity::class.memberProperties.firstOrNull { it.name == propertyName }
    return property?.call(entity)
  }

  override fun save(entity: T): T {
    logger.info("Saving $entity")
    preSave(entity)
    return if (entityManager.contains(entity)) {
      entityManager.merge(entity)
    } else {
      if (getId(entity) == null) {
        entityManager.persist(entity)
        entity
      } else {
        entityManager.merge(entity)
      }
    }
  }

  protected fun preSave(entity: T) {
    val entityClass = entity::class
    val entityName = entityClass.simpleName ?: ""

    entityManager.detach(entity)

    val oldEntity = entityManager.find(entityClass.java, getId(entity))

    entityClass.memberProperties.forEach { prop ->
      val auditAnnotation = prop.findAnnotation<Audit>()
      if (auditAnnotation != null && oldEntity != null) {
        val fieldName = auditAnnotation.fieldName.ifEmpty { prop.name }
        val oldValue = oldEntity?.let { getOldValue(it, prop.name) }?.toString()
        val newValue = prop.call(entity)?.toString() // Corrected to call with entity

        if (oldValue != newValue) {
          val auditLog = AuditLog(
            entityName = auditAnnotation.entityName.ifEmpty { entityName },
            entityId = getId(entity) as Long,
            fieldName = fieldName,
            oldValue = oldValue,
            newValue = newValue
          )
          auditLogService.saveAuditLog(auditLog)
          logger.info("Audit log created for $entityName field $fieldName")
        }
      }
    }
  }

  override fun findById(id: ID): T? = entityManager.find(entityClass.java, id)

  fun findAll(spec: Specification<T>, pageNumber: Int, pageSize: Int, sortBy: String, sortDirection: SortDirection): List<T> {
    val cb = entityManager.criteriaBuilder
    val cq: CriteriaQuery<T> = cb.createQuery(entityClass.java)
    val root = cq.from(entityClass.java)

    val predicate = spec.toPredicate(root, cq, cb)
    cq.where(predicate)

    // Add sorting
    val order: Order = when (sortDirection) {
      SortDirection.ASC -> cb.asc(root.get<Any>(sortBy))
      SortDirection.DESC -> cb.desc(root.get<Any>(sortBy))
    }

    cq.orderBy(order)

    val query = entityManager.createQuery(cq)
    query.firstResult = (pageNumber - 1) * pageSize
    query.maxResults = pageSize

    return query.resultList
  }

  override fun update(entity: T): T {
    if (!entityManager.contains(entity)) {
      return entityManager.merge(entity)
    }
    entityManager.flush()
    return entity
  }

  override fun delete(entity: T) {
    entityManager.remove(if (entityManager.contains(entity)) entity else entityManager.merge(entity))
  }
}