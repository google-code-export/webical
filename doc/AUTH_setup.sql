-- *********************************************** --
--                                                 --
-- Setup Webical with authentication database      --
--                                                 --
-- *********************************************** --

use webical;

-- create the table to hold the auth users --
CREATE TABLE `_auth_user` ( 
`username` varchar(250) NOT NULL, 
`userpass` varchar(250) NOT NULL, 
PRIMARY KEY  (`username`)  
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- create the table to hold the auth user roles --
CREATE TABLE `_auth_userrole` ( 
`username` varchar(250) NOT NULL, 
`role` varchar(250) NOT NULL, 
PRIMARY KEY  (`username`,`role`)  
) ENGINE=MyISAM DEFAULT CHARSET=latin1;


-- **************************************** --
-- Change username 'webical' and password 'webical' to use different 
-- credentials.
-- 
-- note: if you change the username, don't forget to change the column 
-- username in the _auth_userrole table
-- **************************************** --

-- Create a new user                        --
INSERT INTO _auth_user (username, userpass) VALUES ("webical", "webical");

-- ****************************************
-- note: if you change the userrole in something else than webicaluser,
-- make sure you change the <role-name> in tomcat/webapps/webical/WEB-INF/web.xml 
-- ****************************************

-- Create a new userrole
INSERT INTO _auth_userrole (username, role) VALUES ("webical", "webicaluser");

