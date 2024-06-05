package org.example.userdemo.user.model

import jakarta.persistence.*
import org.example.userdemo.auditlog.annotations.Audit
import org.example.userdemo.user.enums.UserStatus
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant


@Entity
@Table(name = "users")
data class User(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long = 0,

  @Column(name = "first_name", nullable = false, length = 50)
  val firstName: String,

  @Column(name = "last_name", nullable = false, length = 50)
  val lastName: String,

  @Column(name = "email", nullable = false, unique = true, length = 100)
  val email: String,

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(32) default 'PENDING'")
  @Audit(fieldName = "status", entityName = "User")
  var status: UserStatus = UserStatus.PENDING,

  @Column(name = "balance", nullable = false)
  val balance: Long = 0,

  @CreationTimestamp
  @Column(name = "creation_time", nullable = false)
  val creationTime: Instant = Instant.now()
)
