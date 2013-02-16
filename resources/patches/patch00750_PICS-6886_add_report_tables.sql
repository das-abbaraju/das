-- Create the tables 
CREATE TABLE `report_column` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `reportID` int(11) NOT NULL,
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `sqlFunction` varchar(15) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `width` int(8) NOT NULL DEFAULT '200',
  `sortIndex` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=290 DEFAULT CHARSET=utf8;

CREATE TABLE `report_filter` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `reportID` int(11) NOT NULL,
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `operator` varchar(15) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `value` varchar(400) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `columnCompare` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `sqlFunction` varchar(15) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=111 DEFAULT CHARSET=latin1;

CREATE TABLE `report_sort` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `reportID` int(11) NOT NULL,
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `ascending` tinyint(1) NOT NULL DEFAULT '1',
  `sqlFunction` varchar(15) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8;
