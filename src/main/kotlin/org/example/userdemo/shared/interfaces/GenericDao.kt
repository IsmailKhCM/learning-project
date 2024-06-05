package org.example.userdemo.shared.interfaces

import java.io.Serializable

interface GenericDao<T, ID : Serializable> {
  fun save(entity: T): T
  fun findById(id: ID): T?
  fun update(entity: T): T
  fun delete(entity: T)
}
