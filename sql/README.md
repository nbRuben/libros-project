Beeter - SQL scripts

This folder contains the scripts to create the user associated to the beeter database (user: libros, default password libros) and the schema of the database. It also contains script to configure the Tomcat realm.
Installation

    Connect as root to mysql, execute script librosdb-user.sql and realmlibrosdb-user.sql, then exit.
    Connect as realmlibros (password: realmlibros) to mysql, execute script librosdb-schema.sql, then exit.
    Connect as libros (password: libros) to mysql, execute script librosdb-schema.sql, then exit.
