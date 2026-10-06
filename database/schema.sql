-- MySQL dump 10.13  Distrib 8.4.11, for macos15 (arm64)
--
-- Host: localhost    Database: proteahealth
-- ------------------------------------------------------
-- Server version	8.4.11

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Admins`
--

DROP TABLE IF EXISTS `Admins`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Admins` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `surname` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Doctor`
--

DROP TABLE IF EXISTS `Doctor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Doctor` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `surname` varchar(100) NOT NULL,
  `email` varchar(150) DEFAULT NULL,
  `password_hash` varchar(255) DEFAULT NULL,
  `specialization` varchar(150) NOT NULL,
  `location` varchar(255) NOT NULL,
  `clinic_name` varchar(200) NOT NULL,
  `years_in_professional` int NOT NULL,
  `consultation_price` decimal(10,2) NOT NULL,
  `certification` varchar(255) NOT NULL,
  `verification` enum('pending','verified','rejected') NOT NULL DEFAULT 'pending',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  CONSTRAINT `chk_doctor_price` CHECK ((`consultation_price` >= 0)),
  CONSTRAINT `chk_doctor_years` CHECK ((`years_in_professional` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Education_Category`
--

DROP TABLE IF EXISTS `Education_Category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Education_Category` (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(150) NOT NULL,
  `patient_id` int DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `uq_category_patient_name` (`patient_id`,`name`),
  KEY `idx_category_patient_id` (`patient_id`),
  CONSTRAINT `fk_category_patient` FOREIGN KEY (`patient_id`) REFERENCES `Patient` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Educational_Resource`
--

DROP TABLE IF EXISTS `Educational_Resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Educational_Resource` (
  `resource_id` int NOT NULL AUTO_INCREMENT,
  `category_id` int NOT NULL,
  `provider_id` int NOT NULL,
  `title` varchar(255) NOT NULL,
  `summary` text NOT NULL,
  `content` longtext NOT NULL,
  `read_time` int NOT NULL,
  `status` enum('draft','published','archived') NOT NULL DEFAULT 'draft',
  `is_reviewed` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`resource_id`),
  KEY `idx_resource_category_id` (`category_id`),
  KEY `idx_resource_provider_id` (`provider_id`),
  CONSTRAINT `fk_resource_category` FOREIGN KEY (`category_id`) REFERENCES `Education_Category` (`category_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_resource_doctor` FOREIGN KEY (`provider_id`) REFERENCES `Doctor` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `chk_resource_read_time` CHECK ((`read_time` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Orders`
--

DROP TABLE IF EXISTS `Orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Orders` (
  `id` int NOT NULL AUTO_INCREMENT,
  `patient_id` int NOT NULL,
  `pharmacy_id` int NOT NULL,
  `medication_name` varchar(200) NOT NULL,
  `quantity` int NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `fulfillment_method` enum('collection','delivery') NOT NULL DEFAULT 'collection',
  `delivery_address` varchar(500) DEFAULT NULL,
  `delivery_fee` decimal(10,2) NOT NULL DEFAULT '0.00',
  `order_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` enum('pending','confirmed','processing','shipped','delivered','cancelled') NOT NULL DEFAULT 'pending',
  PRIMARY KEY (`id`),
  KEY `idx_order_patient_id` (`patient_id`),
  KEY `idx_order_pharmacy_id` (`pharmacy_id`),
  CONSTRAINT `fk_order_patient` FOREIGN KEY (`patient_id`) REFERENCES `Patient` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_order_pharmacy` FOREIGN KEY (`pharmacy_id`) REFERENCES `Pharmacies` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `chk_order_amount` CHECK ((`amount` >= 0)),
  CONSTRAINT `chk_order_quantity` CHECK ((`quantity` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Patient`
--

DROP TABLE IF EXISTS `Patient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Patient` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `surname` varchar(100) NOT NULL,
  `age` int NOT NULL,
  `email` varchar(150) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `phone` varchar(30) NOT NULL,
  `gender` enum('male','female','other','prefer_not_to_say') NOT NULL,
  `home_address` varchar(500) NOT NULL,
  `emergency_contact_name` varchar(150) DEFAULT NULL,
  `emergency_contact_number` varchar(30) DEFAULT NULL,
  `emergency_contact_relationship` varchar(100) DEFAULT NULL,
  `allergies` text,
  `medical_conditions` text,
  `risk_level` enum('low','medium','high','critical') NOT NULL DEFAULT 'low',
  `doc_id` int DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_patient_doc_id` (`doc_id`),
  CONSTRAINT `fk_patient_doctor` FOREIGN KEY (`doc_id`) REFERENCES `Doctor` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `chk_patient_age` CHECK (((`age` > 0) and (`age` <= 150)))
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Patient_Question`
--

DROP TABLE IF EXISTS `Patient_Question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Patient_Question` (
  `question_id` int NOT NULL AUTO_INCREMENT,
  `patient_id` int NOT NULL,
  `category_id` int NOT NULL,
  `question_text` text NOT NULL,
  `is_anonymous` tinyint(1) NOT NULL DEFAULT '0',
  `status` enum('open','answered','closed') NOT NULL DEFAULT 'open',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`question_id`),
  KEY `idx_question_patient_id` (`patient_id`),
  KEY `idx_question_category_id` (`category_id`),
  CONSTRAINT `fk_question_category` FOREIGN KEY (`category_id`) REFERENCES `Education_Category` (`category_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_question_patient` FOREIGN KEY (`patient_id`) REFERENCES `Patient` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Pharmacies`
--

DROP TABLE IF EXISTS `Pharmacies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Pharmacies` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  `email` varchar(150) DEFAULT NULL,
  `password_hash` varchar(255) DEFAULT NULL,
  `location` varchar(255) NOT NULL,
  `open_hour` time NOT NULL,
  `close_hours` time NOT NULL,
  `details` text,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `offers_delivery` tinyint(1) NOT NULL DEFAULT '0',
  `delivery_fee` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `chk_pharmacy_hours` CHECK ((`open_hour` <> `close_hours`))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Pharmacist`
--

DROP TABLE IF EXISTS `Pharmacist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Pharmacist` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `surname` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `password_hash` varchar(255) DEFAULT NULL,
  `pharmacy_id` int DEFAULT NULL,
  `phone` varchar(30) NOT NULL,
  `pharmacy_location` varchar(255) NOT NULL,
  `verification` enum('pending','verified','rejected') NOT NULL DEFAULT 'pending',
  `registration_code` varchar(100) NOT NULL,
  `work_experience_years` int NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  KEY `fk_pharmacist_pharmacy` (`pharmacy_id`),
  CONSTRAINT `fk_pharmacist_pharmacy` FOREIGN KEY (`pharmacy_id`) REFERENCES `Pharmacies` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `chk_pharmacist_experience` CHECK ((`work_experience_years` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Pharmacy_Medication`
--

DROP TABLE IF EXISTS `Pharmacy_Medication`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Pharmacy_Medication` (
  `id` int NOT NULL AUTO_INCREMENT,
  `pharmacy_id` int NOT NULL,
  `medication_name` varchar(200) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `availability` enum('available','unavailable') NOT NULL DEFAULT 'available',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_medication_pharmacy_id` (`pharmacy_id`),
  CONSTRAINT `fk_medication_pharmacy` FOREIGN KEY (`pharmacy_id`) REFERENCES `Pharmacies` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_medication_price` CHECK ((`price` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Prescriptions`
--

DROP TABLE IF EXISTS `Prescriptions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Prescriptions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `patient_id` int NOT NULL,
  `doctor_id` int NOT NULL,
  `medication_id` int NOT NULL,
  `quantity` int NOT NULL,
  `dosage` varchar(100) DEFAULT NULL,
  `purpose` varchar(255) DEFAULT NULL,
  `scheduled_time` time DEFAULT NULL,
  `remaining_doses` int NOT NULL DEFAULT '0',
  `last_taken_date` date DEFAULT NULL,
  `date_prescription` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_prescription_patient_id` (`patient_id`),
  KEY `idx_prescription_doctor_id` (`doctor_id`),
  KEY `idx_prescription_medication_id` (`medication_id`),
  CONSTRAINT `fk_prescription_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `Doctor` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_prescription_medication` FOREIGN KEY (`medication_id`) REFERENCES `Pharmacy_Medication` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_prescription_patient` FOREIGN KEY (`patient_id`) REFERENCES `Patient` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_prescription_quantity` CHECK ((`quantity` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Question_Answer`
--

DROP TABLE IF EXISTS `Question_Answer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Question_Answer` (
  `answer_id` int NOT NULL AUTO_INCREMENT,
  `question_id` int NOT NULL,
  `provider_id` int NOT NULL,
  `answer_text` text NOT NULL,
  `answered_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`answer_id`),
  KEY `idx_answer_question_id` (`question_id`),
  KEY `idx_answer_provider_id` (`provider_id`),
  CONSTRAINT `fk_answer_doctor` FOREIGN KEY (`provider_id`) REFERENCES `Doctor` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_answer_question` FOREIGN KEY (`question_id`) REFERENCES `Patient_Question` (`question_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Resource_View`
--

DROP TABLE IF EXISTS `Resource_View`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Resource_View` (
  `view_id` int NOT NULL AUTO_INCREMENT,
  `patient_id` int NOT NULL,
  `resource_id` int NOT NULL,
  `viewed_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`view_id`),
  UNIQUE KEY `uq_patient_resource_view` (`patient_id`,`resource_id`),
  KEY `idx_view_patient_id` (`patient_id`),
  KEY `idx_view_resource_id` (`resource_id`),
  CONSTRAINT `fk_view_patient` FOREIGN KEY (`patient_id`) REFERENCES `Patient` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_view_resource` FOREIGN KEY (`resource_id`) REFERENCES `Educational_Resource` (`resource_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-06 22:01:14
