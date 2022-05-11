# Welcome to SuperFields!

## Overview

This is a collection of hopefully useful Vaadin 14 and 23+ components, grouped into several sub-projects:
* `superfields` are various input components;
* `demo-v23` contains an app for Vaadin 23+ that shows all components - [see the demo online](https://superfields.herokuapp.com/) on Heroku

## Installation

[SuperFields are also available in Vaadin Directory](https://vaadin.com/directory/component/superfields). If you find this library useful, consider rating it there or leaving a review. Thanks :)

SuperFields work with Vaadin 23 (and above) and Vaadin 14 LTS.

### Vaadin 23+ and Java 11

Starting with **version 0.12** SuperFields work with the latest major Vaadin version. **Java 11** or newer is required. 

#### Maven setup

This is the relevant dependency:
```
<dependency>
   <groupId>org.vaadin.miki</groupId>
   <artifactId>superfields</artifactId>
   <version>0.13.1</version>
</dependency>
```

Here is the repository:
```
<repository>
   <id>vaadin-addons</id>
   <url>https://maven.vaadin.com/vaadin-addons</url>
</repository>
```

Note that not every version will be released in the Vaadin Directory, as it has to be done manually.

All releases are available:
* as binaries under [project's releases](https://github.com/vaadin-miki/super-fields/releases)
* as Maven packages from [GitHub packages](https://github.com/vaadin-miki/super-fields/packages/177670)

### Vaadin 14 LTS and Java 8

Versions below **0.12** work only with Vaadin 14 LTS.

SuperFields in general **should** be compatible with Vaadin 14 LTS and with Java 8. These versions are released in the Vaadin Directory marked with `-vaadin14` suffix in the version, e.g. `0.12.0-vaadin14`. Their functionality is identical to the official release.

This repository has a branch `java-8` which contains the most recent release compatible with Java 8 and Vaadin 14 LTS. Please note that Java 8 has been released in 2014. Also, versions compatible with Vaadin 14 LTS will be maintained only as long as Vaadin 14 LTS is.  

## Contribution guidelines

You are more than welcome to contribute. Feel free to make PRs, submit issues, ideas etc.

### Contributors

The author of the majority of the code is Miki, but this project would not be possible without these wonderful people - listed in alphabetical order:

* Diego Cardoso
* Wolfgang Fischlein
* Jean-Christophe Gueriaud
* Holger Hähnel
* Matthias Hämmerle
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

## Small print

All components are provided "as is", with no warranty or liability. See license for details.

This library is not officially supported or endorsed by Vaadin and is not part of the Vaadin Platform.
