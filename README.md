# Sherdog Parser
![travis-ci](https://travis-ci.org/lamarios/sherdog-parser.svg?branch=master) ![maven](https://maven-badges.herokuapp.com/maven-central/com.ftpix/sherdog-parser/badge.svg)

Sherdog Parser is Java library to parse through Sherdog and have get all the info about MMA you need.

## Download

```xml
<!-- https://mvnrepository.com/artifact/com.ftpix/sherdog-parser -->
<dependency>
    <groupId>com.ftpix</groupId>
    <artifactId>sherdog-parser</artifactId>
    <version>2.4</version>
</dependency>
```

## Usage
[Java Documentation](https://lamarios.github.io/sherdog-parser/apidocs/)

### Basic

First created a Sherdog object.
```Java
Sherdog parser = new Sherdog.Builder().build();
```

To get all the events of an organization
```Java
Organization ufc = parser.getOrganization(Organizations.UFC);
```
Some organizations are already preset under the enum **Organizations** or you can just put the URL of the organization from Sherdog website

In the parser, everything works with the URL of the item in Sherdog.

To get the detail of an Event. As before, you can use the event URLs you get from the organization or directly using Sherdog URL

```Java
Event ufc1 = parser.getEvent(ufc.getEvents().get(0).getSherdogUrl());
```

An event will hold a list of fights
```Java
Fight firstFight = ufc1.getFights().get(0);
```

You can get more details about a fighter with his Sherdog url.
```Java
Fighter fighter = parser.getFighter(firstFight.getFighter1().getSherdogUrl());
```


By default the fighter picture  value will be set to the sherdog picture URL. If you wish to do something else with it you can set a picture processor to your parser.
Example on how to download the picture to a temp file and set it as the fighter picture value.

The processor takes two parameters, the url (String) and the fighter (Fighter)

```java
Sherdog parser = new Sherdog.Builder().withPictureProcessor((url, fighter) -> {
            final Path tempFile = Files.createTempFile(fighter.getName(), "");
            FileUtils.copyURLToFile(new URL(url), tempFile.toFile());

            return tempFile.toAbsolutePath().toString();
        }).build();
```

### Alternative

If for some reason you want to get the content of sherdog web pages on your own you can still parse the HTML document by simply using the **FromHtml** methods

Example:
```java
Organization ufc = parser.getOrganizationFromHtml("The html code of an organisation page");
```
