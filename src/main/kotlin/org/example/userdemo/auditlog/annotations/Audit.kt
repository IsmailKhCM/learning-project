package org.example.userdemo.auditlog.annotations

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Audit(
        val fieldName: String = "",
        val entityName: String = ""
)