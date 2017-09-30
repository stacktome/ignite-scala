## Description

A convenient Scala API for Apache Ignite 

## Motivation

There are a lot of configuration options for Apache Ignite and so it can take some time to learn which features to use.
By providing traits and case classes that make it clear what should be provided, new applications using Ignite can be developed more rapidly.   

## Features

* Configure and build IgniteConfiguration used to start client or server nodes without use of Spring XML
* Configure and build CacheConfiguration used to create caches without use of Spring XML
* Define and run Continuous Query with server-side event filter and client-side async callback
* Start Ignite node and create all caches

## Installation

* Clone this repository
* Eexecute `sbt publish-local` to publish the jar to your local ivy cache
* Add `libraryDependencies += "com.jasonmar" %% "ignite-scala" % "1.0"` to your project's build.sbt

## License

This project uses the Apache 2.0 license. Read LICENSE file.

## Authors and Copyright

Copyright (C) 2017 Jason Mar
