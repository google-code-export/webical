-- Clean Webical Demo data 

-- --------------------------------------------------------

-- Change the admin password to something secret
UPDATE `application_settings` SET configurationPassword = 'noneofyourbusiness' WHERE `applicationSettingsId` = '1';
