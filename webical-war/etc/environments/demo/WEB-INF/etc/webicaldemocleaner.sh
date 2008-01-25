#!/bin/bash
###########################################
### User variables, change where needed ###
###########################################
db=org_webical_demo
db_user=org_webical_demo
db_pass=org_webical_demo
tomcat_user=
tomcat_pass=
url=http://demo.webical.org
context_path=/
insert_script=/var/sites/org.webical.demo/etc/defaultwebicaldemodata.sql
demo_ical_file=/var/sites/org.webical.demo/www/ROOT/dav/webicaldemo.ics
clean_ical_file=/var/sites/org.webical.demo/etc/webicaldemo.ics

############################################

echo "demo.webical.org cleaning time!"
echo "==============================="

echo "copying the clean ics file over the demo file"
/bin/cp $clean_ical_file $demo_ical_file

echo "reloading context"
result=`/usr/bin/wget -q --user=$tomcat_user --password=$tomcat_pass $url/manager/reload?path=$context_path -O -`
echo ${result}
if [[ "${result:0:4}" == "FAIL" ]] ; then
	echo "context not started yet, starting up"
	/usr/bin/wget -q --user=$tomcat_user --password=$tomcat_pass $url/manager/start?path=$context_path -O -
fi	

echo "inserting private data"
/usr/bin/mysql -u$db_user -p$db_pass $db < $insert_script

echo "=================================="
echo "demo.webical.org cleaning is done!"
