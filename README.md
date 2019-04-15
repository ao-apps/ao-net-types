# [<img src="ao-logo.png" alt="AO Logo" width="35" height="40">](https://github.com/aoindustries) [AO Net Types](https://github.com/aoindustries/ao-net-types)
<p>
	<a href="https://aoindustries.com/life-cycle#project-current-stable">
		<img src="https://aoindustries.com/ao-badges/project-current-stable.svg" alt="project: current stable" />
	</a>
	<a href="https://aoindustries.com/life-cycle#management-production">
		<img src="https://aoindustries.com/ao-badges/management-production.svg" alt="management: production" />
	</a>
	<a href="https://aoindustries.com/life-cycle#packaging-active">
		<img src="https://aoindustries.com/ao-badges/packaging-active.svg" alt="packaging: active" />
	</a>
	<br />
	<a href="https://docs.oracle.com/javase/7/docs/api/">
		<img src="https://aoindustries.com/ao-badges/java-7.svg" alt="java: &gt;= 7" />
	</a>
	<a href="http://semver.org/spec/v2.0.0.html">
		<img src="https://aoindustries.com/ao-badges/semver-2.0.0.svg" alt="semantic versioning: 2.0.0" />
	</a>
	<a href="https://www.gnu.org/licenses/lgpl-3.0">
		<img src="https://aoindustries.com/ao-badges/license-lgpl-3.0.svg" alt="license: LGPL v3" />
	</a>
</p>

Networking-related value types.

## Project Links
* [Project Home](https://aoindustries.com/ao-net-types/)
* [Changelog](https://aoindustries.com/ao-net-types/changelog)
* [API Docs](https://aoindustries.com/ao-net-types/apidocs/)
* [Maven Central Repository](https://search.maven.org/#search%7Cgav%7C1%7Cg:%22com.aoindustries%22%20AND%20a:%22ao-net-types%22)
* [GitHub](https://github.com/aoindustries/ao-net-types)

## Features
* IPv4 and IPv6 address families.
* Individual addresses and network ranges.
* Protocols, ports, and port ranges.
* Supports optimization through reduction of IP and port combinations through:
    * Coalescing of addresses and network ranges into fewer network ranges.
    * Coalescing of ports and port ranges into fewer port ranges.
* [Fast serializable](https://aoindustries.com/ao-lang/apidocs/com/aoindustries/io/FastExternalizable.html) and self-validating objects.
* [Internable](https://aoindustries.com/ao-lang/apidocs/com/aoindustries/util/Internable.html) for memory savings on large datasets.
* Small footprint, minimal dependencies - not part of a big monolithic package.
* Java 1.7 implementation:
    * Android compatible.
    * Java EE 6+ compatible.
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
