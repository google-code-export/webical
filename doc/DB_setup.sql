-- ***************************************************** --
--                                                       --
-- Setup for the Webical database                        --
--                                                       --
-- ***************************************************** --

-- --------------------------------------------------------

--
-- Table structure for table `application_settings`
--

DROP TABLE IF EXISTS `application_settings`;
CREATE TABLE IF NOT EXISTS `application_settings` (
  `applicationSettingsId` bigint(20) NOT NULL auto_increment,
  `customPageTitle` mediumtext,
  `pluginWorkPath` mediumtext,
  `pluginPackageExtension` mediumtext,
  `calendarRefreshTimeMs` int(11) default NULL,
  `pluginCleanupEnabled` bit(1) default NULL,
  `configurationUsername` mediumtext,
  `configurationPassword` mediumtext,
  PRIMARY KEY  (`applicationSettingsId`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `application_settings_plugin_paths`
--

DROP TABLE IF EXISTS `application_settings_plugin_paths`;
CREATE TABLE IF NOT EXISTS `application_settings_plugin_paths` (
  `applicationSettingsId` bigint(20) NOT NULL,
  `plugin_path` mediumtext,
  KEY `FK888DA9CFD92BC9E2` (`applicationSettingsId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `application_settings_resource_paths`
--

DROP TABLE IF EXISTS `application_settings_resource_paths`;
CREATE TABLE IF NOT EXISTS `application_settings_resource_paths` (
  `applicationSettingsId` bigint(20) NOT NULL,
  `resource_path` mediumtext,
  KEY `FK8C31272AD92BC9E2` (`applicationSettingsId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `calendar`
--

DROP TABLE IF EXISTS `calendar`;
CREATE TABLE IF NOT EXISTS `calendar` (
  `calendarId` bigint(20) NOT NULL auto_increment,
  `name` mediumtext NOT NULL,
  `type` mediumtext NOT NULL,
  `url` mediumtext NOT NULL,
  `username` mediumtext,
  `password` mediumtext,
  `visible` bit(1) default NULL,
  `offSetFrom` bigint(20) default NULL,
  `offSetTo` bigint(20) default NULL,
  `lastRefreshTimeStamp` bigint(20) default NULL,
  `userId` varchar(255) NOT NULL,
  PRIMARY KEY  (`calendarId`),
  KEY `FKF55EFB3EF4310590` (`userId`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `event`
--

DROP TABLE IF EXISTS `event`;
CREATE TABLE IF NOT EXISTS `event` (
  `eventId` bigint(20) NOT NULL auto_increment,
  `calendarId` bigint(20) NOT NULL,
  `clazz` mediumtext,
  `description` mediumtext,
  `geo` mediumtext,
  `location` mediumtext,
  `organizer` mediumtext,
  `status` mediumtext,
  `summary` mediumtext,
  `transp` mediumtext,
  `uid` mediumtext,
  `url` mediumtext,
  `allDay` bit(1) default NULL,
  `created` datetime default NULL,
  `dtStart` datetime default NULL,
  `lastMod` datetime default NULL,
  `dtStamp` datetime default NULL,
  `seq` int(11) default NULL,
  `dtEnd` datetime default NULL,
  `duration` varchar(255) default NULL,
  PRIMARY KEY  (`eventId`),
  KEY `FK5C6729A5DB02C36` (`calendarId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `event_attach`
--

DROP TABLE IF EXISTS `event_attach`;
CREATE TABLE IF NOT EXISTS `event_attach` (
  `eventId` bigint(20) NOT NULL,
  `attach` mediumtext,
  KEY `FK2079378A47C8C010` (`eventId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `event_attendee`
--

DROP TABLE IF EXISTS `event_attendee`;
CREATE TABLE IF NOT EXISTS `event_attendee` (
  `eventId` bigint(20) NOT NULL,
  `attendee` mediumtext,
  KEY `FKE746D8BF47C8C010` (`eventId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `event_categories`
--

DROP TABLE IF EXISTS `event_categories`;
CREATE TABLE IF NOT EXISTS `event_categories` (
  `eventId` bigint(20) NOT NULL,
  `categories` mediumtext,
  KEY `FKD2164E147C8C010` (`eventId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `event_comment`
--

DROP TABLE IF EXISTS `event_comment`;
CREATE TABLE IF NOT EXISTS `event_comment` (
  `eventId` bigint(20) NOT NULL,
  `comment` mediumtext,
  KEY `FK4F94CDBA47C8C010` (`eventId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `event_contact`
--

DROP TABLE IF EXISTS `event_contact`;
CREATE TABLE IF NOT EXISTS `event_contact` (
  `eventId` bigint(20) NOT NULL,
  `contact` mediumtext,
  KEY `FK4FA6037B47C8C010` (`eventId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `event_exDate`
--

DROP TABLE IF EXISTS `event_exDate`;
CREATE TABLE IF NOT EXISTS `event_exDate` (
  `eventId` bigint(20) NOT NULL,
  `exDate` datetime default NULL,
  KEY `FK276F284647C8C010` (`eventId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `event_exRule`
--

DROP TABLE IF EXISTS `event_exRule`;
CREATE TABLE IF NOT EXISTS `event_exRule` (
  `eventId` bigint(20) NOT NULL,
  `exRule` mediumtext,
  KEY `FK2775CF9447C8C010` (`eventId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `event_rDate`
--

DROP TABLE IF EXISTS `event_rDate`;
CREATE TABLE IF NOT EXISTS `event_rDate` (
  `eventId` bigint(20) NOT NULL,
  `rDate` datetime default NULL,
  KEY `FK1AABD55B47C8C010` (`eventId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `event_related`
--

DROP TABLE IF EXISTS `event_related`;
CREATE TABLE IF NOT EXISTS `event_related` (
  `eventId` bigint(20) NOT NULL,
  `related` mediumtext,
  KEY `FK57EE7B8647C8C010` (`eventId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `event_resources`
--

DROP TABLE IF EXISTS `event_resources`;
CREATE TABLE IF NOT EXISTS `event_resources` (
  `eventId` bigint(20) NOT NULL,
  `resources` mediumtext,
  KEY `FKA08132C047C8C010` (`eventId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `event_rRule`
--

DROP TABLE IF EXISTS `event_rRule`;
CREATE TABLE IF NOT EXISTS `event_rRule` (
  `eventId` bigint(20) NOT NULL,
  `rRule` mediumtext,
  KEY `FK1AB27CA947C8C010` (`eventId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `event_rStatus`
--

DROP TABLE IF EXISTS `event_rStatus`;
CREATE TABLE IF NOT EXISTS `event_rStatus` (
  `eventId` bigint(20) NOT NULL,
  `rStatus` mediumtext,
  KEY `FK39A7FE5F47C8C010` (`eventId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `event_xProps`
--

DROP TABLE IF EXISTS `event_xProps`;
CREATE TABLE IF NOT EXISTS `event_xProps` (
  `id` bigint(20) NOT NULL,
  `xprop_value` mediumtext,
  `xprop_name` varchar(255) NOT NULL,
  PRIMARY KEY  (`id`,`xprop_name`),
  KEY `FK45BCAD1D99D48BF6` (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `options`
--

DROP TABLE IF EXISTS `options`;
CREATE TABLE IF NOT EXISTS `options` (
  `id` bigint(20) NOT NULL auto_increment,
  `settings` bigint(20) default NULL,
  `name` varchar(255) default NULL,
  `value` tinyblob,
  `settings_id` bigint(20) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `FKB586869EF9C97865` (`settings`),
  KEY `FKB586869E9D0BDAF9` (`settings_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `plugin_settings`
--

DROP TABLE IF EXISTS `plugin_settings`;
CREATE TABLE IF NOT EXISTS `plugin_settings` (
  `id` bigint(20) NOT NULL,
  `pluginClass` varchar(255) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK182EE7AFA446C95D` (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `settings`
--

DROP TABLE IF EXISTS `settings`;
CREATE TABLE IF NOT EXISTS `settings` (
  `id` bigint(20) NOT NULL auto_increment,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user` (
  `userId` varchar(255) NOT NULL,
  `firstName` text,
  `lastNamePrefix` text,
  `lastName` text,
  `birthDate` date default NULL,
  PRIMARY KEY  (`userId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `user_plugin_settings`
--

DROP TABLE IF EXISTS `user_plugin_settings`;
CREATE TABLE IF NOT EXISTS `user_plugin_settings` (
  `id` bigint(20) NOT NULL,
  `pluginClass` varchar(255) default NULL,
  `user` varchar(255) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK8704333B263CBF35` (`user`),
  KEY `FK8704333BA446C95D` (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `user_settings`
--

DROP TABLE IF EXISTS `user_settings`;
CREATE TABLE `user_settings` (
  `id` bigint(20) NOT NULL,
  `defaultCalendarView` int(11) default NULL,
  `firstDayOfWeek` int(11) default NULL,
  `numberOfAgendaDays` int(11) default NULL,
  `dateFormat` varchar(255) default NULL,
  `timeFormat` varchar(255) default NULL,
  `user` varchar(255) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK58861617263CBF35` (`user`),
  KEY `FK58861617A446C95D` (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

