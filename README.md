# Welcome to SuperFields!

## Overview

This is a collection of hopefully useful Vaadin 14 components, grouped into several sub-projects:
* `superfields` are various input components;
* `demo-v14` contains an app for Vaadin 14 that shows all components - [see the demo online](https://superfields.herokuapp.com/) on Heroku

## Installation

[SuperFields are also available in Vaadin Directory](https://vaadin.com/directory/component/superfields). If you find this library useful, consider rating it there or leaving a review. Thanks :)

SuperFields require Java 10 or newer and work with the latest Vaadin 14 LTS version.

### Maven setup

This is the relevant dependency:
```
<dependency>
   <groupId>org.vaadin.miki</groupId>
   <artifactId>superfields</artifactId>
   <version>0.9.0</version>
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

### Java 8

Some versions compatible with Java 8 are available in the Vaadin Directory. They are marked with `.java8` suffix in the version, e.g. `0.6.0.java8`. Their functionality is identical to the official release.

This repository has a branch `java-8` which contains the most recent release compatible with Java 8. 

## Contribution guidelines

You are more than welcome to contribute. Feel free to make PRs, submit issues, ideas etc.

### Contributors

The author of the majority of the code is Miki, but this project would not be possible without these wonderful people - listed in alphabetical order:

* Wolfgang Fischlein
* Jean-Christophe Gueriaud
* Holger Hähnel
* Gerald Koch
* Sebastian Kühnau
* Jean-François Lamy
* Stuart Robinson
* Kaspar Scherrer
* Tomi Virkki

## Small print

All components are provided "as is", with no warranty or liability. See license for details.
