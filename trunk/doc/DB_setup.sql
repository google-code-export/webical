-- ***************************************************** --
--                                                       --
-- Setup for the Webical database (MySQL)                --
--                                                       --
-- ***************************************************** --

--
-- Drop old DB tables in the right sequence
--

use WebiCal ;

DROP TABLE IF EXISTS `event_xProps`;
DROP TABLE IF EXISTS `event_rStatus`;
DROP TABLE IF EXISTS `event_rRule`;
DROP TABLE IF EXISTS `event_resources`;
DROP TABLE IF EXISTS `event_related`;
DROP TABLE IF EXISTS `event_rDate`;
DROP TABLE IF EXISTS `event_exRule`;
DROP TABLE IF EXISTS `event_exDate`;
DROP TABLE IF EXISTS `event_contact`;
DROP TABLE IF EXISTS `event_comment`;
DROP TABLE IF EXISTS `event_categories`;
DROP TABLE IF EXISTS `event_attendee`;
DROP TABLE IF EXISTS `event_attach`;
DROP TABLE IF EXISTS `event`;
DROP TABLE IF EXISTS `calendar`;

DROP TABLE IF EXISTS `user_settings`;
DROP TABLE IF EXISTS `plugin_settings`;
DROP TABLE IF EXISTS `user_plugin_settings`;
DROP TABLE IF EXISTS `options`;
DROP TABLE IF EXISTS `settings`;

DROP TABLE IF EXISTS `appl_sett_resource_paths`;
DROP TABLE IF EXISTS `appl_sett_plugin_paths`;
DROP TABLE IF EXISTS `application_settings`;

DROP TABLE IF EXISTS `wcaluser`;

-- --------------------------------------------------------

--
-- Table structure for table `application_settings`
--

CREATE TABLE IF NOT EXISTS `application_settings` (
  `applicationSettingsId` bigint NOT NULL auto_increment,
  `lastUpdateTime` datetime NOT NULL default 0;
  `customPageTitle` longtext,         -- why does hibernate want longtexts?
  `pluginWorkPath` longtext,
  `pluginPackageExtension` longtext,
  `calendarRefreshTimeMs` integer default NULL,
  `pluginCleanupEnabled` bit(1) default 0,
  `configurationUsername` longtext,
  `configurationPassword` longtext,
  PRIMARY KEY (`applicationSettingsId`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `application_settings_plugin_paths`
--

CREATE TABLE IF NOT EXISTS `appl_sett_plugin_paths` (
  `applicationSettingsId` bigint NOT NULL,
  `plugin_path` longtext,
  FOREIGN KEY (`applicationSettingsId`) REFERENCES `application_settings`(`applicationSettingsId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `application_settings_resource_paths`
--

CREATE TABLE IF NOT EXISTS `appl_sett_resource_paths` (
  `applicationSettingsId` bigint NOT NULL,
  `resource_path` longtext,
  FOREIGN KEY (`applicationSettingsId`) REFERENCES `application_settings`(`applicationSettingsId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `wcaluser`
--

CREATE TABLE IF NOT EXISTS `wcaluser` (
  `userId` varchar(255) NOT NULL,
  `lastUpdateTime` datetime NOT NULL default 0;
  `firstName` longtext,
  `lastNamePrefix` longtext,
  `lastName` longtext,
  `birthDate` date default NULL,
  PRIMARY KEY (`userId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `calendar`
--

CREATE TABLE IF NOT EXISTS `calendar` (
  `calendarId` bigint NOT NULL auto_increment,
  `lastUpdateTime` datetime NOT NULL default 0;
  `name` longtext NOT NULL,
  `type` longtext NOT NULL,
  `url` longtext NOT NULL,
  `username` longtext,
  `password` longtext,
  `visible` bit(1) DEFAULT 1,
  `offSetFrom` integer DEFAULT 0,
  `offSetTo` integer DEFAULT 0,
  `lastRefreshTimeStamp` bigint default NULL,
  `userId` varchar(255) NOT NULL,
  PRIMARY KEY (`calendarId`),
  FOREIGN KEY (`userId`) REFERENCES `wcaluser`(`userId`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `event`
--

CREATE TABLE IF NOT EXISTS `event` (
  `eventId` bigint NOT NULL auto_increment,
  `lastUpdateTime` datetime NOT NULL default 0;
  `calendarId` bigint NOT NULL,
  `clazz` longtext,
  `description` longtext,
  `geo` longtext,
  `location` longtext,
  `organizer` longtext,
  `status` longtext,
  `summary` longtext,
  `transp` longtext,
  `wuid` longtext,         -- was: uid
  `url` longtext,
  `allDay` bit(1) default NULL,
  `created` datetime default NULL,
  `dtStart` datetime default NULL,
  `lastMod` datetime default NULL,
  `dtStamp` datetime default NULL,
  `seq` integer default NULL,
  `dtEnd` datetime default NULL,
  `duration` varchar(255) default NULL,
  PRIMARY KEY (`eventId`),
  FOREIGN KEY (`calendarId`) REFERENCES `calendar`(`calendarId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `event_attach`
--

CREATE TABLE IF NOT EXISTS `event_attach` (
  `eventId` bigint NOT NULL,
  `attach` longtext,
  FOREIGN KEY (`eventId`) REFERENCES `event`(`eventId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `event_attendee`
--

CREATE TABLE IF NOT EXISTS `event_attendee` (
  `eventId` bigint NOT NULL,
  `attendee` longtext,
  FOREIGN KEY (`eventId`) REFERENCES `event`(`eventId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `event_categories`
--

CREATE TABLE IF NOT EXISTS `event_categories` (
  `eventId` bigint NOT NULL,
  `categories` longtext,
  FOREIGN KEY (`eventId`) REFERENCES `event`(`eventId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `event_comment`
--

CREATE TABLE IF NOT EXISTS `event_comment` (
  `eventId` bigint NOT NULL,
  `comments` longtext,         -- was: comment
  FOREIGN KEY (`eventId`) REFERENCES `event`(`eventId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `event_contact`
--

CREATE TABLE IF NOT EXISTS `event_contact` (
  `eventId` bigint NOT NULL,
  `contact` longtext,
  FOREIGN KEY (`eventId`) REFERENCES `event`(`eventId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `event_exDate`
--

CREATE TABLE IF NOT EXISTS `event_exDate` (
  `eventId` bigint NOT NULL,
  `exDate` datetime default NULL,
  FOREIGN KEY (`eventId`) REFERENCES `event`(`eventId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `event_exRule`
--

CREATE TABLE IF NOT EXISTS `event_exRule` (
  `eventId` bigint NOT NULL,
  `exRule` longtext,
  FOREIGN KEY (`eventId`) REFERENCES `event`(`eventId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `event_rDate`
--

CREATE TABLE IF NOT EXISTS `event_rDate` (
  `eventId` bigint NOT NULL,
  `rDate` datetime default NULL,
  FOREIGN KEY (`eventId`) REFERENCES `event`(`eventId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `event_related`
--

CREATE TABLE IF NOT EXISTS `event_related` (
  `eventId` bigint NOT NULL,
  `related` longtext,
  FOREIGN KEY (`eventId`) REFERENCES `event`(`eventId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `event_resources`
--

CREATE TABLE IF NOT EXISTS `event_resources` (
  `eventId` bigint NOT NULL,
  `resources` longtext,
  FOREIGN KEY (`eventId`) REFERENCES `event`(`eventId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `event_rRule`
--

CREATE TABLE IF NOT EXISTS `event_rRule` (
  `eventId` bigint NOT NULL,
  `rRule` longtext,
  FOREIGN KEY (`eventId`) REFERENCES `event`(`eventId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `event_rStatus`
--

CREATE TABLE IF NOT EXISTS `event_rStatus` (
  `eventId` bigint NOT NULL,
  `rStatus` longtext,
  FOREIGN KEY (`eventId`) REFERENCES `event`(`eventId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `event_xProps`
--

CREATE TABLE IF NOT EXISTS `event_xProps` (
  `eventId` bigint NOT NULL,
  `xprop_name` varchar(255) NOT NULL,
  `xprop_value` longtext,
  PRIMARY KEY (`eventId`,`xprop_name`),
  FOREIGN KEY (`eventId`) REFERENCES `event`(`eventId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `settings`
--

CREATE TABLE IF NOT EXISTS `settings` (
  `settingsId` bigint NOT NULL auto_increment,
  `lastUpdateTime` datetime NOT NULL default 0;
  PRIMARY KEY (`settingsId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `options`
--

CREATE TABLE IF NOT EXISTS `options` (
  `optionId` bigint NOT NULL auto_increment,
  `lastUpdateTime` datetime NOT NULL default 0;
  `name` varchar(255) NOT NULL,
  `value`tinyblob default NULL,
  `settingsId` bigint NOT NULL,
  `settingsId2` bigint NOT NULL,
  PRIMARY KEY (`optionId`),
  FOREIGN KEY (`settingsId`) REFERENCES `settings`(`settingsId`),
  FOREIGN KEY (`settingsId2`) REFERENCES `settings`(`settingsId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `plugin_settings`
--

CREATE TABLE IF NOT EXISTS `plugin_settings` (
  `settingsId` bigint NOT NULL,
  `pluginClass` longtext default NULL,
  PRIMARY KEY (`settingsId`),
  FOREIGN KEY (`settingsId`) REFERENCES `settings`(`settingsId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `user_plugin_settings`
--

CREATE TABLE IF NOT EXISTS `user_plugin_settings` (
  `settingsId` bigint NOT NULL,
  `userId` varchar(255) NOT NULL,
  `pluginClass` longtext default NULL,
  PRIMARY KEY (`settingsId`),
  FOREIGN KEY (`userId`) REFERENCES `wcaluser`(`userId`),
  FOREIGN KEY (`settingsId`) REFERENCES `settings`(`settingsId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `user_settings`
--

CREATE TABLE IF NOT EXISTS `user_settings` (
  `settingsId` bigint NOT NULL,
  `userId` varchar(255) NOT NULL,
  `defaultCalendarView` integer default NULL,
  `firstDayOfWeek` integer default NULL,
  `numberOfAgendaDays` integer default NULL,
  `dateFormat` varchar(255) default NULL,
  `timeFormat` varchar(255) default NULL,
  PRIMARY KEY (`settingsId`),
  FOREIGN KEY (`userId`) REFERENCES `wcaluser`(`userId`),
  FOREIGN KEY (`settingsId`) REFERENCES `settings`(`settingsId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
