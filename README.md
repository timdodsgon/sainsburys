# Sainsburys Technical Test

## Coding Considerations

The code is designed to follow best practices, portability and be easily extensible.

The whole concept of scraping html for vales is fragile at best so one of my early consideration was to de couple the css selectors from the actual service that is going to use them, to achieve this the application has a centralised configuration which can be overridable by an external properties file 

When considering extensibility i had in mind being able to scrape a completely different domain, to achieve this the calling code to scrape a site calls an interface that is returned a scraper from a ScraperServiceFactory this means adding another scraper tailored to different site is as easy as adding a class and adding the new site to the factory

Logging is used to feedback information to the user, as well as error messages



## Installation

    git clone https://github.com/timdodsgon/sainsburys/
    
Once you have cloned the project run the following at a CMD prompt in the project folder

    mvn package

## Dependencies

The application depends on just few popular libraries/frameworks which all supplied by maven dependencies

- jackson - for JSON data manipulation.
- jsoup - for HTML parsing and DOM manipulation as well as web connections.
- junit, mockito, wiremock and hamcrest - for testing.

## Usage

### Simple

Given the installation steps above have been completed, open a CMD prompt in the project folder and run the following command

    java -jar target/sainsburys-1.0-SNAPSHOT-shaded.jar

### With Configuration File

The application can optionally be passed a properties file, the properties file can override from one to six values used in the application.

The properties file follows a standard format of name=value once per line

##### Properties File Example

    url=https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/webapp/wcs/stores/servlet/gb/groceries/berries-cherries-currants6039.html
    baseurl=https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/
    title=div.productTitleDescriptionContainer h1
    calories=td.nutritionLevel1, td:eq(0)[class], tr:eq(1) td:eq(1)
    price=p.pricePerUnit
    description=div.productText p, div.itemTypeGroup

Once you have created a file save it to the target folder in the project, enter following command and the application will run using the overridden values you have entered in the config file

    java -jar target/sainsburys-1.0-SNAPSHOT-shaded.jar target/config.properties


## Test

All test can be run by typing the following at a CMD prompt, in the project folder

    mvn test

    
