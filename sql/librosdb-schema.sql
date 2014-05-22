DROP DATABASE IF EXISTS librosdb;
CREATE DATABASE librosdb;
USE librosdb;
CREATE TABLE Users (
  Username VARCHAR(255) NOT NULL,
  Name VARCHAR(255) NOT NULL,
  Email VARCHAR(255),
  PRIMARY KEY (Username)   
);

CREATE TABLE Libros (
  Id INTEGER NOT NULL AUTO_INCREMENT,
  Titulo VARCHAR(255) NOT NULL ,
  Autor VARCHAR(255) NOT NULL ,
  Idioma VARCHAR(255) NOT NULL ,
  Edicion VARCHAR(255) NOT NULL ,
  Fecha_Edicion DATE,
  Fecha_Impresion DATE,
  Editorial VARCHAR(255) NOT NULL ,
  Last_modified timestamp,
  PRIMARY KEY (Id) 
);

CREATE TABLE Opinion (
  Id INTEGER NOT NULL AUTO_INCREMENT,
  Username VARCHAR(255) NOT NULL ,
  Fecha DATE ,
  Contenido VARCHAR(500) NOT NULL ,
  Id_libro INTEGER NOT NULL ,
  Last_modified timestamp,
  PRIMARY KEY (Id),
  FOREIGN KEY (Username) REFERENCES Users(Username),
  FOREIGN KEY (Id_libro) REFERENCES Libros(Id)
);