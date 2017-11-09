package com.asymmetrik.ocr.parser.impl;

import com.asymmetrik.ocr.parser.Configuration;
import org.apache.log4j.Logger;

/**
 * Factory class for instantiating a BusinessCardParser.
 */
public class BusinessCardParserFactory
{
    private static final Logger log = Logger.getLogger( BusinessCardParserFactory.class );

    /**
     * Classpath reference for the BusinessCardParser that will be instantiated.
     * <p>
     * This can be changed by setting the businesscardparser.type property in the config.properties file.
     */
    private static String PARSER_CLASSPATH = Configuration.getProperty( "businesscardparser.type",
        "com.asymmetrik.ocr.parser.impl.DefaultBusinessCardParser" );

    /**
     * Instantiates a new BusinessCardParser based on the businesscardparser.type property.
     * <p>
     * If the property is not found, returns an instance of DefaultBusinessCardParser.
     *
     * @return an instance of BusinessCardParser
     */
    public static BusinessCardParser create( )
    {
        try
        {
            return ( BusinessCardParser ) Class.forName( PARSER_CLASSPATH ).newInstance( );
        } catch ( ClassNotFoundException | IllegalAccessException | InstantiationException e )
        {
            log.error( "Error instantiating BusinessCardParser.", e );
        }

        return new DefaultBusinessCardParser( );
    }
}
