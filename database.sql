-- ============================================================================
-- UPSRTC DUTY MANAGEMENT PORTAL - DATABASE SCHEMA
-- Hostinger MySQL / phpMyAdmin Deployment Script
-- Compatible with MySQL 5.7+ / MariaDB 10.3+ / MySQL 8.0+
-- Charset: utf8mb4 / Collation: utf8mb4_unicode_ci
-- ============================================================================

-- 1. Create Database (Optional: Uncomment if creating a new database locally)
-- CREATE DATABASE IF NOT EXISTS `upsrtc_portal` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- USE `upsrtc_portal`;

SET FOREIGN_KEY_CHECKS = 0;
SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+05:30"; -- Indian Standard Time (IST)

-- ============================================================================
-- Table: users (Drivers, Conductors, Clerks, and Depot Supervisors)
-- ============================================================================
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` VARCHAR(64) NOT NULL,
  `employee_id` VARCHAR(50) NOT NULL,
  `full_name` VARCHAR(100) NOT NULL,
  `designation` VARCHAR(50) DEFAULT 'Conductor',
  `user_type` VARCHAR(50) DEFAULT 'Conductor',
  `depot` VARCHAR(100) DEFAULT 'Meerut Depot',
  `depot_code` VARCHAR(30) DEFAULT 'MRT-01',
  `region` VARCHAR(50) DEFAULT 'Meerut',
  `mobile` VARCHAR(20) DEFAULT '',
  `email` VARCHAR(100) DEFAULT '',
  `password_hash` VARCHAR(255) DEFAULT '',
  `token_no` VARCHAR(50) DEFAULT '',
  `photo_url` LONGTEXT DEFAULT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_unique_empid` (`employee_id`),
  KEY `idx_depot` (`depot`),
  KEY `idx_mobile` (`mobile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- Table: duties (Daily Bus Route Duty Log, KM, Load Factor, and Revenue)
-- ============================================================================
DROP TABLE IF EXISTS `duties`;
CREATE TABLE `duties` (
  `id` VARCHAR(64) NOT NULL,
  `employee_id` VARCHAR(50) NOT NULL,
  `user_id` VARCHAR(64) DEFAULT NULL,
  `duty_date` DATE NOT NULL,
  `duty_number` VARCHAR(30) DEFAULT '01',
  `shift` VARCHAR(30) DEFAULT 'Morning',
  `bus_number` VARCHAR(30) NOT NULL,
  `route_from` VARCHAR(100) NOT NULL,
  `route_to` VARCHAR(100) NOT NULL,
  `departure_time` VARCHAR(10) DEFAULT '',
  `arrival_time` VARCHAR(10) DEFAULT '',
  `opening_km` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  `closing_km` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  `total_km` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  `passenger_count` INT NOT NULL DEFAULT 0,
  `total_seats` INT NOT NULL DEFAULT 52,
  `load_factor` DECIMAL(5,2) NOT NULL DEFAULT 0.00,
  `income` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  `expenses` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  `remarks` TEXT DEFAULT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_empid_date` (`employee_id`, `duty_date`),
  KEY `idx_duty_date` (`duty_date`),
  KEY `idx_bus_num` (`bus_number`),
  KEY `idx_route` (`route_from`, `route_to`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- Table: portal_settings (External UPSRTC department links & portal config)
-- ============================================================================
DROP TABLE IF EXISTS `portal_settings`;
CREATE TABLE `portal_settings` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `setting_key` VARCHAR(50) NOT NULL UNIQUE,
  `setting_value` TEXT NOT NULL,
  `description` VARCHAR(255) DEFAULT '',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- Seed Initial Demo Data: Conductor Profile
-- ============================================================================
INSERT INTO `users` (`id`, `employee_id`, `full_name`, `designation`, `user_type`, `depot`, `depot_code`, `region`, `mobile`, `email`, `token_no`)
VALUES (
  'u1',
  'CND 1901',
  'Kishor Kumar',
  'Senior Conductor',
  'Conductor',
  'Meerut Depot',
  'MRT-01',
  'Meerut Region',
  '9876543210',
  'kishor2013@gmail.com',
  'TKN-7821'
) ON DUPLICATE KEY UPDATE `full_name` = VALUES(`full_name`);

-- ============================================================================
-- Seed Initial Demo Data: Default Duties
-- ============================================================================
INSERT INTO `duties` (
  `id`, `employee_id`, `user_id`, `duty_date`, `duty_number`, `shift`, `bus_number`,
  `route_from`, `route_to`, `departure_time`, `arrival_time`, `opening_km`, `closing_km`,
  `total_km`, `passenger_count`, `total_seats`, `load_factor`, `income`, `expenses`, `remarks`
) VALUES
('d1', 'CND 1901', 'u1', CURDATE() - INTERVAL 16 DAY, '01', 'Morning', 'UP15-AT-4210', 'MEERUT', 'DELHI ISBT', '06:30', '11:45', 12450.00, 12680.00, 230.00, 48, 52, 92.30, 7850.00, 0.00, 'On-time schedule clearance'),
('d2', 'CND 1901', 'u1', CURDATE() - INTERVAL 12 DAY, '02', 'Morning', 'UP15-AT-4210', 'MEERUT', 'AGRA ISBT', '05:45', '12:30', 12680.00, 12970.00, 290.00, 50, 52, 96.20, 9450.00, 0.00, 'Express trip, full seating capacity'),
('d3', 'CND 1901', 'u1', CURDATE() - INTERVAL 8 DAY, '01', 'Evening', 'UP15-AT-4210', 'MEERUT', 'BAREILLY', '14:00', '19:40', 12970.00, 13220.00, 250.00, 46, 52, 88.50, 8200.00, 0.00, 'Smooth Highway transit'),
('d4', 'CND 1901', 'u1', CURDATE() - INTERVAL 4 DAY, '01', 'Morning', 'UP15-AT-4210', 'MEERUT', 'HARIDWAR', '06:00', '11:15', 13220.00, 13430.00, 210.00, 44, 52, 84.60, 7150.00, 0.00, 'Pilgrim route run'),
('d5', 'CND 1901', 'u1', CURDATE() - INTERVAL 1 DAY, '03', 'Double', 'UP15-AT-4210', 'MEERUT', 'LUCKNOW', '07:30', '16:00', 13430.00, 13780.00, 350.00, 52, 52, 100.00, 11200.00, 0.00, 'Peak festive high revenue double shift'),
('d6', 'CND 1901', 'u1', CURDATE(), '01', 'Morning', 'UP15-AT-4210', 'MEERUT', 'DELHI ISBT', '06:30', '11:45', 13780.00, 14010.00, 230.00, 49, 52, 94.20, 8850.00, 0.00, 'Today duty run completed successfully')
ON DUPLICATE KEY UPDATE `income` = VALUES(`income`);

-- ============================================================================
-- Seed Portal URLs
-- ============================================================================
INSERT INTO `portal_settings` (`setting_key`, `setting_value`, `description`) VALUES
('epfo', 'https://unifiedportal-mem.epfindia.gov.in/memberinterface/', 'EPFO Member Portal for PF Passbook & Claims'),
('attendance', 'https://upsrtc.up.gov.in/', 'UPSRTC Duty Attendance System'),
('duty_list', 'https://upsrtc.up.gov.in/', 'Online Monthly Duty Roster'),
('salary_slip', 'https://upsrtc.up.gov.in/', 'Official Monthly Salary & Incentive Slips')
ON DUPLICATE KEY UPDATE `setting_value` = VALUES(`setting_value`);

SET FOREIGN_KEY_CHECKS = 1;
COMMIT;
