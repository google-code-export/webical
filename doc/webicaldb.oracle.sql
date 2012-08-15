-- ***************************************************** --
--                                                       --
-- Setup for the Webical database for Oracle             --
--                                                       --
-- ***************************************************** --

--
-- Drop old DB tables in the right sequence
--

use WebiCal ;

Drop Table event_xProps ;
Drop Table event_rStatus ;
Drop Table event_rRule ;
Drop Table event_resources ;
Drop Table event_related ;
Drop Table event_rDate ;
Drop Table event_exRule ;
Drop Table event_exDate ;
Drop Table event_contact ;
Drop Table event_comment ;
Drop Table event_categories ;
Drop Table event_attendee ;
Drop Table event_attach ;
Drop Table event ;
Drop Table calendar ;

Drop Table user_settings ;
Drop Table user_plugin_settings ;
Drop Table plugin_settings ;
Drop Table options ;
Drop Table settings ;

Drop Table appl_sett_resource_paths ;
Drop Table appl_sett_plugin_paths;
Drop Table application_settings ;

Drop Table wcaluser ;

-- --------------------------------------------------------

--
-- Table structure for table application_settings
--

CREATE TABLE application_settings (
  applicationSettingsId NUMBER (19,0) NOT NULL,   -- auto_increment
  customPageTitle VARCHAR2(1000),
  pluginWorkPath VARCHAR2(3000),
  pluginPackageExtension VARCHAR2(1000),
  calendarRefreshTimeMs NUMBER (10,0) DEFAULT NULL,
  pluginCleanupEnabled NUMBER(1) DEFAULT 0,
  configurationUsername VARCHAR2(1000),
  configurationPassword VARCHAR2(1000),
  PRIMARY KEY (applicationSettingsId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table application_settings_plugin_paths
--

CREATE TABLE appl_sett_plugin_paths (
  applicationSettingsId NUMBER (19,0) NOT NULL,
  plugin_path VARCHAR2(3000),
  CONSTRAINT FK_appl_sett_id_plugin_paths FOREIGN KEY (applicationSettingsId) REFERENCES application_settings(applicationSettingsId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table application_settings_resource_paths
--

CREATE TABLE appl_sett_resource_paths (
  applicationSettingsId NUMBER (19,0) NOT NULL,
  resource_path VARCHAR2(3000),
  CONSTRAINT FK_appl_sett_id_resourc_paths FOREIGN KEY (applicationSettingsId) REFERENCES application_settings(applicationSettingsId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table wcaluser
--

CREATE TABLE wcaluser (
  userId VARCHAR2(255) NOT NULL,
  firstName VARCHAR2(1000),
  lastNamePrefix VARCHAR2(1000),
  lastName VARCHAR2(1000),
  birthDate DATE DEFAULT NULL,
  PRIMARY KEY (userId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table calendar
--

CREATE TABLE calendar (
  calendarId NUMBER (19,0) NOT NULL,   -- auto_increment
  name VARCHAR2(1000) NOT NULL,
  type VARCHAR2(1000) NOT NULL,
  url VARCHAR2(3000) NOT NULL,
  username VARCHAR2(1000),
  password VARCHAR2(1000),
  visible NUMBER(1) DEFAULT 1,
  offSetFrom NUMBER (10,0) DEFAULT 0,
  offSetTo NUMBER (10,0) DEFAULT 0,
  lastRefreshTimeStamp NUMBER (19,0) DEFAULT NULL,
  userId VARCHAR2(255) NOT NULL,
  PRIMARY KEY (calendarId),
  CONSTRAINT FK_calendar_userId FOREIGN KEY (userId) REFERENCES wcaluser(userId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table event
--

CREATE TABLE event (
  eventId NUMBER (19,0) NOT NULL,   -- auto_increment
  calendarId NUMBER (19,0) NOT NULL,
  clazz VARCHAR2(1000),
  description VARCHAR2(3000),
  geo VARCHAR2(1000),
  location VARCHAR2(1000),
  organizer VARCHAR2(1000),
  status VARCHAR2(1000),
  summary VARCHAR2(1000),
  transp VARCHAR2(1000),  
  wuid VARCHAR2(1000),         -- was: uid
  url VARCHAR2(3000),
  allDay NUMBER(1) DEFAULT NULL,
  created TIMESTAMP DEFAULT NULL,
  dtStart TIMESTAMP DEFAULT NULL,
  lastMod TIMESTAMP DEFAULT NULL,
  dtStamp TIMESTAMP DEFAULT NULL,
  seq NUMBER (10,0) DEFAULT NULL,
  dtEnd TIMESTAMP DEFAULT NULL,
  duration VARCHAR2(255) DEFAULT NULL,
  PRIMARY KEY (eventId),
  CONSTRAINT FK_event_calendarId FOREIGN KEY (calendarId) REFERENCES calendar(calendarId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table event_attach
--

CREATE TABLE event_attach (
  eventId NUMBER (19,0) NOT NULL,
  attach VARCHAR2(1000),
  CONSTRAINT FK_event_attach_eventId FOREIGN KEY (eventId) REFERENCES event(eventId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table event_attendee
--

CREATE TABLE event_attendee (
  eventId NUMBER (19,0) NOT NULL,
  attendee VARCHAR2(1000),
  CONSTRAINT FK_event_attendee_eventId FOREIGN KEY (eventId) REFERENCES event(eventId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table event_categories
--

CREATE TABLE event_categories (
  eventId NUMBER (19,0) NOT NULL,
  categories VARCHAR2(1000),
  CONSTRAINT FK_event_categories_eventId FOREIGN KEY (eventId) REFERENCES event(eventId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table event_comment
--

CREATE TABLE event_comment (
  eventId NUMBER (19,0) NOT NULL,
  comments VARCHAR2(3000),         -- was: comment
  CONSTRAINT FK_event_comment_eventId FOREIGN KEY (eventId) REFERENCES event(eventId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table event_contact
--

CREATE TABLE  event_contact (
  eventId NUMBER (19,0) NOT NULL,
  contact VARCHAR2(1000),
  CONSTRAINT FK_event_contact_eventId FOREIGN KEY (eventId) REFERENCES event(eventId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table event_exDate
--

CREATE TABLE event_exDate (
  eventId NUMBER (19,0) NOT NULL,
  exDate TIMESTAMP DEFAULT NULL,
  CONSTRAINT FK_event_exDate_eventId FOREIGN KEY (eventId) REFERENCES event(eventId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table event_exRule
--

CREATE TABLE event_exRule (
  eventId NUMBER (19,0) NOT NULL,
  exRule VARCHAR2(1000),
  CONSTRAINT FK_event_exRule_eventId FOREIGN KEY (eventId) REFERENCES event(eventId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table event_rDate
--

CREATE TABLE event_rDate (
  eventId NUMBER (19,0) NOT NULL,
  rDate TIMESTAMP DEFAULT NULL,
  CONSTRAINT FK_event_rDate_eventId FOREIGN KEY (eventId) REFERENCES event(eventId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table event_related
--

CREATE TABLE event_related (
  eventId NUMBER (19,0) NOT NULL,
  related VARCHAR2(1000),
  CONSTRAINT FK_event_related_eventId FOREIGN KEY (eventId) REFERENCES event(eventId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table event_resources
--

CREATE TABLE event_resources (
  eventId NUMBER (19,0) NOT NULL,
  resources VARCHAR2(1000),
  CONSTRAINT FK_event_resources_eventId FOREIGN KEY (eventId) REFERENCES event(eventId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table event_rRule
--

CREATE TABLE event_rRule (
  eventId NUMBER (19,0) NOT NULL,
  rRule VARCHAR2(1000),
  CONSTRAINT FK_event_rRule_eventId FOREIGN KEY (eventId) REFERENCES event(eventId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table event_rStatus
--

CREATE TABLE event_rStatus (
  eventId NUMBER (19,0) NOT NULL,
  rStatus VARCHAR2(1000),
  CONSTRAINT FK_event_rStatus_eventId FOREIGN KEY (eventId) REFERENCES event(eventId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table event_xProps
--

CREATE TABLE event_xProps (
  eventId NUMBER (19,0) NOT NULL,
  xprop_name VARCHAR2(255) NOT NULL,
  xprop_value VARCHAR2(1000),
  PRIMARY KEY (eventId,xprop_name),
  CONSTRAINT FK_event_xProps_eventId FOREIGN KEY (eventId) REFERENCES event(eventId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table settings
--

CREATE TABLE settings (
  settingsId NUMBER (19,0) NOT NULL,   -- auto_increment
  PRIMARY KEY (settingsId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table options
--

CREATE TABLE options (
  optionId NUMBER (19,0) NOT NULL,   -- auto_increment
  name VARCHAR2(255) NOT NULL,
  value RAW(255) DEFAULT NULL,
  settingsId NUMBER (19,0) NOT NULL,
  settingsId2 NUMBER (19,0) NOT NULL,
  PRIMARY KEY (optionId),
  CONSTRAINT FK_options_settingsId FOREIGN KEY (settingsId) REFERENCES settings(settingsId),
  CONSTRAINT FK_options_settingsId2 FOREIGN KEY (settingsId2) REFERENCES settings(settingsId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table plugin_settings
--

CREATE TABLE plugin_settings (
  settingsId NUMBER (19,0) NOT NULL,
  pluginClass VARCHAR2(3000) DEFAULT NULL,
  PRIMARY KEY (settingsId),
  CONSTRAINT FK_plugin_sett_settingsId FOREIGN KEY (settingsId) REFERENCES settings(settingsId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table user_plugin_settings
--

CREATE TABLE user_plugin_settings (
  settingsId NUMBER (19,0) NOT NULL,
  userId VARCHAR2(255)  NOT NULL,
  pluginClass VARCHAR2(3000) DEFAULT NULL,
  PRIMARY KEY (settingsId),
  CONSTRAINT FK_usr_plugin_sett_settingsId FOREIGN KEY (settingsId) REFERENCES settings(settingsId),
  CONSTRAINT FK_usr_plugin_sett_userId FOREIGN KEY (userId) REFERENCES wcaluser(userId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table user_settings
--

CREATE TABLE user_settings (
  settingsId NUMBER (19,0) NOT NULL,
  userId VARCHAR2(255) NOT NULL,
  defaultCalendarView NUMBER (10,0) DEFAULT NULL,
  firstDayOfWeek NUMBER (10,0) DEFAULT NULL,
  numberOfAgendaDays NUMBER (10,0) DEFAULT NULL,
  dateFormat VARCHAR2(255) DEFAULT NULL,
  timeFormat VARCHAR2(255) DEFAULT NULL,
  PRIMARY KEY (settingsId),
  CONSTRAINT FK_user_settings_settingsId FOREIGN KEY (settingsId) REFERENCES settings(settingsId),
  CONSTRAINT FK_user_settings_userId FOREIGN KEY (userId) REFERENCES wcaluser(userId)
) ;
