drop user 'realmlibros'@'localhost';
create user 'realmlibros'@'localhost' identified by 'realmlibros';
grant all privileges on realmlibrosdb.* to 'realmlibros'@'localhost';
flush privileges;
