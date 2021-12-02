package com.shattered.database;


import com.shattered.database.mysql.MySQLDatabase;

/**
 * @author JTlr Frost - 9/17/2018 - 4:04 PM
 */
public interface DatabaseConfiguration {

    /**
     * MYSQL Database settings.
     */
    MySQLDatabase[] DATABASES = {
            new MySQLDatabase("grizzly", "grizzlyent.cpde7dtfjvvy.us-west-2.rds.amazonaws.com", "admin", "!003786dc"),
            new MySQLDatabase("shatteredrelics", "shatteredrelics.cpde7dtfjvvy.us-west-2.rds.amazonaws.com", "admin", "!003786dc"),
    };


}
