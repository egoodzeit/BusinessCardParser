package com.asymmetrik.ocr.parser;

import com.asymmetrik.ocr.parser.impl.BusinessCardParser;
import com.asymmetrik.ocr.parser.impl.BusinessCardParserFactory;
import com.asymmetrik.ocr.parser.impl.ContactInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Test class that contains unit tests for the ContactInfo object returned by the BusinessCardParser
 * getContactInfo() method.
 */
@RunWith( Parameterized.class )
public class BusinessCardParserTests
{
    /**
     * Example test file 1 path.
     */
    private static final String EXAMPLE_1_PATH = "examples/example1.txt";

    /**
     * Example test file 2 path.
     */
    private static final String EXAMPLE_2_PATH = "examples/example2.txt";

    /**
     * Example test file 3 path.
     */
    private static final String EXAMPLE_3_PATH = "examples/example3.txt";

    /**
     * BusinessCardParser to be used for parsing the example file text.
     */
    protected static BusinessCardParser parser;

    /**
     * Actual ContactInfo parameter.
     */
    @Parameterized.Parameter
    public ContactInfo actual;

    /**
     * Expected ContactInfo parameter.
     */
    @Parameterized.Parameter( 1 )
    public ContactInfo expected;

    /**
     * Loads ContactInfo parameters from the specified test files.
     *
     * @return A Collection of ContactInfo arrays, where the first index
     * contains the ContactInfo object returned by the parser, and the
     * second contains a ContactInfo object populated with expected values.
     * @throws IOException Thrown if the example file is not found.
     */
    @Parameterized.Parameters
    public static Collection<ContactInfo[]> data( ) throws IOException
    {
        parser = BusinessCardParserFactory.create( );

        return Arrays.asList( new ContactInfo[][]{
            { loadAndParseExampleFile( EXAMPLE_1_PATH ),
                new ContactInfo( "Mike Smith", "4105551234", "msmith@asymmetrik.com" ) },
            { loadAndParseExampleFile( EXAMPLE_2_PATH ),
                new ContactInfo( "Lisa Haung", "4105551234", "lisa.haung@foobartech.com" ) },
            { loadAndParseExampleFile( EXAMPLE_3_PATH ),
                new ContactInfo( "Arthur Wilson", "17035551259", "awilson@abctech.com" ) }

        } );
    }

    /**
     * Helper function that loads and parses the example text files.
     *
     * @param fileName File path to example file.
     * @return Parsed ContactInfo object.
     * @throws IOException thrown if the example text file is not found.
     */
    private static ContactInfo loadAndParseExampleFile( String fileName ) throws IOException
    {
        ClassLoader classLoader = Thread.currentThread( ).getContextClassLoader( );
        InputStream inStream = classLoader.getResourceAsStream( fileName );
        BufferedReader reader = new BufferedReader( new InputStreamReader( inStream ) );
        String text = reader.lines( ).collect( Collectors.joining( "\n" ) );

        return parser.getContactInfo( text );
    }

    /**
     * Tests for if the person's name is equal to the expected value.
     */
    @Test
    public void testName( )
    {
        Assert.assertEquals( expected.getName( ), actual.getName( ) );
    }

    /**
     * Tests for if the phone number is equal to the expected value.
     */
    @Test
    public void testPhone( )
    {
        Assert.assertEquals( expected.getPhoneNumber( ), actual.getPhoneNumber( ) );
    }

    /**
     * Tests for if the email is equal to the expected value.
     */
    @Test
    public void testEmail( )
    {
        Assert.assertEquals( expected.getEmailAddress( ), actual.getEmailAddress( ) );
    }
}
