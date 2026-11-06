-- phpMyAdmin SQL Dump
-- version 5.2.3
-- https://www.phpmyadmin.net/
--
-- Servidor: mysql:3306
-- Xerado en: 06 de Nov de 2026 ás 09:10
-- Versión do servidor: 8.4.11
-- Versión do PHP: 8.3.26

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `bbdd`
--

-- --------------------------------------------------------

--
-- Estrutura da táboa `citas`
--

CREATE TABLE `citas` (
  `idcita` int NOT NULL,
  `citadnipac` varchar(9) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `citaiddoc` int NOT NULL,
  `fechacita` date NOT NULL,
  `horacita` time NOT NULL,
  `motivo` varchar(200) DEFAULT NULL,
  `diagnostico` text,
  `tratamiento` text,
  `estadocita` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'Pendiente'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `citas`
--
ALTER TABLE `citas`
  ADD PRIMARY KEY (`idcita`),
  ADD KEY `dnipac` (`citadnipac`),
  ADD KEY `iddoc` (`citaiddoc`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `citas`
--
ALTER TABLE `citas`
  MODIFY `idcita` int NOT NULL AUTO_INCREMENT;

--
-- Restricións para os envorcados das táboas
--

--
-- Restricións para a táboa `citas`
--
ALTER TABLE `citas`
  ADD CONSTRAINT `citas_ibfk_1` FOREIGN KEY (`citadnipac`) REFERENCES `pacientes` (`dnipac`),
  ADD CONSTRAINT `citas_ibfk_2` FOREIGN KEY (`citaiddoc`) REFERENCES `doctores` (`iddoc`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
