-- Add the entity_id column to the audit_log table
ALTER TABLE audit_log
ADD COLUMN entity_id BIGINT NOT NULL;