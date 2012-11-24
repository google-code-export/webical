-- ***************************************************** --
--                                                       --
-- Setup for the Webical database for Apache Derby       --
--                                                       --
-- ***************************************************** --

--
-- Drop old DB tables in the right sequence
--

Set Schema WebiCal ;

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
Drop Table appl_sett_plugin_paths ;
Drop Table application_settings ;

Drop Table wcaluser ;

-- --------------------------------------------------------

--
-- Tables for authentication
--

-- CREATE TABLE users (
--   userName varchar(250) Not Null,
--   userPass varchar(250) Not Null
-- ) ;

-- CREATE TABLE userrole (
--   userName varchar(250) Not Null,
--   role varchar(250) Not Null
-- ) ;

-- --------------------------------------------------------

--
-- Table structure for table application_settings
--

CREATE TABLE application_settings (
  applicationSettingsId BIGINT NOT NULL Generated By Default As Identity,
  lastUpdateTime TIMESTAMP NOT NULL DEFAULT 0;
  customPageTitle VARCHAR(1000),
  pluginWorkPath VARCHAR(3000),
  pluginPackageExtension VARCHAR(1000),
  calendarRefreshTimeMs INTEGER DEFAULT NULL,
  pluginCleanupEnabled NUMERIC(1) DEFAULT 0,
  configurationUsername VARCHAR(1000),
  configurationPassword VARCHAR(1000),
  PRIMARY KEY (applicationSettingsId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table application_settings_plugin_paths
--

CREATE TABLE appl_sett_plugin_paths (
  applicationSettingsId BIGINT NOT NULL,
  plugin_path VARCHAR(3000),
  CONSTRAINT FK_appl_sett_id_plugin_paths FOREIGN KEY (applicationSettingsId) REFERENCES application_settings(applicationSettingsId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table application_settings_resource_paths
--

CREATE TABLE appl_sett_resource_paths (
  applicationSettingsId BIGINT NOT NULL,
  resource_path VARCHAR(3000),
  CONSTRAINT FK_appl_sett_id_resourc_paths FOREIGN KEY (applicationSettingsId) REFERENCES application_settings(applicationSettingsId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table wcaluser
--

CREATE TABLE wcaluser (
  userId VARCHAR(255) NOT NULL,
  lastUpdateTime TIMESTAMP NOT NULL DEFAULT 0;
  firstName VARCHAR(1000),
  lastNamePrefix VARCHAR(1000),
  lastName VARCHAR(1000),
  birthDate DATE DEFAULT NULL,
  PRIMARY KEY (userId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table calendar
--

CREATE TABLE calendar (
  calendarId BIGINT NOT NULL Generated By Default As Identity,
  lastUpdateTime TIMESTAMP NOT NULL DEFAULT 0;
  name VARCHAR(1000) NOT NULL,
  type VARCHAR(1000) NOT NULL,
  url VARCHAR(3000) NOT NULL,
  username VARCHAR(1000),
  password VARCHAR(1000),
  visible NUMERIC(1) DEFAULT 1,
  offSetFrom INTEGER DEFAULT 0,
  offSetTo INTEGER DEFAULT 0,
  lastRefreshTimeStamp BIGINT DEFAULT NULL,
  userId VARCHAR(255) NOT NULL,
  PRIMARY KEY (calendarId),
  CONSTRAINT FK_calendar_userId FOREIGN KEY (userId) REFERENCES wcaluser(userId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table event
--

CREATE TABLE event (
  eventId BIGINT NOT NULL Generated By Default As Identity,
  lastUpdateTime TIMESTAMP NOT NULL DEFAULT 0;
  calendarId BIGINT NOT NULL,
  clazz VARCHAR(1000),
  description VARCHAR(3000),
  geo VARCHAR(1000),
  location VARCHAR(1000),
  organizer VARCHAR(1000),
  status VARCHAR(1000),
  summary VARCHAR(1000),
  transp VARCHAR(1000),  
  wuid VARCHAR(1000),         -- was: uid
  url VARCHAR(3000),
  allDay NUMERIC(1) DEFAULT NULL,
  created TIMESTAMP DEFAULT NULL,
  dtStart TIMESTAMP DEFAULT NULL,
  lastMod TIMESTAMP DEFAULT NULL,
  dtStamp TIMESTAMP DEFAULT NULL,
  seq INTEGER DEFAULT NULL,
  dtEnd TIMESTAMP DEFAULT NULL,
  duration VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (eventId),
  CONSTRAINT FK_event_calendarId FOREIGN KEY (calendarId) REFERENCES calendar(calendarId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table event_attach
--

CREATE TABLE event_attach (
  eventId BIGINT NOT NULL,
  attach VARCHAR(1000),
  CONSTRAINT FK_event_attach_eventId FOREIGN KEY (eventId) REFERENCES event(eventId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table event_attendee
--

CREATE TABLE event_attendee (
  eventId BIGINT NOT NULL,
  attendee VARCHAR(1000),
  CONSTRAINT FK_event_attendee_eventId FOREIGN KEY (eventId) REFERENCES event(eventId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table event_categories
--

CREATE TABLE event_categories (
  eventId BIGINT NOT NULL,
  categories VARCHAR(1000),
  CONSTRAINT FK_event_categories_eventId FOREIGN KEY (eventId) REFERENCES event(eventId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table event_comment
--

CREATE TABLE event_comment (
  eventId BIGINT NOT NULL,
  comments VARCHAR(3000),         -- was: comment
  CONSTRAINT FK_event_comment_eventId FOREIGN KEY (eventId) REFERENCES event(eventId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table event_contact
--

CREATE TABLE event_contact (
  eventId BIGINT NOT NULL,
  contact VARCHAR(1000),
  CONSTRAINT FK_event_contact_eventId FOREIGN KEY (eventId) REFERENCES event(eventId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table event_exDate
--

CREATE TABLE event_exDate (
  eventId BIGINT NOT NULL,
  exDate TIMESTAMP DEFAULT NULL,
  CONSTRAINT FK_event_exDate_eventId FOREIGN KEY (eventId) REFERENCES event(eventId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table event_exRule
--

CREATE TABLE event_exRule (
  eventId BIGINT NOT NULL,
  exRule VARCHAR(1000),
  CONSTRAINT FK_event_exRule_eventId FOREIGN KEY (eventId) REFERENCES event(eventId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table event_rDate
--

CREATE TABLE event_rDate (
  eventId BIGINT NOT NULL,
  rDate TIMESTAMP DEFAULT NULL,
  CONSTRAINT FK_event_rDate_eventId FOREIGN KEY (eventId) REFERENCES event(eventId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table event_related
--

CREATE TABLE event_related (
  eventId BIGINT NOT NULL,
  related VARCHAR(1000),
  CONSTRAINT FK_event_related_eventId FOREIGN KEY (eventId) REFERENCES event(eventId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table event_resources
--

CREATE TABLE event_resources (
  eventId BIGINT NOT NULL,
  resources VARCHAR(1000),
  CONSTRAINT FK_event_resources_eventId FOREIGN KEY (eventId) REFERENCES event(eventId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table event_rRule
--

CREATE TABLE event_rRule (
  eventId BIGINT NOT NULL,
  rRule VARCHAR(1000),
  CONSTRAINT FK_event_rRule_eventId FOREIGN KEY (eventId) REFERENCES event(eventId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table event_rStatus
--

CREATE TABLE event_rStatus (
  eventId BIGINT NOT NULL,
  rStatus VARCHAR(1000),
  CONSTRAINT FK_event_rStatus_eventId FOREIGN KEY (eventId) REFERENCES event(eventId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table event_xProps
--

CREATE TABLE event_xProps (
  eventId BIGINT NOT NULL,
  xprop_name VARCHAR(255) NOT NULL,
  xprop_value VARCHAR(1000),
  PRIMARY KEY (eventId,xprop_name),
  CONSTRAINT FK_event_xProps_eventId FOREIGN KEY (eventId) REFERENCES event(eventId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table settings
--

CREATE TABLE settings (
  settingsId BIGINT NOT NULL Generated By Default As Identity,
  lastUpdateTime TIMESTAMP NOT NULL DEFAULT 0;
  PRIMARY KEY (settingsId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table options
--

CREATE TABLE options (
  optionId BIGINT NOT NULL Generated By Default As Identity,
  lastUpdateTime TIMESTAMP NOT NULL DEFAULT 0;
  name VARCHAR(255) NOT NULL,
  value VARCHAR(255) For Bit Data DEFAULT NULL,
  settingsId BIGINT NOT NULL,
  settingsId2 BIGINT NOT NULL,
  PRIMARY KEY (optionId),
  CONSTRAINT FK_options_settingsId FOREIGN KEY (settingsId) REFERENCES settings(settingsId),
  CONSTRAINT FK_options_settingsId2 FOREIGN KEY (settingsId2) REFERENCES settings(settingsId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table plugin_settings
--

CREATE TABLE plugin_settings (
  settingsId BIGINT NOT NULL,
  pluginClass VARCHAR(3000) DEFAULT NULL,
  PRIMARY KEY (settingsId),
  CONSTRAINT FK_plugin_sett_settingsId FOREIGN KEY (settingsId) REFERENCES settings(settingsId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table user_plugin_settings
--

CREATE TABLE user_plugin_settings (
  settingsId BIGINT NOT NULL,
  userId VARCHAR(255) NOT NULL,
  pluginClass VARCHAR(3000) DEFAULT NULL,
  PRIMARY KEY (settingsId),
  CONSTRAINT FK_usr_plugin_sett_settingsId FOREIGN KEY (settingsId) REFERENCES settings(settingsId),
  CONSTRAINT FK_usr_plugin_sett_userId FOREIGN KEY (userId) REFERENCES wcaluser(userId)
) ;

-- --------------------------------------------------------

--
-- Table structure for table user_settings
--

CREATE TABLE user_settings (
  settingsId BIGINT NOT NULL,
  userId VARCHAR(255) NOT NULL,
  defaultCalendarView INTEGER DEFAULT NULL,
  firstDayOfWeek INTEGER DEFAULT NULL,
  numberOfAgendaDays INTEGER DEFAULT NULL,
  dateFormat VARCHAR(255) DEFAULT NULL,
  timeFormat VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (settingsId),
  CONSTRAINT FK_user_settings_settingsId FOREIGN KEY (settingsId) REFERENCES settings(settingsId),
  CONSTRAINT FK_user_settings_userId FOREIGN KEY (userId) REFERENCES wcaluser(userId)
) ;


Grant All Privileges On Table user_settings To webical ;
Grant All Privileges On Table user_plugin_settings To webical ;
Grant All Privileges On Table plugin_settings To webical ;
Grant All Privileges On Table options To webical ;
Grant All Privileges On Table settings To webical ;
Grant All Privileges On Table event_xProps To webical ;
Grant All Privileges On Table event_rStatus To webical ;
Grant All Privileges On Table event_rRule To webical ;
Grant All Privileges On Table event_resources To webical ;
Grant All Privileges On Table event_related To webical ;
Grant All Privileges On Table event_rDate To webical ;
Grant All Privileges On Table event_exRule To webical ;
Grant All Privileges On Table event_exDate To webical ;
Grant All Privileges On Table event_contact To webical ;
Grant All Privileges On Table event_comment To webical ;
Grant All Privileges On Table event_categories To webical ;
Grant All Privileges On Table event_attendee To webical ;
Grant All Privileges On Table event_attach To webical ;
Grant All Privileges On Table event To webical ;
Grant All Privileges On Table calendar To webical ;
Grant All Privileges On Table wcaluser To webical ;
Grant All Privileges On Table appl_sett_resource_paths To webical ;
Grant All Privileges On Table appl_sett_plugin_paths To webical ;
Grant All Privileges On Table application_settings To webical ;
--Grant All Privileges On Table users To webical ;
--Grant All Privileges On Table userrole To webical ;
