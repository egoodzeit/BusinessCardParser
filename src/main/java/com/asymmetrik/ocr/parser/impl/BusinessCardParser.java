package com.asymmetrik.ocr.parser.impl;

/**
 * Root interface for a BusinessCardParser
 * <p>
 * A BusinessCardParser is an object that can take in a string representation of a
 * business card, and extract the contact information, such as a person's name,
 * their phone number and their email address.
 */
public interface BusinessCardParser
{
    /**
     * Parses the string representation of a business card, and returns a ContactInfo object
     * that contains the name of the person, their phone number and an email address.
     *
     * @param document A string representation of a business card.
     * @return A ContactInfo object containing the extracted name, phone number and email address.
     */
    ContactInfo getContactInfo( String document );
}
