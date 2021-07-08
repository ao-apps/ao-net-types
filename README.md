# [<img src="ao-logo.png" alt="AO Logo" width="35" height="40">](https://github.com/aoindustries) [AO OSS](https://github.com/aoindustries/ao-oss) / [Net Types](https://github.com/aoindustries/ao-net-types)

[![project: current stable](https://oss.aoapps.com/ao-badges/project-current-stable.svg)](https://aoindustries.com/life-cycle#project-current-stable)
[![management: production](https://oss.aoapps.com/ao-badges/management-production.svg)](https://aoindustries.com/life-cycle#management-production)
[![packaging: active](https://oss.aoapps.com/ao-badges/packaging-active.svg)](https://aoindustries.com/life-cycle#packaging-active)  
[![java: &gt;= 8](https://oss.aoapps.com/ao-badges/java-8.svg)](https://docs.oracle.com/javase/8/docs/api/)
[![semantic versioning: 2.0.0](https://oss.aoapps.com/ao-badges/semver-2.0.0.svg)](http://semver.org/spec/v2.0.0.html)
[![license: LGPL v3](https://oss.aoapps.com/ao-badges/license-lgpl-3.0.svg)](https://www.gnu.org/licenses/lgpl-3.0)

Networking-related value types.

## Project Links
* [Project Home](https://oss.aoapps.com/net-types/)
* [Changelog](https://oss.aoapps.com/net-types/changelog)
* [API Docs](https://oss.aoapps.com/net-types/apidocs/)
* [Maven Central Repository](https://search.maven.org/artifact/com.aoapps/ao-net-types)
* [GitHub](https://github.com/aoindustries/ao-net-types)

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
The [AOServ Platform](https://aoindustries.com/aoserv/) allows configuration of services on a per-port and per-IP basis.  System configuration components like [AO SELinux](https://github.com/aoindustries/ao-selinux) and [AO firewalld](https://github.com/aoindustries/ao-firewalld) support configurations over port ranges and network ranges.

These value types are a common representation of networking configuration shared between various components.  They have minimal dependencies and may be useful for projects needing to go beyond the stock `java.net.*` classes.

## Evaluated Alternatives
Admittedly, several features of this API are redundant with the [standard Java API](https://docs.oracle.com/javase/7/docs/api/java/net/package-summary.html).  This API has a long history and, as the Java platform matures, is increasingly redundant.  When it is sufficient for your needs, please prefer the [standard Java API](https://docs.oracle.com/javase/7/docs/api/java/net/package-summary.html).

## Contact Us
For questions or support, please [contact us](https://aoindustries.com/contact):

Email: [support@aoindustries.com](mailto:support@aoindustries.com)  
Phone: [1-800-519-9541](tel:1-800-519-9541)  
Phone: [+1-251-607-9556](tel:+1-251-607-9556)  
Web: https://aoindustries.com/contact
