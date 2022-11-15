# [<img src="ao-logo.png" alt="AO Logo" width="35" height="40">](https://github.com/ao-apps) [AO OSS](https://github.com/ao-apps/ao-oss) / [Net Types](https://github.com/ao-apps/ao-net-types)

[![project: current stable](https://oss.aoapps.com/ao-badges/project-current-stable.svg)](https://aoindustries.com/life-cycle#project-current-stable)
[![management: production](https://oss.aoapps.com/ao-badges/management-production.svg)](https://aoindustries.com/life-cycle#management-production)
[![packaging: active](https://oss.aoapps.com/ao-badges/packaging-active.svg)](https://aoindustries.com/life-cycle#packaging-active)  
[![java: &gt;= 8](https://oss.aoapps.com/ao-badges/java-8.svg)](https://docs.oracle.com/javase/8/)
[![semantic versioning: 2.0.0](https://oss.aoapps.com/ao-badges/semver-2.0.0.svg)](http://semver.org/spec/v2.0.0.html)
[![license: LGPL v3](https://oss.aoapps.com/ao-badges/license-lgpl-3.0.svg)](https://www.gnu.org/licenses/lgpl-3.0)

[![Build](https://github.com/ao-apps/ao-net-types/workflows/Build/badge.svg?branch=master)](https://github.com/ao-apps/ao-net-types/actions?query=workflow%3ABuild)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.aoapps/ao-net-types/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.aoapps/ao-net-types)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?branch=master&project=com.aoapps%3Aao-net-types&metric=alert_status)](https://sonarcloud.io/dashboard?branch=master&id=com.aoapps%3Aao-net-types)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?branch=master&project=com.aoapps%3Aao-net-types&metric=ncloc)](https://sonarcloud.io/component_measures?branch=master&id=com.aoapps%3Aao-net-types&metric=ncloc)  
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?branch=master&project=com.aoapps%3Aao-net-types&metric=reliability_rating)](https://sonarcloud.io/component_measures?branch=master&id=com.aoapps%3Aao-net-types&metric=Reliability)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?branch=master&project=com.aoapps%3Aao-net-types&metric=security_rating)](https://sonarcloud.io/component_measures?branch=master&id=com.aoapps%3Aao-net-types&metric=Security)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?branch=master&project=com.aoapps%3Aao-net-types&metric=sqale_rating)](https://sonarcloud.io/component_measures?branch=master&id=com.aoapps%3Aao-net-types&metric=Maintainability)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?branch=master&project=com.aoapps%3Aao-net-types&metric=coverage)](https://sonarcloud.io/component_measures?branch=master&id=com.aoapps%3Aao-net-types&metric=Coverage)

Networking-related value types.

## Project Links
* [Project Home](https://oss.aoapps.com/net-types/)
* [Changelog](https://oss.aoapps.com/net-types/changelog)
* [API Docs](https://oss.aoapps.com/net-types/apidocs/)
* [Maven Central Repository](https://search.maven.org/artifact/com.aoapps/ao-net-types)
* [GitHub](https://github.com/ao-apps/ao-net-types)

## Features
* IPv4 and IPv6 address families.
* Individual addresses and network ranges.
* Protocols, ports, and port ranges.
* Supports optimization through reduction of IP and port combinations through:
    * Coalescing of addresses and network ranges into fewer network ranges.
    * Coalescing of ports and port ranges into fewer port ranges.
* [Fast serializable](https://oss.aoapps.com/lang/apidocs/com.aoapps.lang/com/aoapps/lang/io/FastExternalizable.html) and self-validating objects.
* [Internable](https://oss.aoapps.com/lang/apidocs/com.aoapps.lang/com/aoapps/lang/util/Internable.html) for memory savings on large datasets.
* Small footprint, minimal dependencies - not part of a big monolithic package.
* Java 1.8 implementation:
    * Android compatible.
* Compatible [PostgreSQL](https://www.postgresql.org/) type implementations for database-level integrity.

## Motivation
The [AOServ Platform](https://aoindustries.com/aoserv/) allows configuration of services on a per-port and per-IP basis.  System configuration components like [AO SELinux](https://github.com/ao-apps/ao-selinux) and [AO firewalld](https://github.com/ao-apps/ao-firewalld) support configurations over port ranges and network ranges.

These value types are a common representation of networking configuration shared between various components.  They have minimal dependencies and may be useful for projects needing to go beyond the stock `java.net.*` classes.

## Evaluated Alternatives
Admittedly, several features of this API are redundant with the [standard Java API](https://docs.oracle.com/javase/7/docs/api/java/net/package-summary.html).  This API has a long history and, as the Java platform matures, is increasingly redundant.  When it is sufficient for your needs, please prefer the [standard Java API](https://docs.oracle.com/javase/7/docs/api/java/net/package-summary.html).

## Contact Us
For questions or support, please [contact us](https://aoindustries.com/contact):

Email: [support@aoindustries.com](mailto:support@aoindustries.com)  
Phone: [1-800-519-9541](tel:1-800-519-9541)  
Phone: [+1-251-607-9556](tel:+1-251-607-9556)  
Web: https://aoindustries.com/contact
