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
-- Dumping data for table `Admins`
--

LOCK TABLES `Admins` WRITE;
/*!40000 ALTER TABLE `Admins` DISABLE KEYS */;
INSERT INTO `Admins` VALUES (1,'Lushen','Govender','LG26'),(2,'Nianca','Chetty','NC12'),(3,'Vusiwe','Bengu','VB23'),(4,'Mahek','Lala','ML34'),(5,'Wengal','Weldekidan','WW45');
/*!40000 ALTER TABLE `Admins` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `Doctor`
--

LOCK TABLES `Doctor` WRITE;
/*!40000 ALTER TABLE `Doctor` DISABLE KEYS */;
INSERT INTO `Doctor` VALUES (1,'Sarah','Naidoo','doctor1@proteahealth.local','1234','General Practitioner','Johannesburg','HealthCare Medical Centre',8,650.00,'MBChB, HPCSA','verified','2026-09-30 12:27:33'),(2,'Michael','Dlamini','doctor2@proteahealth.local',NULL,'Cardiologist','Pretoria','HeartCare Clinic',14,1200.00,'MBChB, FCP(SA), HPCSA','verified','2026-09-30 12:27:33'),(3,'Aisha','Khan','doctor3@proteahealth.local',NULL,'Dermatologist','Sandton','Skin Health Clinic',6,900.00,'MBChB, Dip Dermatology, HPCSA','pending','2026-09-30 12:27:33'),(4,'James','Smith','james.smith@example.com','$2y$12$VqaDefKBzp6.vNTt530znuEHuIp6DxF8xYsLTSR7zrZGoU4z9yIOq','General Practitioner','Johannesburg','Protea Health Clinic',8,450.00,'MBChB','pending','2026-09-30 13:03:22'),(5,'tes','rrr','rgsrg@ee.com','$2y$12$kiF64Hw5FQ.dE9BG7ZNlx.mno/iHcITEsNe2KfWiekuPrWY.I9c2u','sss','rrrr','sss',422,42.00,'rrr','verified','2026-09-30 15:06:06'),(6,'bruno','fernandes','bruno@mun.com','$2y$12$mWrNBTAu0XuLHS6OuXiOLeGPSWT1piVtRDaqZLiN1jpCzeANJdvI.','passing','Manchester','Old Trafford',15,320000.00,'PFA25/26','verified','2026-10-02 05:12:16');
/*!40000 ALTER TABLE `Doctor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `Education_Category`
--

LOCK TABLES `Education_Category` WRITE;
/*!40000 ALTER TABLE `Education_Category` DISABLE KEYS */;
INSERT INTO `Education_Category` VALUES (1,'Heart Health',1,'2026-09-30 12:27:33'),(2,'Medication Safety',2,'2026-09-30 12:27:33'),(3,'Skin Health',3,'2026-09-30 12:27:33');
/*!40000 ALTER TABLE `Education_Category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `Educational_Resource`
--

LOCK TABLES `Educational_Resource` WRITE;
/*!40000 ALTER TABLE `Educational_Resource` DISABLE KEYS */;
INSERT INTO `Educational_Resource` VALUES (1,1,2,'Understanding Heart Health','An introduction to maintaining cardiovascular health.','Heart health can be supported through regular physical activity, balanced nutrition, appropriate medical screening and management of risk factors.',5,'published',1,'2026-09-30 12:27:33','2026-09-30 12:27:33'),(2,2,1,'Medication Safety Basics','Important information about taking medication safely.','Patients should follow the instructions provided by their healthcare professional and pharmacist and should not change prescribed doses without professional advice.',4,'published',1,'2026-09-30 12:27:33','2026-09-30 12:27:33'),(3,3,3,'Basic Skin Health','General information about maintaining healthy skin.','Healthy skin is supported by appropriate cleansing, sun protection and seeking professional advice when persistent or concerning skin changes occur.',3,'draft',0,'2026-09-30 12:27:33','2026-09-30 12:27:33');
/*!40000 ALTER TABLE `Educational_Resource` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `Orders`
--

LOCK TABLES `Orders` WRITE;
/*!40000 ALTER TABLE `Orders` DISABLE KEYS */;
INSERT INTO `Orders` VALUES (1,1,1,'Paracetamol 500mg',20,919.80,'collection',NULL,0.00,'2026-09-30 12:27:33','pending'),(2,2,2,'Amoxicillin 500mg',21,1889.79,'collection',NULL,0.00,'2026-09-30 12:27:33','confirmed'),(3,3,3,'Cetirizine 10mg',10,655.00,'collection',NULL,0.00,'2026-09-30 12:27:33','delivered'),(4,6,2,'Paracetamol 500mg',30,1199.70,'collection',NULL,0.00,'2026-10-06 09:03:56','confirmed'),(5,6,1,'Paracetamol 500mg',30,1414.70,'delivery','123rfrf',35.00,'2026-10-06 13:52:32','confirmed');
/*!40000 ALTER TABLE `Orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `Patient`
--

LOCK TABLES `Patient` WRITE;
/*!40000 ALTER TABLE `Patient` DISABLE KEYS */;
INSERT INTO `Patient` VALUES (1,'John','Mthembu',29,'john.mthembu@example.com','','0825551001','male','Soweto, Johannesburg','Mary Mthembu - 0825552001',NULL,NULL,'Penicillin',NULL,'medium',1,'2026-09-30 12:27:33'),(2,'Lerato','Molefe',41,'lerato.molefe@example.com','','0825551002','female','Pretoria, Gauteng','Thabo Molefe - 0825552002',NULL,NULL,'None known',NULL,'low',2,'2026-09-30 12:27:33'),(3,'Adam','Pillay',35,'adam.pillay@example.com','','0825551003','male','Sandton, Johannesburg','Nadia Pillay - 0825552003',NULL,NULL,'Peanuts',NULL,'high',3,'2026-09-30 12:27:33'),(4,'Test','Patient',25,'test.patient@example.com','$2y$12$FT05GQXrKm19dJY5P3fGCe0jc6z6mW/Eub1GXDVjxXOPH5cNjJXae','0821234567','other','Johannesburg',NULL,NULL,NULL,NULL,NULL,'low',NULL,'2026-09-30 13:00:26'),(5,'james','la',45,'join@why.com','$2y$12$YimtDtGx942NyqQu2nuqLuENR8ECR1Owup54O8W.dQlgOWzRAcFWe','0651258963','male','grjj','james',NULL,NULL,'',NULL,'low',NULL,'2026-09-30 14:37:45'),(6,'hope','please',99,'work@please.com','$2y$12$4.FCeZd6w74acg.hVnU6aORmkh1aBTZs.qgOwfcpVvR2N2/ynloBe','0977896896666','other','123 hope street','hope','073454254','talking stage','Android Studio','No sleepious','low',NULL,'2026-10-01 21:51:37'),(7,'helo','hi',34,'de@ff.com','$2y$12$d0h5.dw3i5xpqFhz4.THFOi6BCgdn3maFlZMDV/U1H.BqATnPNVne','452545442','other','wrf','hi','56356','f','non',NULL,'low',NULL,'2026-10-02 00:41:19');
/*!40000 ALTER TABLE `Patient` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `Patient_Question`
--

LOCK TABLES `Patient_Question` WRITE;
/*!40000 ALTER TABLE `Patient_Question` DISABLE KEYS */;
INSERT INTO `Patient_Question` VALUES (1,1,1,'What are some general ways I can improve my heart health?',0,'answered','2026-09-30 12:27:33'),(2,2,2,'How should I keep track of my medication schedule?',1,'answered','2026-09-30 12:27:33'),(3,3,3,'When should I speak to a doctor about a persistent skin problem?',0,'open','2026-09-30 12:27:33');
/*!40000 ALTER TABLE `Patient_Question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `Pharmacies`
--

LOCK TABLES `Pharmacies` WRITE;
/*!40000 ALTER TABLE `Pharmacies` DISABLE KEYS */;
INSERT INTO `Pharmacies` VALUES (1,'HealthPlus Pharmacy','pharmacy1@proteahealth.local',NULL,'Johannesburg','08:00:00','18:00:00','Community pharmacy offering prescription and over-the-counter medication.','2026-09-30 12:27:33',1,35.00),(2,'MedCare Pharmacy','pharmacy2@proteahealth.local',NULL,'Sandton','08:00:00','20:00:00','Full-service pharmacy with medication delivery.','2026-09-30 12:27:33',0,NULL),(3,'Wellness Pharmacy','pharmacy3@proteahealth.local',NULL,'Pretoria','09:00:00','19:00:00','Pharmacy specialising in chronic medication and wellness products.','2026-09-30 12:27:33',0,NULL),(4,'Protea Pharmacy','protea.pharmacy@example.com','$2y$12$YYPbOgeLCLFCRjP6vD13Xe7fMgyvQCKZ0vwZJJcCtP95udO2Wh53q','Protea Glen','08:00:00','18:00:00','Community pharmacy','2026-09-30 13:04:19',1,50.00);
/*!40000 ALTER TABLE `Pharmacies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `Pharmacist`
--

LOCK TABLES `Pharmacist` WRITE;
/*!40000 ALTER TABLE `Pharmacist` DISABLE KEYS */;
INSERT INTO `Pharmacist` VALUES (1,'James','Mokoena','james.mokoena@example.com',NULL,NULL,'0115551001','Johannesburg','pending','',7,'2026-09-30 12:27:33'),(2,'Priya','Patel','priya.patel@example.com',NULL,NULL,'0115551002','Sandton','pending','',5,'2026-09-30 12:27:33'),(3,'Daniel','Smith','daniel.smith@example.com',NULL,NULL,'0115551003','Pretoria','pending','',11,'2026-09-30 12:27:33'),(4,'Test','Pharmacist','pha@123.com','$2y$12$aMxa3r1SGAWXkqtls2YfAODFRXcPXi.6OT50HOWmvxnoO2fnaw.Q2',NULL,'0123456789','Test Pharmacy','verified','TEST-PHARM-001',1,'2026-10-06 13:02:13');
/*!40000 ALTER TABLE `Pharmacist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `Pharmacy_Medication`
--

LOCK TABLES `Pharmacy_Medication` WRITE;
/*!40000 ALTER TABLE `Pharmacy_Medication` DISABLE KEYS */;
INSERT INTO `Pharmacy_Medication` VALUES (1,1,'Paracetamol 500mg',45.99,'available','2026-09-30 12:27:33'),(2,2,'Amoxicillin 500mg',89.99,'available','2026-09-30 12:27:33'),(3,3,'Cetirizine 10mg',65.50,'available','2026-09-30 12:27:33'),(4,1,'Metformin 500mg',85.00,'available','2026-10-04 10:49:56'),(5,1,'Amlodipine 5mg',65.00,'available','2026-10-04 10:49:56'),(6,1,'Atorvastatin 20mg',120.00,'available','2026-10-04 10:49:56'),(7,1,'Paracetamol 500mg',45.99,'available','2026-10-05 12:12:02'),(8,2,'Paracetamol 500mg',39.99,'available','2026-10-05 12:12:02'),(9,3,'Paracetamol 500mg',42.50,'available','2026-10-05 12:12:02'),(10,4,'Paracetamol 500mg',47.99,'available','2026-10-05 12:12:02'),(11,1,'Amoxicillin 500mg',89.99,'available','2026-10-05 12:12:02'),(12,2,'Amoxicillin 500mg',84.50,'available','2026-10-05 12:12:02'),(13,3,'Amoxicillin 500mg',92.99,'available','2026-10-05 12:12:02'),(14,4,'Amoxicillin 500mg',87.99,'available','2026-10-05 12:12:02'),(15,1,'Cetirizine 10mg',59.99,'available','2026-10-05 12:12:02'),(16,2,'Cetirizine 10mg',54.99,'available','2026-10-05 12:12:02'),(17,3,'Cetirizine 10mg',57.50,'available','2026-10-05 12:12:02'),(18,4,'Cetirizine 10mg',61.99,'available','2026-10-05 12:12:02');
/*!40000 ALTER TABLE `Pharmacy_Medication` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `Prescriptions`
--

LOCK TABLES `Prescriptions` WRITE;
/*!40000 ALTER TABLE `Prescriptions` DISABLE KEYS */;
INSERT INTO `Prescriptions` VALUES (1,1,1,1,20,NULL,NULL,NULL,0,NULL,'2026-09-30 12:27:33'),(2,2,2,2,21,NULL,NULL,NULL,0,NULL,'2026-09-30 12:27:33'),(3,3,3,3,30,NULL,NULL,NULL,0,NULL,'2026-09-30 12:27:33'),(4,6,1,1,30,'1 tablet','Pain and fever relief','08:00:00',30,NULL,'2026-10-04 10:49:46'),(5,6,1,2,20,'1 capsule','Bacterial infection treatment','14:00:00',20,NULL,'2026-10-04 10:49:46'),(6,6,1,3,10,'1 tablet','Allergy symptom relief','21:00:00',10,NULL,'2026-10-04 10:49:46');
/*!40000 ALTER TABLE `Prescriptions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `Question_Answer`
--

LOCK TABLES `Question_Answer` WRITE;
/*!40000 ALTER TABLE `Question_Answer` DISABLE KEYS */;
INSERT INTO `Question_Answer` VALUES (1,1,2,'Regular physical activity, balanced nutrition and appropriate medical check-ups can all contribute to cardiovascular health.','2026-09-30 12:27:33'),(2,2,1,'Using a consistent schedule and following the instructions provided by your healthcare professional can help you keep track of medication.','2026-09-30 12:27:33');
/*!40000 ALTER TABLE `Question_Answer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `Resource_View`
--

LOCK TABLES `Resource_View` WRITE;
/*!40000 ALTER TABLE `Resource_View` DISABLE KEYS */;
INSERT INTO `Resource_View` VALUES (1,1,1,'2026-09-30 12:27:33'),(2,2,2,'2026-09-30 12:27:33');
/*!40000 ALTER TABLE `Resource_View` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-06 22:03:19
