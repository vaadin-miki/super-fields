# Welcome to SuperFields!

## Overview

This is a collection of hopefully useful components for Vaadin 25. There are two submodules within this repository:

* `superfields` are various Vaadin components;
* `demo-app` that contains an app for Vaadin 25 that shows all components - [see the demo online](https://demo.unforgiven.pl/superfields/) or run it yourself with `mvn package jetty:run`

## Installation

[SuperFields are also available in Vaadin Directory](https://vaadin.com/directory/component/superfields). If you find
this library useful, consider rating it there or leaving a review. Thanks :)

### Vaadin and Java versions

Starting from **version 0.20** SuperFields work with Vaadin 25 and require **Java 21**. 

Starting from **version 0.18** SuperFields work with Vaadin 24 and require **Java 17**.

**Versions 0.12 to 0.17** of SuperFields work with Vaadin 23 version. **Java 11** or newer is required.

From **version 0.16** every release is available in Maven Central. Please note that the `groupId` of the project **has
changed** from `org.vaadin.miki` to `pl.unforgiven`. Class names and package names **have not changed**.

#### Maven setup

This is the relevant dependency:

```
<dependency>
   <groupId>pl.unforgiven</groupId>
   <artifactId>superfields</artifactId>
   <version>0.20.0</version>
</dependency>
```

All releases are available:

* in Maven Central
* as binaries under [project's releases](https://github.com/vaadin-miki/super-fields/releases)
* as Maven packages from [GitHub packages](https://github.com/vaadin-miki/super-fields/packages/177670) or
  from [Vaadin Directory](https://vaadin.com/directory/component/superfields)

### Vaadin 14 LTS and Java 8

Versions **0.19.3** and newer **do not** have a Vaadin 14 release, as it has reached the end of free LTS support. Thus,
the last version that works with Vaadin 14 is **0.19.2-vaadin14**.

Please note that versions below **0.12** work only with Vaadin 14 LTS.

Each release between **0.12.0** and **0.19.2** has a corresponding version marked with `-vaadin14` suffix in the number,
e.g. `0.12.0-vaadin14`. The functionality is identical to the regular release. These releases are available in Maven
Central.

This repository has a branch `java-8` which contains the code for the most recent release compatible with Java 8 and
Vaadin 14 LTS. Please note that Java 8 has been released in 2014 and free support for Vaadin 14 LTS has ended in
September 2024.

Future releases of `SuperFields` for Vaadin 14 are still possible only in two cases:

* pull requests by the community;
* paid work (see `Support` below).

### Snapshot versions

As of version `0.20` snapshots may be available from Central. Snapshot versions follow a version `0.0.YEAR-SNAPSHOT`,
where `YEAR` is the current calendar year. These are automatically created upon a new commit to `development` and are
also [automatically removed from Central every 90 days](https://central.sonatype.org/publish/publish-portal-snapshots/).

Please abstain from using snapshot versions in production code. The intended use is to try bugfixes and new features
as they are being implemented and to ensure the deployment pipeline works at all times.

## Contribution guidelines

You are more than welcome to contribute. Feel free to make PRs, submit issues, ideas etc.

### Contributors

The author of the majority of the code is me (Miki), but this project would not be possible without these wonderful
people - listed in (Finnish) alphabetical order:

* Diego Cardoso
* Wolfgang Fischlein
* Jean-Christophe Gueriaud
* Holger Hähnel
* Matthias Hämmerle
* Sascha Ißbrücker
* Jarmo Kemppainen
* Gerald Koch
* Sebastian Kühnau
* Jean-François Lamy
* Erik Lumme
* Simon Martinelli
* Dmitry Nazukin
* Stefan Penndorf
* Sebastian Penttinen
* Stuart Robinson
* Kaspar Scherrer
* Tomi Virkki
* Martin Vyšný
* Leif Åstrand

## Support

If you like SuperFields, you can support the project in a variety of ways:

* spread the word - the more people know about SuperFields, the better! - if you use social networks, you can tag me on
  Mastodon (`@miki@the.unforgiven.pl`) or on Twitter (`@mikiolsz`);
* visit [the Vaadin Directory](https://vaadin.com/directory/component/superfields), rate the components and/or leave a
  review;
* contribute - pick an issue from the list, implement it and make a PR;
* on-demand features - get in touch with me to agree the details;
* buy a print of [one of my photos](https://www.uneven-eyes.info/page/photos-for-you).

## Small print

All components are provided "as is", with no warranty or liability. See license for details.

This library is not officially supported or endorsed by Vaadin and is not part of the Vaadin Platform.
