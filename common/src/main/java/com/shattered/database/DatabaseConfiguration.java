package com.shattered.database;


import com.shattered.database.mysql.MySQLDatabase;

/**
 * @author JTlr Frost - 9/17/2018 - 4:04 PM
 */
public interface DatabaseConfiguration {

    /**
     * MYSQL Database settings.
     */
    MySQLDatabase[] GRIZZLY_DATABASES = {
            new MySQLDatabase("grizzly"),
    };

    MySQLDatabase[] SHATTERED_DATABASES = {
            new MySQLDatabase("shatteredrelics"),
    };



}
