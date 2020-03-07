
DROP SCHEMA IF EXISTS obl;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ OBL;
USE OBL;

SET SQL_SAFE_UPDATES = 0;

DROP TABLE IF EXISTS `User`;
CREATE TABLE `User` (
  `ID` varchar(9) NOT NULL ,
  `password` varchar(16)NOT NULL default '',
  `permission` int(1) NOT NULL,
  `isOnline` boolean default false,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB;

--
--

/*!40000 ALTER TABLE `User`  */;
INSERT INTO `User` (`ID`,`password`,`permission`) VALUES 
 ('308328756','12345',1),
 ('308328757','12345',2),
 ('308328758','12345',3),
 
 ('319611604','12345',1),
 ('319611605','12345',2),
 ('319611606','12345',3),
 
 ('336489240','12345',1),
 ('336489241','12345',2),
 ('336489242','12345',3),
 
 ('203332226','12345',1),
 ('203332227','12345',2),
 ('203332228','12345',3),
 
 ('205727993','12345',1),
 ('205727994','12345',2),
 ('205727995','12345',3),
 
 ('308512561','aa1254',2),
 ('205124362','asd254',2),
 ('202124875','79456d',2),
 ('302134873','a2p254',3),
 ('354946422','654654',3),
 ('354126424','53fg12',3),
 ('241848926','d3fg12',3),
 ('247891429','d3fgds2',3),
 ('223646897','5464ads',3),
 ('216897163','5464456s',3);
 

DROP TABLE IF EXISTS `ReaderAccount`;
CREATE TABLE `ReaderAccount` (
  `ID` varchar(9) NOT NULL  ,
  `firstName` varchar(60) NOT NULL,
  `lastName`varchar(60) NOT NULL,
  `phone` varchar(10) NOT NULL,
   `email`  varchar(60)  NOT NULL,
   `address` varchar(60) default '',
   `educationYear` varchar(1) default '' ,
	`status` enum('Active','Frozen','Locked') default 'Active' NOT NULL,
    `numOfDelays` int(9) NOT NULL default 0,
  PRIMARY KEY  (`ID`),
  FOREIGN KEY (`ID`) REFERENCES `User` (`ID`)
) ENGINE=InnoDB;

--
--

/*!40000 ALTER TABLE `ReaderAccount`  */;
INSERT INTO `ReaderAccount` (`ID`,`firstName`,`lastName`,`phone`,`email`,`address`) VALUES 
 ('354126424','Gerken', 'Till','0508450984','o7789257@nwytg.net',"Zefat"),
 ('241848926','Yarger','Randy Jay','0508450984','dsa1656@nwytg.net',"Zefat"),
  ('336489242','Natali','Kama','0508450923','kameneva0894@gmail.com',"Nahariya");
  INSERT INTO `ReaderAccount` (`ID`,`firstName`,`lastName`,`phone`,`email`,`educationYear`) VALUES
 ('223646897','King','Tim','0508450984','45q1a95@nwytg.net',3),
  ('203332228','Ester','Asulin','0508450984','esterasulin93@gmail.com',4),
  ('216897163','Date','Chris','0508450984','oa87622@nwytg.net',2);
 INSERT INTO `ReaderAccount` (`ID`,`firstName`,`lastName`,`phone`,`email`,`address`,`educationYear`) VALUES 
 ('302134873','Edwards', 'Jeri','0508630114','o7071257@nwytg.net',"haifa",1),
 ('308328758','Ziv','Perez','0508630114','ziper02@gmail.com',"Nahariya hanita 4d",1),
 ('205727995','Ziv','Perez','0508630114','ziper02@gmail.com',"Nahariya hanita 4d",1),
 ('319611606','Vitali','Layzerman','0546604782','Lzvitali68@gmail.com',"Haifa",2);
  INSERT INTO `ReaderAccount` (`ID`,`firstName`,`lastName`,`phone`,`email`) VALUES
  ('354946422','Ratschiller','Tobias','0508450984','ods1257@nwytg.net'),
  ('247891429','Reese','Georg','0508450984','878a9w@nwytg.net');

 
 DROP TABLE IF EXISTS `Book`;
CREATE TABLE `Book` (
  `bookId` int(9) NOT NULL auto_increment ,
  `bookName` varchar(60) NOT NULL,
  `authorName`varchar(60) NOT NULL,
  `year` varchar(4) NOT NULL,
   `topic`  varchar(50)  NOT NULL,
	`isDesired` boolean default false,
   `edition` int(2) default 0 NOT NULL,
    `bookLocation` varchar(3) NOT NULL,
    
  PRIMARY KEY  (`bookId`)
) ENGINE=InnoDB;
 
 INSERT INTO `Book` (`bookId`,`bookName`,`topic`,`authorName`,`year`,`edition`,`bookLocation`) VALUES 
 (1,'Linux','Installation, Konfiguration, Anwendung','Kofler Michael','2010',1,'A-1'),
 (2,'The Definitive Guide to Excel VBA','VBA','Kofler Michael, Kramer David','1999',0,'A-1'),
 (3,'Client Server Survival Guide','SQL','Edwards Jeri, Harkey Dan, Orfali Robert','2010',0,'A-1'),
 (4,'Web Application Development with PHP 4.0','PHP','Gerken Till, Ratschiller Tobias','1999',0,'A-2'),
 (7,'MySQL','SQL','DuBois Paul','1999',0,'A-2'),
 (9,'MySQL & mSQL','software','King Tim, Reese Georg, Yarger Randy Jay','2010',0,'A-2'),
 (11,'A Guide to the SQL Standard','SQL','Darween Hugh, Date Chris','2010',0,'A-5'),
 (13,'Visual Basic 6','Programmiertechniken, Datenbanken, Internet','Kofler Michael','1999',0,'B-3'),
 (14,'Excel 2000 programmieren','software','Kofler Michael','2018',0,'B-3'),
 (17,'PHP - Grundlagen und Lösungen','Webserver-Programming unter Windows und Linux','Krause Jörg','1990',0,'B-4'),
 (18,'Nennen wir ihn Anna','drama','Pohl Peter','1990',0,'C-3'),
 (19,'Alltid den där Annette','drama','Pohl Peter','1999',0,'C-3'),
 (20,'Jag saknar dig, jag saknar dig','comdey','Pohl Peter','2018',0,'C-3'),
 (21,'LaTeX','comdey','Kopka Helmut','1999',0,'D-6'),
 (22,'Mathematica','Einführung, Anwendung, Referenz','Kofler Michael','2010',0,'D-6'),
 (23,'Maple','drama','Bitsch Gerhard, Kofler Michael, Komma Michael','2010',0,'D-2'),
 (24,'VBA-Programmierung mit Excel 7','Programming','Kofler Michael','2010',0,'F-2'),
 (25,'Linux für Internet und Intranet','Programming','Holz Helmut, Schmitt Bernd, Tikart Andreas','2017',0,'F-1'),
 (27,'Practical UNIX & Internet security','drama','Garfinkel Simon','1999',0,'F-2'),
 (30,'Visual Basic Datenbankprogrammierung','Client/Server-System','Kofler Michael','1990',0,'G-2'),
 (32,'Ute av verden','comdey','Knausgård Karl Ove','1999',0,'K-2'),
 (33,'MySQL','Installation, Programmierung, Referenz','Kofler Michael','2018',0,'V-4'),
 (34,'MySQL','software','Kofler Michael, Kramer David','2010',0,'F-5'),
 (41,'PHP 4','software','Krause Jörg','1999',0,'J-2'),
 (42,'Kärleken','software','Theodor Kallifatides','2010',1,'G-2'),
 (43,'Mit LaTeX ins Web','Elektronisches Publizieren mit TeX, HTML und XML','Goosens Michael, Rahtz Sebastian','2018',0,'Y-1'),
 (51,'Anklage Vatermord','Der Fall Philipp Halsmann','Pollack Martin','2010',2,'F-2'),
 (52,'A Programmer\'s Introduction to PHP 4.0','software','Gilmore W.J.','1999',2,'E-2');
 
 
 
 
 
 DROP TABLE IF EXISTS `Copy`;
CREATE TABLE `Copy` (
  `copyId` int(9) NOT NULL auto_increment ,
  `bookId` int(9) NOT NULL ,
  `borrowerId` varchar(9) default null,
  `borrowDate` date default null,
  `returnDate` date default null,
  PRIMARY KEY  (`copyId`),
  FOREIGN KEY (`bookId`) REFERENCES `Book` (`bookId`),
  FOREIGN KEY (`borrowerId`) REFERENCES `ReaderAccount` (`ID`)
) ENGINE=InnoDB;
 
 INSERT INTO `Copy` (`bookId`) VALUES 
 (21),(22),(23),(24),(9),(11),(13),
 (14),(17), (18),(19),(20),(21),(22),(23),(24), (25),(27),(30), (32),
 (33),(34),(41), (42),(43), (51), (52);
 
  INSERT INTO `Copy` (`bookId`,`borrowerId`,`borrowDate`,`returnDate`) VALUES 
 (1,'354126424','2019-02-02','2019-02-16'),
 (2,'308328758','2019-02-04','2019-02-18'),
 (32,'308328758','2019-02-05','2019-02-19'),
 (33,'203332228','2019-02-01','2019-02-15'),
 (34,'336489242','2019-02-05','2019-02-19'),
 (3,'205727995','2019-02-02','2019-02-16'),
 (4,'319611606','2019-02-05','2019-02-19'),
 (7,'247891429','2019-02-05','2019-02-19');
 
 
DROP TABLE IF EXISTS `Reservations`;
CREATE TABLE `Reservations` (
  `bookId` int(9) NOT NULL ,
  `readerAccountID` varchar(9) NOT NULL ,
  `Date` date default null,
  `Time` time default null,
  `startTimerImplement` datetime default null,
  PRIMARY KEY  (`bookId`,`readerAccountID`),
  FOREIGN KEY (`bookId`) REFERENCES `Book` (`bookId`),
  FOREIGN KEY (`readerAccountID`) REFERENCES `ReaderAccount` (`ID`)
) ENGINE=InnoDB;

  INSERT INTO `Reservations` (`bookId`,`readerAccountID`,`Date`,`Time`) VALUES 
 (1,'308328758','2019-02-12','14:00:30'),
 (1,'203332228','2019-02-13','14:30:30'),
 (33,'319611606','2019-02-11','16:30:30');


DROP TABLE IF EXISTS `History`;
CREATE TABLE `History` (
  `no` int(254) NOT NULL auto_increment ,
  `readerAccountID` varchar(9) default null,
  `bookId` int(9) default null ,
  `copyId` int(9) default null ,
  `action` varchar(150) NOT NULL ,
  `date` date NOT NULL,
  `Note` varchar(500) default '',
  PRIMARY KEY  (`no`),
  FOREIGN KEY (`readerAccountID`) REFERENCES `ReaderAccount` (`ID`)
) ENGINE=InnoDB;

  INSERT INTO `History` (`readerAccountID`,`Date`,`action`) VALUES 
 ('354126424','2019-02-01','Registration to OBL'),
 ('241848926','2019-02-01','Registration to OBL'),
  ('336489242','2019-02-01','Registration to OBL'),
 ('223646897','2019-02-01','Registration to OBL'),
  ('203332228','2019-02-01','Registration to OBL'),
  ('216897163','2019-02-01','Registration to OBL'),
 ('302134873','2019-02-01','Registration to OBL'),
 ('308328758','2019-02-01','Registration to OBL'),
 ('205727995','2019-02-01','Registration to OBL'),
 ('319611606','2019-02-01','Registration to OBL'),
  ('354946422','2019-02-01','Registration to OBL'),
  ('247891429','2019-02-01','Registration to OBL');
  
  INSERT INTO `History` (`Date`,`action`,`note`) VALUES 
   ('2019-02-01','Active readerAccounts','12'),
 ('2019-02-01','Freezed readerAccounts','0'),
 ('2019-02-01','Locked book','0'),
 ('2019-02-01','quantity of copies','35');
  
   INSERT INTO `History` (`bookId`,`copyId`,`readerAccountID`,`Date`,`action`) VALUES 
   (1,28,'354126424','2019-02-02','Borrow book'),
 (2,29,'308328758','2019-02-04','Borrow book'),
 (32,30,'308328758','2019-02-05','Borrow book'),
 (33,31,'203332228','2019-02-01','Borrow book'),
 (34,32,'336489242','2019-02-05','Borrow book'),
 (3,33,'205727995','2019-02-02','Borrow book'),
 (4,34,'319611606','2019-02-05','Borrow book'),
 (7,35,'247891429','2019-02-05','Borrow book');
 
   INSERT INTO `History` (`bookId`,`readerAccountID`,`Date`,`action`) VALUES 
   (1,'308328758','2019-02-12','Reserve book'),
	(1,'203332228','2019-02-13','Reserve book'),
	(33,'319611606','2019-02-11','Reserve book');
	DROP TABLE IF EXISTS `UserStatusHistory`;
    
CREATE TABLE `UserStatusHistory` (
  `readerAccountID` varchar(9) NOT NULL ,
  `status` enum('Active','Frozen','Locked')  NOT NULL,
  PRIMARY KEY  (`readerAccountID`,`status`),
  FOREIGN KEY (`readerAccountID`) REFERENCES `ReaderAccount` (`ID`)
) ENGINE=InnoDB;


/*!40000 ALTER TABLE `UserStatusHistory`  */;
INSERT INTO `UserStatusHistory` (`readerAccountID`,`status`) VALUES 
 ('354126424','Active'),
 ('241848926','Active'),
  ('336489242','Active'),
 ('223646897','Active'),
  ('203332228','Active'),
  ('216897163','Active'),
 ('302134873','Active'),
 ('308328758','Active'),
 ('205727995','Active'),
 ('319611606','Active'),
  ('354946422','Active'),
  ('247891429','Active');

DROP TABLE IF EXISTS `ReportsHistory`;
CREATE TABLE `ReportsHistory` (
	`Year` varchar(4) NOT NULL,
    `Month` varchar(4) NOT NULL,
  `ActiveReaderAccounts` int(5) NOT NULL ,
  `FreezedReaderAccounts` int(5) NOT NULL ,
  `LockedReaderAccounts` int(5) NOT NULL ,
  `totalBookCopies` int(5) NOT NULL ,
  `didntReturned` int(5) NOT NULL ,
  PRIMARY KEY  (`Year`,`Month`)
) ENGINE=InnoDB;
