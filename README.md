# Welcome to SuperFields!

## Overview

This is a collection of hopefully useful Vaadin 14 and 24+ components, grouped into several subprojects:
* `superfields` are various input components;
* `demo-v24` contains an app for Vaadin 24+ that shows all components - [see the demo online](https://demo.unforgiven.pl/superfields/) or run it yourself with `mvn package jetty:run`

## Installation

[SuperFields are also available in Vaadin Directory](https://vaadin.com/directory/component/superfields). If you find this library useful, consider rating it there or leaving a review. Thanks :)

SuperFields work with Vaadin 24 (and above) and Vaadin 14 LTS (only as long as Vaadin 14 is officially supported).

### Vaadin 23/24 and Java 11/17

Starting from **version 0.18** SuperFields work with Vaadin 24 and require **Java 17**.

**Versions 0.12 to 0.17** of SuperFields work with Vaadin 23 version. **Java 11** or newer is required.

From **version 0.16** every release is available in Maven Central. Please note that the `groupId` of the project **has changed** from `org.vaadin.miki` to `pl.unforgiven`. Class names and package names **have not changed**.

#### Maven setup

This is the relevant dependency:
```
<dependency>
   <groupId>pl.unforgiven</groupId>
   <artifactId>superfields</artifactId>
   <version>0.18.0</version>
</dependency>
```

All releases are available:
* in Maven Central
* as binaries under [project's releases](https://github.com/vaadin-miki/super-fields/releases)
* as Maven packages from [GitHub packages](https://github.com/vaadin-miki/super-fields/packages/177670) or from [Vaadin Directory](https://vaadin.com/directory/component/superfields)

### Vaadin 14 LTS and Java 8

Versions below **0.12** work only with Vaadin 14 LTS.

SuperFields in general **should** be compatible with Vaadin 14 LTS and with Java 8. Each release has a corresponding version marked with `-vaadin14` suffix in the number, e.g. `0.12.0-vaadin14`. The functionality is identical to the official release.

These releases are available in Maven Central. They can also be obtained from [Vaadin Directory](https://vaadin.com/directory/component/superfields). Here is the required repository:
```
<repository>
   <id>vaadin-addons</id>
   <url>https://maven.vaadin.com/vaadin-addons</url>
</repository>
```

This repository has a branch `java-8` which contains the code for the most recent release compatible with Java 8 and Vaadin 14 LTS. Please note that Java 8 has been released in 2014. Also, versions compatible with Vaadin 14 LTS will be maintained only as long as Vaadin 14 LTS is.

## Contribution guidelines

You are more than welcome to contribute. Feel free to make PRs, submit issues, ideas etc.

### Contributors

The author of the majority of the code is me (Miki), but this project would not be possible without these wonderful people - listed in (Finnish) alphabetical order:

* Diego Cardoso
* Wolfgang Fischlein
* Jean-Christophe Gueriaud
* Holger Hähnel
* Matthias Hämmerle
* Jarmo Kemppainen
* Gerald Koch
* Sebastian Kühnau
* Jean-François Lamy
* Erik Lumme
* Simon Martinelli
* Dmitry Nazukin
* Stefan Penndorf
* Stuart Robinson
* Kaspar Scherrer
* Tomi Virkki
* Martin Vysny
* Leif Åstrand

## Support

If you like SuperFields, you can support it in a variety of ways:
* spread the word - the more people know about SuperFields, the better! - if you use social networks, you can tag me on Mastodon (`@miki@the.unforgiven.pl`) or on Twitter (`@mikiolsz`)
* visit [the Vaadin Directory](https://vaadin.com/directory/component/superfields), rate the components and/or leave a review
* contribute - pick an issue from the list, implement it and make a PR
* buy a print of [one of my photos](https://www.uneven-eyes.info/page/photos-for-you)

## Small print

All components are provided "as is", with no warranty or liability. See license for details.

This library is not officially supported or endorsed by Vaadin and is not part of the Vaadin Platform.
