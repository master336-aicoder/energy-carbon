
USE energy_carbon;

DELIMITER $$
DROP PROCEDURE IF EXISTS AddColIfNotExists$$
CREATE PROCEDURE AddColIfNotExists(IN tableName VARCHAR(64), IN colName VARCHAR(64), IN colDef VARCHAR(255))
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = tableName
          AND column_name = colName
    ) THEN
        SET @sql = CONCAT('ALTER TABLE ', tableName, ' ADD COLUMN ', colName, ' ', colDef);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$
DELIMITER ;

CALL AddColIfNotExists('sys_log', 'browser', "VARCHAR(100) DEFAULT NULL COMMENT '浏览器信息'");
CALL AddColIfNotExists('sys_log', 'os', "VARCHAR(100) DEFAULT NULL COMMENT '操作系统信息'");
CALL AddColIfNotExists('sys_log', 'referer', "VARCHAR(500) DEFAULT NULL COMMENT '来源页面'");
CALL AddColIfNotExists('sys_log', 'user_agent', "VARCHAR(500) DEFAULT NULL COMMENT '用户代理'");

DROP PROCEDURE IF EXISTS AddColIfNotExists;
