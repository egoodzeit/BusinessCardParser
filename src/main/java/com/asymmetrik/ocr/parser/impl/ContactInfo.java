package com.asymmetrik.ocr.parser.impl;

import java.util.StringJoiner;

/**
 * An immutable object for storing a name, email address and phone number as strings.
 */
public class ContactInfo
{
    /**
     * Person's name.
     */
    protected String name;

    /**
     * Person's phone number.
     */
    protected String phoneNumber;

    /**
     * Person's email address.
     */
    protected String emailAddress;

    /**
     * Instantiates this instance of a ContactInfo object.
     *
     * @param name         Person's name as a String.
     * @param phoneNumber  Person's phone number as a String.
     * @param emailAddress Person's email address as a String.
     */
    public ContactInfo( String name,
                        String phoneNumber,
                        String emailAddress )
    {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
    }

    /**
     * Returns the name.
     *
     * @return the name of the person.
     */
    public String getName( )
    {
        return name;
    }

    /**
     * Returns the phone number.
     *
     * @return the phone number.
     */
    public String getPhoneNumber( )
    {
        return phoneNumber;
    }

    /**
     * Returns the email address.
     *
     * @return the email address.
     */
    public String getEmailAddress( )
    {
        return emailAddress;
    }

    /**
     * Overriden to better format the string represetation
     *
     * @return
     */
    public String toString( )
    {
        StringJoiner joiner = new StringJoiner( System.getProperty( "line.separator" ) );
        joiner.add( "Name: " + name );
        joiner.add( "Phone: " + phoneNumber );
        joiner.add( "Email: " + emailAddress );
        return joiner.toString( );
    }
}
