To start with webical do the following:

1. Check the versionnumber in src/main/webapp/WEB-INF/applicationContext-dev.xml

2. Run maven: mvn -Dwtpversion1.0 eclipse:eclipse

3. Install the aspectJ developer tools from http://www.eclipse.org/ajdt/ and configure aspectj for the project (right-click -> AspectJ Tools -> Configure...).

4. Set up the servlet container

If using Jetty:

- Check you have a database that matches the database in src/test/resources/jetty.xml
- Run src/test/org/webical/web/StartWebApplication as a normal java application

If using tomcat

- Install wtp from http://www.eclipse.org/webtools/
- Check you have a database that matches the database in src/main/webapp/META-INF/context.xml
- Edit src/main/webapp/web.xml and alter the webdav servlet's rootPath parameter to use the full path
- Run webical on a sever configuration