Despector IntelliJ Integration
==============================

Brings support for [Despector](https://github.com/Deamon5550/Despector), a Java / Kotlin decompilation tool and AST library by
[Nick Condé (Deamon5550)](https://github.com/Deamon5550).

Currently doesn't do much, feature set will increase as Despector's stability and feature set also increases. Will eventually
support switching between Java and Kotlin output, using current IntelliJ style for decompiler output (or setting custom out styles),
switching between Despector and Fernflower output, and other stuff that I can come up with.

Building
--------

Despector isn't availalbe on Maven yet, so grab it from https://github.com/Deamon5550/Despector

> Build and install it: `./gradlew install`

Then switch back over here and build it with: `./gradlew build`

Run the plugin in a test IDE with `./gradlew runIde`

Preview
-------

![](http://i.imgur.com/Lu5Tijp.gif)
