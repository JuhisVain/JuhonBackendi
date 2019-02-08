# JuhonBackendi

### A rudimentary Java http server

Requires superuser rights to run.
java -jar jubatest.jar

Can be tested through browser with: 
localhost/weather?q=HELSINKI

also works with queries 'ESPOO' and 'ROVANIEMI' and whatever else you place in data/ folder using the same form.

Tested to work with openjdk 1.8 on Linux

Only handles GET requests, Access-Control-Allow-Origin set to *.

reactui/reactui has an ugly react thing that tries to connect to localhost.
