# BusinessCardParser

A simple business card text parser that extracts the contact information from the raw text of a business card.

### Prerequisites

You will need Maven and at least Java 8 to compile and install the BusinessCardParser.

### Installing

Install using Maven this will create an executable jar with the dependencies.  Note, the jar will be large, around 375mb.  This is due to the inclusiong of the Stanford CoreNLP library and models.

```
mvn clean install
```

### Running

Navigate to the target folder.  Execute the jar with the first argument being a path to an input text file.

```
java -jar business-card-ocr-text-parser-jar-with-dependencies.jar <path_to_input_file>
```

You can optionally output the results to a file:

```
-o <path_to_output_file>
```

### Running tests

Unit tests can be run with the following command:

```
mvn test
```

Example test files are found in tests/resources/examples.

### Using BusinessCardParser

```java
BusinessCardParser parser = BusinessCardParserFactory.create();

/*
* document is the string representation of business card, e.g.; 
*
* John Smith
* Software Developer
* (555) 555-5555
* jsmith@foo.bar
*/
ContactInfo contactInfo = parser.getContactInfo( document );

String name = contactInfo.getName();
String phone = contactInfo.getPhoneNumber();
String email = contactInfo.getEmailAddress();
```

### Built With

* [Stanford CoreNLP](https://stanfordnlp.github.io/CoreNLP/) - The NLP library used.
* [libphonenumber](https://github.com/googlei18n/libphonenumber) - Library used for phone number parsing.
* [Maven](https://maven.apache.org/) - Dependency Management