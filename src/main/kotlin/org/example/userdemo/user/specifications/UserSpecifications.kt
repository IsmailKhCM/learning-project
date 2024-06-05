package org.example.userdemo.user.specifications

import jakarta.persistence.criteria.*
import org.springframework.data.jpa.domain.Specification
import org.example.userdemo.user.model.User

class UserSpecifications {

  companion object {
    private val ignoredParams = setOf("pageNumber", "pageSize", "sortBy", "sortDirection")

    fun withDynamicQuery(filterParams: Map<String, Any>): Specification<User> {
      return Specification { root, _, cb ->
        val predicates = mutableListOf<Predicate>()
        filterParams.filterNot { it.key in ignoredParams }.forEach { (key, value) ->
          val parts = key.split("_")
          if (parts.size == 2) {
            val attribute = parts[0]
            val operation = parts[1]
            predicates.add(createPredicate(cb, root, attribute, value, operation))
          } else {
            throw IllegalArgumentException("Unsupported key: $key")
          }
        }
        cb.and(*predicates.toTypedArray())
      }
    }

    private fun createPredicate(
      cb: CriteriaBuilder,
      root: Root<User>,
      attribute: String,
      value: Any,
      operation: String
    ): Predicate {
      return when (operation) {
        "gt", "lt", "gte", "lte" -> handleNumericComparisons(cb, root, attribute, value, operation)
        "eq", "neq" -> handleEqualityComparisons(cb, root, attribute, value, operation)
        "like" -> cb.like(root.get(attribute), "%$value%")
        else -> throw IllegalArgumentException("Unsupported operation: $operation")
      }
    }

    private fun handleNumericComparisons(
      cb: CriteriaBuilder,
      root: Root<User>,
      attribute: String,
      value: Any,
      operation: String
    ): Predicate {
      val path = root.get<Comparable<Any>>(attribute)
      val comparableValue = when (value) {
        is Number -> value
        is String -> value.toDouble()
        else -> throw IllegalArgumentException("Invalid type for numeric operation: $value")
      }
      return when (operation) {
        "gt" -> cb.greaterThan(path, comparableValue as Comparable<Any>)
        "lt" -> cb.lessThan(path, comparableValue as Comparable<Any>)
        "gte" -> cb.greaterThanOrEqualTo(path, comparableValue as Comparable<Any>)
        "lte" -> cb.lessThanOrEqualTo(path, comparableValue as Comparable<Any>)
        else -> throw IllegalArgumentException("Unsupported numeric operation: $operation")
      }
    }

    private fun handleEqualityComparisons(
      cb: CriteriaBuilder,
      root: Root<User>,
      attribute: String,
      value: Any,
      operation: String
    ): Predicate {
      val path = root.get<Any>(attribute)
      return when (operation) {
        "eq" -> cb.equal(path, value)
        "neq" -> cb.notEqual(path, value)
        else -> throw IllegalArgumentException("Unsupported equality operation: $operation")
      }
    }
  }
}
