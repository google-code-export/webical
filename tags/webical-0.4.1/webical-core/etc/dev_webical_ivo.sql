-- phpMyAdmin SQL Dump
-- version 2.8.0.4
-- http://www.phpmyadmin.net
-- 
-- Host: localhost
-- Generation Time: Oct 05, 2006 at 09:34 AM
-- Server version: 5.0.24
-- PHP Version: 4.3.10-16
-- 
-- Database: `dev_webical_ivo`
-- 

-- --------------------------------------------------------

-- 
-- Table structure for table `_auth_user`
-- 

CREATE TABLE `_auth_user` (
  `username` varchar(250) NOT NULL,
  `userpass` varchar(250) NOT NULL,
  PRIMARY KEY  (`username`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- 
-- Dumping data for table `_auth_user`
-- 

INSERT INTO `_auth_user` VALUES ('webical', 'webical');

-- --------------------------------------------------------

-- 
-- Table structure for table `auth_userrole`
-- 

CREATE TABLE `auth_userrole` (
  `username` varchar(250) NOT NULL,
  `role` varchar(250) NOT NULL,
  PRIMARY KEY  (`username`,`role`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- 
-- Dumping data for table `auth_userrole`
-- 

INSERT INTO `auth_userrole` VALUES ('webical', 'webical');

INSERT INTO `user` VALUES ('webical', 'webical', 'webical', 'webical', '1970-11-01');
INSERT INTO `calendar` VALUES (6, 'calendar2_webical_public', 'webdav', 'http://localhost/webdav/calendar2.ics', NULL, NULL, 'webical');
INSERT INTO `calendar` VALUES (6, 'calendar2_webical_private', 'webdav', 'http://localhost/webdav/calendar2.ics', 'username', 'password', 'webical');
