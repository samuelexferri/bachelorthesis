-- MySQL dump 10.13  Distrib 5.7.25, for Linux (x86_64)
--
-- Host: localhost    Database: se4med
-- ------------------------------------------------------
-- Server version	5.7.25-0ubuntu0.18.04.2

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `application`
--

DROP TABLE IF EXISTS `application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `application` (
  `id` varchar(10) NOT NULL COMMENT 'questa tabella memorizza tutte le applicazioni. per ogni applicazione memorizza a quale progetto appartiene, il nome, una descrizione e se è una app web o no.\n\nid applicazione, identifica univocamente l''applicazione',
  `idproject` int(11) DEFAULT NULL COMMENT 'fa riferimento all''id del progetto al quale l''applicazione appartiene -> FK\n\nse l''id della tabella project viene aggiornato, viene aggiornato anche idproject\nse l''id della tabella project viene cancellato e c''è un record che contiene quell''id, non viene permessa la cancellazione del progetto',
  `name` varchar(50) DEFAULT NULL,
  `description` text,
  `webApp` tinyint(1) DEFAULT NULL COMMENT 'indica se l''applicazione è un''applicazione web oppure è un altro tipo di applicazione (es: per smartphone)\n0 -> NO WEB APP\n1 -> WEB APP',
  PRIMARY KEY (`id`),
  KEY `idproject_idx` (`idproject`),
  CONSTRAINT `idproject` FOREIGN KEY (`idproject`) REFERENCES `project` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `doctor`
--

DROP TABLE IF EXISTS `doctor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `doctor` (
  `email` varchar(100) NOT NULL COMMENT 'questa tabella memorizza i dottori che si registrano al sito. Devono fornire email, nome, cognome e password.',
  PRIMARY KEY (`email`),
  CONSTRAINT `emaildoc` FOREIGN KEY (`email`) REFERENCES `user` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `doctorapp`
--

DROP TABLE IF EXISTS `doctorapp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `doctorapp` (
  `emaildoctor` varchar(100) NOT NULL COMMENT 'questa tabella mette in relazione il dottore e le applicazioni.\nIl dottore può consultare i dati delle applicazioni per le quali si è registrato\n\nemaildoctor identifica il dottore -> FK\n\nse l''email della tabella doctor viene cancellata/modificata, si cancellano/modificano tutti i record che contengono quel valore',
  `idapp` varchar(10) NOT NULL COMMENT 'identifica l''applicazione per la quale il dottore è abilitato a consultare i dati -> FK\n\nse l''id della tabella application viene aggiornato, viene aggiornato anche idapp\nse l''id della tabella application viene cancellato e c''è un record che contiene quell''id, non viene permessa la cancellazione dell''applicazione',
  PRIMARY KEY (`emaildoctor`,`idapp`),
  KEY `doctorapp_idx` (`idapp`),
  CONSTRAINT `appDoctor` FOREIGN KEY (`idapp`) REFERENCES `application` (`id`),
  CONSTRAINT `emaildoctorDoctorApp` FOREIGN KEY (`emaildoctor`) REFERENCES `doctor` (`email`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `doctorpatient`
--

DROP TABLE IF EXISTS `doctorpatient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `doctorpatient` (
  `emaildoctor` varchar(100) NOT NULL COMMENT 'questa tabella indica per ogni utente quale dottore può consultare i dati delle applicazioni, inoltre per ogni dottore indica quale applicazioni può consultare.\n\ndottore che può consultare i dati delle app -> FK\n\nse l''email della tabella doctor viene cancellata/modificata, si cancellano/modificano tutti i record che contengono quel valore',
  `emailpatient` varchar(100) NOT NULL COMMENT 'email utente per identificare l''utente che autorizza il dottore -> FK\n\nse l''email della tabella user viene cancellata/modificata, si cancellano/modificano tutti i record che contengono quel valore',
  `username` varchar(100) NOT NULL COMMENT 'username per identificare l''utete che autorizza il dottore in modo univoco -> FK\n\nse lo user della tabella user viene cancellata/modificata, si cancellano/modificano tutti i record che contengono quel valore',
  `idapp` varchar(10) NOT NULL COMMENT 'idapp per la quale il dottore è autorizzato a visualizzare i dati del paziente -> FK\n\nse l''id della tabella application viene aggiornato, viene aggiornato anche idapp\nse l''id della tabella application viene cancellato e c''è un record che contiene quell''id, non viene permessa la cancellazione dell''applicazione\n',
  PRIMARY KEY (`emaildoctor`,`emailpatient`,`username`,`idapp`),
  KEY `appuser_idx` (`idapp`),
  KEY `emailUser_idx` (`username`,`emailpatient`),
  CONSTRAINT `appuserdoctor` FOREIGN KEY (`idapp`) REFERENCES `application` (`id`),
  CONSTRAINT `emaildoctor` FOREIGN KEY (`emaildoctor`) REFERENCES `doctor` (`email`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `emailuser` FOREIGN KEY (`username`, `emailpatient`) REFERENCES `patient` (`username`, `emailpatient`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `patient`
--

DROP TABLE IF EXISTS `patient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `patient` (
  `username` varchar(100) NOT NULL COMMENT 'L''utente si registra fornendo UserName, Surname, Name, DataOfBirth, EmailUser e Password.\nNel caso di prima registrazione (l''email non è mai stata registrata), viene anche inserita un''istanza nella tabella EMAIL. Se chi fa il test non è il proprietario dell''email devono essere forniti anche nome e cognome \ndel proprietario dell''email e vengono salvati nella tabelle Email. Nel caso in cui il proprietario dell''email corrisponda all''utente registrato, vengono riportati nome e cognome nella tabella Email. Questo perché se \ngli utenti sono bambini e utilizzano l''email del genitore si vuole memorizzare nome e cognome del genitore.\nUna volta creato un utente con email X, per poter creare altri utenti è necessario prima fare il login e successivamente permettere l''inserimento di altri utenti.\nIl login avviene nel seguente modo:\nInserimento email e password\nSe è associato più di un utente viene mostrato',
  `surname` varchar(50) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `dateofbirth` date DEFAULT NULL,
  `emailpatient` varchar(100) NOT NULL COMMENT 'fa riferiemento all''email con cui l''utente è registrato\n\nse l''email della tabella user viene cancellata/modificata, si cancellano/modificano tutti i record che contengono quel valore',
  `settings` text,
  PRIMARY KEY (`username`,`emailpatient`),
  KEY `emailuser` (`emailpatient`),
  CONSTRAINT `emailPatient` FOREIGN KEY (`emailpatient`) REFERENCES `user` (`email`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `patientapp`
--

DROP TABLE IF EXISTS `patientapp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `patientapp` (
  `emailpatient` varchar(100) NOT NULL COMMENT 'email utente per identificare l''utente che usa l''app -> FK\n\nse l''email della tabella user viene cancellata/modificata, si cancellano/modificano tutti i record che contengono quel valore',
  `username` varchar(100) NOT NULL COMMENT 'username per identificare l''utete che usa l''app -> FK\n\nse lo user della tabella user viene cancellata/modificata, si cancellano/modificano tutti i record che contengono quel valore',
  `idapp` varchar(10) NOT NULL COMMENT 'identifica l''applicazione per la quale il paziente è abilitato -> FK\n\nse l''id della tabella application viene aggiornato, viene aggiornato anche idapp\nse l''id della tabella application viene cancellato e c''è un record che contiene quell''id, non viene permessa la cancellazione dell''applicazione',
  `settings` text,
  PRIMARY KEY (`emailpatient`,`username`,`idapp`),
  KEY `appuser_idx` (`idapp`),
  CONSTRAINT `appuser` FOREIGN KEY (`idapp`) REFERENCES `application` (`id`),
  CONSTRAINT `patientemailApp` FOREIGN KEY (`emailpatient`, `username`) REFERENCES `patient` (`emailpatient`, `username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `patientdoc`
--

DROP TABLE IF EXISTS `patientdoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `patientdoc` (
  `id` int(11) unsigned zerofill NOT NULL AUTO_INCREMENT,
  `surname` varchar(50) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `emaildoc` varchar(100) NOT NULL,
  PRIMARY KEY (`id`,`emaildoc`),
  KEY `fk_patientdoc_1_idx` (`emaildoc`),
  CONSTRAINT `fk_patientdoc_1` FOREIGN KEY (`emaildoc`) REFERENCES `doctor` (`email`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project`
--

DROP TABLE IF EXISTS `project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'questa tabella identifica i progetti',
  `name` varchar(50) DEFAULT NULL,
  `description` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `result_not_registered`
--

DROP TABLE IF EXISTS `result_not_registered`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `result_not_registered` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'questa tabella contiene i risultati\n\nogni record è identificato da un id univoco',
  `idapp` varchar(10) NOT NULL COMMENT 'fa riferimento all''app con cui si è fatto il test/gioco/.... -> FK\n\nse l''id della tabella application viene aggiornato, viene aggiornato anche idapp\nse l''id della tabella application viene cancellato e c''è un record che contiene quell''id, non viene permessa la cancellazione dell''applicazione\n\n',
  `dateandtime` datetime DEFAULT NULL COMMENT 'data/ora a cui si è giocato',
  `result` text COMMENT 'è una stringa contenente informazioni relative al risultato del gioco/trattamento, questa varia in base al gioco/trattamento che si sta registrando',
  `idutente` int(11) unsigned zerofill DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_result_not_registered_1_idx` (`idutente`),
  CONSTRAINT `fk_result_not_registered_1` FOREIGN KEY (`idutente`) REFERENCES `patientdoc` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `results`
--

DROP TABLE IF EXISTS `results`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `results` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'questa tabella contiene i risultati\n\nogni record è identificato da un id univoco',
  `idapp` varchar(10) NOT NULL COMMENT 'fa riferimento all''app con cui si è fatto il test/gioco/.... -> FK\n\nse l''id della tabella application viene aggiornato, viene aggiornato anche idapp\nse l''id della tabella application viene cancellato e c''è un record che contiene quell''id, non viene permessa la cancellazione dell''applicazione\n\n',
  `username` varchar(100) NOT NULL COMMENT 'fa riferimento all''utente che ha effettuato il risultato -> FK\n\nse lo user della tabella user viene cancellata/modificata, si cancellano/modificano tutti i record che contengono quel valore\n',
  `emailpatient` varchar(100) NOT NULL COMMENT 'fa riferimento all''utente che ha effettuato il risultato -> FK\n\nse l''email della tabella user viene cancellata/modificata, si cancellano/modificano tutti i record che contengono quel valore\n',
  `dateandtime` datetime DEFAULT NULL COMMENT 'data/ora a cui si è giocato',
  `result` text COMMENT 'è una stringa contenente informazioni relative al risultato del gioco/trattamento, questa varia in base al gioco/trattamento che si sta registrando',
  PRIMARY KEY (`id`),
  KEY `username` (`username`,`emailpatient`),
  KEY `appresults_idx` (`idapp`),
  CONSTRAINT `appresults` FOREIGN KEY (`idapp`) REFERENCES `application` (`id`),
  CONSTRAINT `results_ibfk_1` FOREIGN KEY (`username`, `emailpatient`) REFERENCES `patient` (`username`, `emailpatient`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `email` varchar(100) NOT NULL COMMENT 'questa tabella identifica i proprietari delle email, ogni proprietario può avere associato uno o più utenti.\n',
  `surname` varchar(50) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `password` varchar(65) DEFAULT NULL,
  `activated` tinyint(1) DEFAULT '0' COMMENT 'indica se l''email è stata attivata\n0 -> NON ATTIVATA\n1 -> ATTIVATA',
  `attemptslogin` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-02-04 11:21:20
