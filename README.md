# Installation

## Using IntelliJ

* Install jdk 1.8+ and add it to path.
* Install [maven](http://www.baeldung.com/install-maven-on-windows-linux-mac) and add it to path.
* Insall IntelliJ
* Import Project -> Create project from existing sources -> Leave project name defaults -> Uncheck all tests -> Done.

There should be a message related to `non-managed pom file found`. Click on it an select `Add project as maven`. This should start resolving all maven dependencies.

IntelliJ might also request a JKD setup. Click on this notification and select the jdk installation folder.

## Using Terminal

I recommend using IntelliJ to at least setup the project for the first time. However, the terminal can be used to run the application. Navigate to the project folder inside the terminal and run the following command `mvn spring-boot:run`