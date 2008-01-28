-- phpMyAdmin SQL Dump
-- version 2.8.0.4
-- http://www.phpmyadmin.net
-- 
-- Host: 127.0.0.1:3307
-- Generation Time: Jan 16, 2007 at 10:44 PM
-- Server version: 4.1.15
-- PHP Version: 4.4.4-0.dotdeb.3
-- 
-- Database: `org_webical_demo`
-- 

-- --------------------------------------------------------

-- 
-- Table structure for table `_auth_user`
-- 

DROP TABLE IF EXISTS `_auth_user`;
CREATE TABLE IF NOT EXISTS `_auth_user` (
  `username` varchar(250) NOT NULL default '',
  `userpass` varchar(250) NOT NULL default '',
  PRIMARY KEY  (`username`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- 
-- Dumping data for table `_auth_user`
-- 

INSERT INTO `_auth_user` (`username`, `userpass`) VALUES ('webical', 'webical');

-- --------------------------------------------------------

-- 
-- Table structure for table `application_settings`
-- 

DROP TABLE IF EXISTS `application_settings`;
CREATE TABLE IF NOT EXISTS `application_settings` (
  `applicationSettingsId` bigint(20) NOT NULL auto_increment,
  `customPageTitle` varchar(255) default NULL,
  `pluginWorkPath` varchar(255) default NULL,
  `pluginPackageExtension` varchar(255) default NULL,
  `calendarRefreshTimeMs` int(11) default NULL,
  `pluginCleanupEnabled` tinyint(1) default NULL,
  `configurationUsername` varchar(255) default NULL,
  `configurationPassword` varchar(255) default NULL,
  PRIMARY KEY  (`applicationSettingsId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=2 ;

-- 
-- Dumping data for table `application_settings`
-- 

-- The admin password needs to be filled in!!!
INSERT INTO `application_settings` 
(`applicationSettingsId`, `customPageTitle`, `pluginWorkPath`, 
`pluginPackageExtension`, `calendarRefreshTimeMs`, `pluginCleanupEnabled`, 
`configurationUsername`, `configurationPassword`) 
VALUES 
(1, 'Webical release 0.2 >>> data is deleted every night! <<<', '/tmp', 
'.zip', 60000, 0, 
'admin', '');

-- --------------------------------------------------------

-- 
-- Table structure for table `auth_userrole`
-- 

DROP TABLE IF EXISTS `auth_userrole`;
CREATE TABLE IF NOT EXISTS `auth_userrole` (
  `username` varchar(250) NOT NULL default '',
  `role` varchar(250) NOT NULL default '',
  PRIMARY KEY  (`username`,`role`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- 
-- Dumping data for table `auth_userrole`
-- 

INSERT INTO `auth_userrole` (`username`, `role`) VALUES ('webical', 'webicaluser');

-- --------------------------------------------------------

-- 
-- Table structure for table `calendar`
-- 

DROP TABLE IF EXISTS `calendar`;
CREATE TABLE IF NOT EXISTS `calendar` (
  `calendarId` bigint(20) NOT NULL auto_increment,
  `name` varchar(255) NOT NULL default '',
  `type` varchar(255) NOT NULL default '',
  `url` varchar(255) NOT NULL default '',
  `username` varchar(255) default NULL,
  `password` varchar(255) default NULL,
  `visible` tinyint(1) default NULL,
  `offSetFrom` bigint(20) default NULL,
  `offSetTo` bigint(20) default NULL,
  `lastRefreshTimeStamp` bigint(20) default NULL,
  `userId` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`calendarId`),
  KEY `FKF55EFB3EF4310590` (`userId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=2 ;

-- 
-- Dumping data for table `calendar`
-- 

INSERT INTO `calendar` (`calendarId`, `name`, `type`, `url`, `username`, `password`, `visible`, `offSetFrom`, `offSetTo`, `lastRefreshTimeStamp`, `userId`) VALUES (1, 'webical demo', 'ical-webdav', 'http://demo.webical.org/dav/webicaldemo.ics', NULL, NULL, 1, -1, 0, 1168983765938, 'webical');

-- --------------------------------------------------------

-- 
-- Table structure for table `user`
-- 

DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user` (
  `userId` varchar(255) NOT NULL default '',
  `firstName` varchar(255) default NULL,
  `lastNamePrefix` varchar(255) default NULL,
  `lastName` varchar(255) default NULL,
  `birthDate` date default NULL,
  PRIMARY KEY  (`userId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- 
-- Dumping data for table `user`
-- 

INSERT INTO `user` (`userId`, `firstName`, `lastNamePrefix`, `lastName`, `birthDate`) VALUES ('webical', NULL, NULL, NULL, NULL);

