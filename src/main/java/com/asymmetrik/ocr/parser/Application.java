package com.asymmetrik.ocr.parser;

import com.asymmetrik.ocr.parser.impl.BusinessCardParser;
import com.asymmetrik.ocr.parser.impl.BusinessCardParserFactory;
import com.asymmetrik.ocr.parser.impl.ContactInfo;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Runnable command line program for the BusinessCardParser
 * <p>
 * Accepts a file name as input with an optional output file for results.
 * Returns contact info results in console.
 */
public final class Application
{
    private static final Logger log = Logger.getLogger( Application.class );

    /**
     * Main entry point into application.
     *
     * @param args command line arguments.
     */
    public static void main( String[] args )
    {
        BusinessCardParser businessCardParser = BusinessCardParserFactory.create( );

        handleArgs( args, businessCardParser );
    }

    /**
     * Parses command line arguments.
     * Parses text file if one is specified.
     *
     * @param args               command line arguments
     * @param businessCardParser BusinessCardParser to be used for text parsing.
     */
    private static void handleArgs( String[] args, BusinessCardParser businessCardParser )
    {

        if( args.length > 0 )
        {
            List<String> argumentList = Arrays.asList( args );
            String fileName = argumentList.get( 0 );

            try
            {
                ContactInfo contactInfo = loadAndParseFile( fileName, businessCardParser );
                log.info( contactInfo );

                int outputIndex = argumentList.indexOf( "-o" );

                if( outputIndex > -1 )
                {
                    if( outputIndex + 1 < argumentList.size( ) )
                    {
                        writeResultsToDisk( contactInfo, argumentList.get( outputIndex + 1 ) );
                    } else
                    {
                        log.error( "No output file path specified, please specify a file path after -o." );
                    }
                }

            } catch ( IOException e )
            {
                log.error( "Unable to load file: " + fileName );
            }
        } else
        {
            System.out.println( "Please specify the path to an input file as the first argument." );
        }
    }

    /**
     * @param fileName
     * @param businessCardParser
     * @return
     * @throws IOException
     */
    private static ContactInfo loadAndParseFile( String fileName, BusinessCardParser businessCardParser ) throws IOException
    {
        log.info( "Parsing business card text from file: " + fileName );
        String document = Files.readAllLines( Paths.get( fileName ) ).stream( ).collect( Collectors.joining( "\n" ) );
        ContactInfo contactInfo = businessCardParser.getContactInfo( document );

        return contactInfo;
    }

    /**
     * Attempts to write the results of the parser to disk.
     * The method will create the file if it doesn't already exist.
     *
     * @param results        ContactInfo results object
     * @param outputFileName The path to the ouput file.
     */
    private static void writeResultsToDisk( ContactInfo results, String outputFileName )
    {
        File file = new File( outputFileName );
        BufferedWriter writer = null;

        try
        {
            file.createNewFile( );

            writer = new BufferedWriter( new FileWriter( file ) );

            writer.write( results.toString( ) );

            log.info( "Results written to " + outputFileName );
        } catch ( IOException e )
        {
            log.error( "Unable to write to file.", e );
        } finally
        {
            try
            {
                if( writer != null )
                {
                    writer.close( );
                }
            } catch ( Exception e )
            {
                log.debug( "Error closing writer." );
            }
        }
    }
}
