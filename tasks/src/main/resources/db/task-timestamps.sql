-- Apply manually when ddl-auto is not enabled
ALTER TABLE tasks ADD COLUMN created_at TIMESTAMP(6) NULL;
ALTER TABLE tasks ADD COLUMN in_progress_at TIMESTAMP(6) NULL;
ALTER TABLE tasks ADD COLUMN completed_at TIMESTAMP(6) NULL;
ALTER TABLE tasks ADD COLUMN cancelled_at TIMESTAMP(6) NULL;
