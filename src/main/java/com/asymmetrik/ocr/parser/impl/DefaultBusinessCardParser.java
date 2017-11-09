package com.asymmetrik.ocr.parser.impl;

import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.logging.RedwoodConfiguration;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The default implementation of a BusinessCardParser
 * <p>
 * This BusinessCardParser uses the Stanford CoreNLP library to identify a person's
 * name within the business card text.  The parser assumes that the business card
 * text will be formatted such that each sentence is on its own line.  Once the
 * text has been tokenized, split, parsed and tagged, the BusinessCardParser
 * traverses the tokens looking for ones that have been tagged as PERSON and returns
 * a compilation of PERSON tokens that are part of the same sentence.
 * <p>
 * Phone numbers are parsed with the libphonenumber library from Google.
 * <p>
 * Emails are parsed via regex.
 * <p>
 * The stanfordcorenlp.properties file contains the properties used by
 * the StanfordCoreNLP pipeline.
 */
public class DefaultBusinessCardParser implements BusinessCardParser
{
    private static final Logger log = Logger.getLogger( DefaultBusinessCardParser.class );

    /**
     * Regex used to parse an email address.  This is an RFC 5322 compliant regex.
     */
    protected static final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    /**
     * Stanford CoreNLP pipeline used for
     */
    protected StanfordCoreNLP pipeline;

    /**
     * Regex Pattern object used for extracting the email address
     */
    protected Pattern emailPattern;

    /**
     * Instantiates an instance of this DefaultBusinessCardParser.
     * <p>
     * Initializes the Stanford CoreNLP pipeline and creates the Pattern object
     * for email parsing.
     */
    public DefaultBusinessCardParser( )
    {
        pipeline = initializePipeline( );
        emailPattern = Pattern.compile( EMAIL_REGEX );
    }

    /**
     * {@inheritDoc}
     */
    public ContactInfo getContactInfo( String document )
    {
        return new ContactInfo( parseName( document ),
                                  parsePhone( document ),
                                  parseEmailAddress( document ) );
    }

    /**
     * Instantiates the StanfordCoreNLP pipeline object.
     *
     * @return the StanfordCoreNLP object
     */
    protected StanfordCoreNLP initializePipeline( )
    {
        RedwoodConfiguration.current( ).clear( ).apply( );

        StanfordCoreNLP pipeline = new StanfordCoreNLP( "stanfordcorenlp" );

        return pipeline;
    }

    /**
     * Helper method for parsing a person's name from the business card string.
     * <p>
     * This annotates the text and extracts the tokens or words that have a named
     * entity recognition tag (NER) of "PERSON".  It returns the PERSON tokens from the
     * first sentence or line that contains these tokens and does not look at any additional
     * sentences.
     *
     * @param text The raw text of the business card
     * @return The person's name, null if none is found.
     */
    protected String parseName( String text )
    {
        Annotation document = new Annotation( text );

        pipeline.annotate( document );

        List<CoreMap> sentences = document.get( CoreAnnotations.SentencesAnnotation.class );

        StringJoiner output = new StringJoiner( " " );

        for ( CoreMap sentence : sentences )
        {
            for ( CoreLabel token : sentence.get( CoreAnnotations.TokensAnnotation.class ) )
            {
                if( token.ner( ).equals( "PERSON" ) )
                {
                    output.add( token.value( ) );
                }
            }

            if( output.length( ) > 0 )
            {
                return output.toString( );
            }
        }

        log.warn( "Unable to parse name from text:\n" + text );

        return null;
    }

    /**
     * Parses the phone number from the business card text.
     * <p>
     * First attempts to locate an international number, if none is found,
     * attempts to locate a national U.S. number.
     * <p>
     * This method will not be able to find a local number from a region
     * other than the U.S.
     *
     * @param text raw text of the business card.
     * @return The phone number as a string of digits, including the country code if it was in the raw text.
     * Returns null if none is found.
     */
    protected String parsePhone( String text )
    {
        String number = tryGetPhoneNumber( text, null );

        if( number == null )
        {
            number = tryGetPhoneNumber( text, "US" );
        }

        if( number == null ) log.warn( "Unable to parse phone number from text:\n" + text );

        return number == null ? null : number.replaceAll( "[^\\d]", "" );
    }

    /**
     * Helper method for parsing the phone number.
     * <p>
     * Calls the libphonenumber PhoneNumberUtil.findNumbers() with the text and region code.
     *
     * @param text          Text to locate the phone number.
     * @param defaultRegion Region code, can be null if the input is expected to contain an international number.
     * @return The number found as a raw string, including any non digit characters.
     */
    protected String tryGetPhoneNumber( String text, String defaultRegion )
    {
        Iterable<PhoneNumberMatch> matches = PhoneNumberUtil.getInstance( ).findNumbers( text, defaultRegion );

        if( matches.iterator( ).hasNext( ) )
        {
            return matches.iterator( ).next( ).rawString( );
        }

        return null;
    }

    /**
     * Parses the email address from the raw text of the business card.
     * <p>
     * This method uses an RFC 5322 compliant regex to locate and extract
     * the email address.
     *
     * @param text the raw text of the business card.
     * @return The email address as a string.
     */
    protected String parseEmailAddress( String text )
    {
        Matcher matcher = emailPattern.matcher( text );

        if( matcher.find( ) )
        {
            return matcher.group( 0 );
        }

        log.warn( "Unable to parse email address from text:\n" + text );

        return null;
    }
}
