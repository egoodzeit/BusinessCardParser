package com.asymmetrik.ocr.parser;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

/**
 * Class that handles program configuration.
 */
public final class Configuration
{
    private static final Logger log = Logger.getLogger( Configuration.class );

    /**
     * Holds properties found in config.properties file.
     */
    private static Properties properties = new Properties( );

    static
    {
        try
        {
            properties.load( Thread.currentThread( ).getContextClassLoader( ).getResourceAsStream( "config.properties" ) );
        } catch ( IOException e )
        {
            log.error( "Unable to load configuration file: config.properties", e );
        }
    }

    /**
     * Returns the value of the property found in the configuration file.  If no property is found,
     * returns the default value.
     *
     * @param property     string key of the property.
     * @param defaultValue default value for the property.
     * @return string value of the property, or the default value if not found.
     */
    public static String getProperty( String property, String defaultValue )
    {
        return properties.getProperty( property, defaultValue );
    }
}
