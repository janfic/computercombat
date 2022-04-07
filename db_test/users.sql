CREATE USER 'client'@'%' IDENTIFIED BY 'computercombat2021';
GRANT SELECT, DELETE, INSERT, UPDATE ON computer_combat.* TO 'client'@'%';
SET GLOBAL sql_mode=(SELECT REPLACE(@@sql_mode,'ONLY_FULL_GROUP_BY',''));