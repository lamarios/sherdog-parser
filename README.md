# Sherdog Parser

Sherdog Parser is Java library to parse through Sherdog and have get all the info about MMA you need.

## Download

*Currently in process to get it to maven central*

## Usage
[Java Documentation](https://lamarios.github.io/sherdog-parser/apidocs/)



First created a Sherdog object with your own timezone and a cache folder where the fighter pictures will be downloaded
```Java
Sherdog parser = new Sherdog.Builder().withCacheFolder("/mnt/media/fighter-pictures/cache").withTimezone("Asia/Kuala_Lumpur").build();
```

To get all the events of an organization
```Java
Organization ufc = parser.getOrganization(Organizations.UFC.url);
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
