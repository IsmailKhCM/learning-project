package org.example.userdemo.user.repositories

import jakarta.persistence.criteria.CriteriaQuery
import org.example.userdemo.dao.AbstractDao
import org.example.userdemo.user.enums.UserStatus
import org.example.userdemo.user.model.User
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Repository
import java.time.Instant
import org.example.userdemo.dao.SortDirection

@Repository
class UserDao : AbstractDao<User, Long>(User::class) {

    override fun findAll(spec: Specification<User>, pageNumber: Int, pageSize: Int, sortBy: String, sortDirection: SortDirection): List<User> {
        val cb = entityManager.criteriaBuilder
        val cq: CriteriaQuery<User> = cb.createQuery(User::class.java)
        val root = cq.from(User::class.java)

        val predicate = spec.toPredicate(root, cq, cb)
        cq.where(predicate)

        // Add sorting
        val order = when (sortDirection) {
            SortDirection.ASC -> cb.asc(root.get<Any>(sortBy))
            SortDirection.DESC -> cb.desc(root.get<Any>(sortBy))
        }
        cq.orderBy(order)

        val query = entityManager.createQuery(cq)
        query.firstResult = (pageNumber - 1) * pageSize
        query.maxResults = pageSize

        return query.resultList
    }

    fun count(spec: Specification<User>): Long {
        val cb = entityManager.criteriaBuilder
        val cq: CriteriaQuery<Long> = cb.createQuery(Long::class.java)
        val root = cq.from(User::class.java)
        cq.select(cb.count(root))

        val predicate = spec.toPredicate(root, cq, cb)
        cq.where(predicate)

        return entityManager.createQuery(cq).singleResult
    }

    fun findPendingUsersOlderThan(dateTimeLimit: Instant): List<User> {
        val cb = entityManager.criteriaBuilder
        val cq = cb.createQuery(User::class.java)
        val root = cq.from(User::class.java)

        val datePredicate = cb.lessThan(root.get("creationTime"), dateTimeLimit)
        val statusPredicate = cb.equal(root.get<String>("status"), UserStatus.PENDING)

        cq.where(cb.and(datePredicate, statusPredicate))

        return entityManager.createQuery(cq).resultList
    }
}