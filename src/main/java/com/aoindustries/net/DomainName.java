/*
 * ao-net-types - Networking-related value types for Java.
 * Copyright (C) 2010-2013, 2015, 2016, 2017  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-net-types.
 *
 * ao-net-types is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-net-types is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-net-types.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.net;

import com.aoindustries.dto.DtoFactory;
import com.aoindustries.io.FastExternalizable;
import com.aoindustries.io.FastObjectInput;
import com.aoindustries.io.FastObjectOutput;
import com.aoindustries.tlds.TopLevelDomain;
import com.aoindustries.util.ComparatorUtils;
import com.aoindustries.util.Internable;
import com.aoindustries.validation.InvalidResult;
import com.aoindustries.validation.ValidResult;
import com.aoindustries.validation.ValidationException;
import com.aoindustries.validation.ValidationResult;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputValidation;
import java.io.ObjectOutput;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Represents a DNS domain name.  Domain names must:
 * <ul>
 *   <li>Be non-null</li>
 *   <li>Be non-empty</li>
 *   <li>May not be "default" (case-insensitive) - to avoid conflicts with Cyrus no-domain configurations.</li>
 *   <li>
 *     Conform to definition in <a href="http://en.wikipedia.org/wiki/Hostname#Internet_hostnames">http://en.wikipedia.org/wiki/Hostname#Internet_hostnames</a>
 *     and <a href="http://en.wikipedia.org/wiki/DNS_label#Parts_of_a_domain_name">http://en.wikipedia.org/wiki/DNS_label#Parts_of_a_domain_name</a>
 *   </li>
 *   <li>May be "localhost" or "localhost.localdomain" - other checks that conflict with this are skipped.</li>
 *   <li>Last domain label must be alphabetic (not be all numeric)</li>
 *   <li>Last label must be a valid top level domain.</li>
 *   <li>For reverse IP address delegation, if the domain ends with ".in-addr.arpa", the first label may also be in the format "##/##".</li>
 *   <li>Not end with a period (.)</li>
 * </ul>
 *
 * @author  AO Industries, Inc.
 */
final public class DomainName
implements
	Comparable<DomainName>,
	FastExternalizable,
	ObjectInputValidation,
	DtoFactory<com.aoindustries.net.dto.DomainName>,
	Internable<DomainName>
{

	public static final int MAX_LENGTH = 253;

	public static final DomainName
		LOCALHOST,
		LOCALHOST_LOCALDOMAIN
	;
	static {
		try {
			LOCALHOST = new DomainName("localhost").intern();
			LOCALHOST_LOCALDOMAIN = new DomainName("localhost.localdomain").intern();
		} catch(ValidationException e) {
			AssertionError ae = new AssertionError("These hard-coded values are valid");
			ae.initCause(e);
			throw ae;
		}
	}

	/*
	private static boolean isNumeric(String label) {
		return isNumeric(label, 0, label.length());
	}*/

	private static boolean isNumeric(String label, int start, int end) {
		if((end-start)<=0) throw new IllegalArgumentException("empty label");
		for(int i=start; i<end; i++) {
			char ch = label.charAt(i);
			if(ch<'0' || ch>'9') return false;
		}
		return true;
	}

	/**
	 * Checks if is in the format numeric / numeric.
	 */
	private static boolean isArpaDelegationFirstLabel(String label, int beginIndex, int endIndex) {
		int slashPos = -1;
		for(int i=beginIndex; i<endIndex; i++) {
			if(label.charAt(i)=='/') {
				slashPos = i;
				break;
			}
		}
		return
			slashPos!=-1
			&& isNumeric(label, beginIndex, slashPos)
			&& isNumeric(label, slashPos+1, endIndex)
		;
	}

	/**
	 * Checks if ends with .in-addr.arpa (case insensitive).
	 *
	 * Performance measurement of new implementation versus old (10,000,000 iterations):
	 *     "This is not an arpa"
	 *         Old: 2911.130028 ms
	 *         New: 37.805258 ms
	 *         Improvement: 77.0 times
	 *     "this is not an arpa"
	 *         Old: 1746.381520 ms
	 *         New: 50.407891 ms
	 *         Improvement: 34.7 times
	 *     "subnet0.144.71.64.in-addr.arpa"
	 *         Old: 2712.463721 ms
	 *         New: 284.561373 ms
	 *         Improvement: 9.5 times
	 */
	public static boolean isArpa(String domain) {
		// Stupid-fast implementation - performance vs. complexity gone too far?
		int pos = domain.length()-13;
		char ch;
		return
			pos>=0
			&&      domain.charAt(pos++) =='.'
			&& ((ch=domain.charAt(pos++))=='i' || ch=='I')
			&& ((ch=domain.charAt(pos++))=='n' || ch=='N')
			&&      domain.charAt(pos++) =='-'
			&& ((ch=domain.charAt(pos++))=='a' || ch=='A')
			&& ((ch=domain.charAt(pos++))=='d' || ch=='D')
			&& ((ch=domain.charAt(pos++))=='d' || ch=='D')
			&& ((ch=domain.charAt(pos++))=='r' || ch=='R')
			&&      domain.charAt(pos++) =='.'
			&& ((ch=domain.charAt(pos++))=='a' || ch=='A')
			&& ((ch=domain.charAt(pos++))=='r' || ch=='R')
			&& ((ch=domain.charAt(pos++))=='p' || ch=='P')
			&& ((ch=domain.charAt(pos  ))=='a' || ch=='A')
		;
		//return domain.toLowerCase(Locale.ENGLISH).endsWith(".in-addr.arpa");
	}
	/*
	private static boolean isArpaBenchmark(String domain) {
		return domain.toLowerCase(Locale.ENGLISH).endsWith(".in-addr.arpa");
	}

	private static void benchmark() {
		final int iterations = 10000000;
		long startTime = System.nanoTime();
		for(int i=0; i<iterations; i++) {
			isArpa("subnet0.144.71.64.in-addr.arpa");
		}
		long midTime = System.nanoTime();
		for(int i=0; i<iterations; i++) {
			isArpaBenchmark("subnet0.144.71.64.in-addr.arpa");
		}
		long endTime = System.nanoTime();
		System.out.println("Old: "+BigDecimal.valueOf(endTime-midTime, 6)+" ms");
		System.out.println("New: "+BigDecimal.valueOf(midTime-startTime, 6)+" ms");
	}
	public static void main(String[] args) {
		for(int c=0; c<100; c++) benchmark();
	}*/

	private static final char[] localhostCharsLower = "localhost".toCharArray();
	private static final char[] localhostCharsUpper = "LOCALHOST".toCharArray();
	private static boolean isLocalhost(String domain) {
		assert localhostCharsLower.length == localhostCharsUpper.length;
		int len = localhostCharsUpper.length;
		if(domain.length()!=len) return false;
		for(int i=0; i<len; i++) {
			char ch = domain.charAt(i);
			if(ch!=localhostCharsLower[i] && ch!=localhostCharsUpper[i]) return false;
		}
		return true;
	}

	private static final char[] localhostLocaldomainCharsLower = "localhost.localdomain".toCharArray();
	private static final char[] localhostLocaldomainCharsUpper = "LOCALHOST.LOCALDOMAIN".toCharArray();
	private static boolean isLocalhostLocaldomain(String domain) {
		assert localhostLocaldomainCharsLower.length == localhostLocaldomainCharsUpper.length;
		int len = localhostLocaldomainCharsUpper.length;
		if(domain.length()!=len) return false;
		for(int i=0; i<len; i++) {
			char ch = domain.charAt(i);
			if(ch!=localhostLocaldomainCharsLower[i] && ch!=localhostLocaldomainCharsUpper[i]) return false;
		}
		return true;
	}

	/**
	 * Validates a domain name, but doesn't allow an ending period.
	 *
	 * @see DomainLabel#validate(java.lang.String)
	 */
	public static ValidationResult validate(String domain) {
		if(domain==null) return new InvalidResult(ApplicationResourcesAccessor.accessor, "DomainName.validate.isNull");
		int len = domain.length();
		if(len==0) return new InvalidResult(ApplicationResourcesAccessor.accessor, "DomainName.validate.empty");
		if(
			!isLocalhost(domain)
			&& !isLocalhostLocaldomain(domain)
		) {
			char ch;
			if(
				// "default".equalsIgnoreCase(domain)
				domain.length()==7
				&& ((ch=domain.charAt(0))=='d' || ch=='D')
				&& ((ch=domain.charAt(1))=='e' || ch=='E')
				&& ((ch=domain.charAt(2))=='f' || ch=='F')
				&& ((ch=domain.charAt(3))=='a' || ch=='A')
				&& ((ch=domain.charAt(4))=='u' || ch=='U')
				&& ((ch=domain.charAt(5))=='l' || ch=='L')
				&& ((ch=domain.charAt(6))=='t' || ch=='T')
			) return new InvalidResult(ApplicationResourcesAccessor.accessor, "DomainName.validate.isDefault");
			if(len>MAX_LENGTH) return new InvalidResult(ApplicationResourcesAccessor.accessor, "DomainName.validate.tooLong", MAX_LENGTH, len);
			boolean isArpa = isArpa(domain);
			int labelStart = 0;
			for(int pos=0; pos<len; pos++) {
				if(domain.charAt(pos)=='.') {
					// For reverse IP address delegation, if the domain ends with ".in-addr.arpa", the first label may also be in the format "##/##".
					if(!isArpa || labelStart!=0 || !isArpaDelegationFirstLabel(domain, labelStart, pos)) {
						ValidationResult result = DomainLabel.validate(domain, labelStart, pos);
						if(!result.isValid()) return result;
					}
					labelStart = pos+1;
				}
			}
			ValidationResult result = DomainLabel.validate(domain, labelStart, len);
			if(!result.isValid()) return result;
			// Last domain label must be alphabetic (not be all numeric)
			if(isNumeric(domain, labelStart, len)) return new InvalidResult(ApplicationResourcesAccessor.accessor, "DomainName.validate.lastLabelAllDigits");
			// Last label must be a valid top level domain
			String lastLabel = domain.substring(labelStart, len);
			if(TopLevelDomain.getByLabel(lastLabel)==null) return new InvalidResult(ApplicationResourcesAccessor.accessor, "DomainName.validate.notEndTopLevelDomain", lastLabel);
		}
		return ValidResult.getInstance();
	}

	private static final ConcurrentMap<String,DomainName> interned = new ConcurrentHashMap<String,DomainName>();

	/**
	 * @param domain  when {@code null}, returns {@code null}
	 */
	public static DomainName valueOf(String domain) throws ValidationException {
		if(domain == null) return null;
		//DomainName existing = interned.get(domain);
		//return existing!=null ? existing : new DomainName(domain);
		return new DomainName(domain);
	}

	private String domain;
	private String lowerDomain;

	private DomainName(String domain) throws ValidationException {
		this(domain, domain.toLowerCase(Locale.ROOT));
	}

	private DomainName(String domain, String lowerDomain) throws ValidationException {
		this.domain = domain;
		this.lowerDomain = lowerDomain;
		validate();
	}

	private void validate() throws ValidationException {
		ValidationResult result = validate(domain);
		if(!result.isValid()) throw new ValidationException(result);
	}

	@Override
	public boolean equals(Object O) {
		return
			O!=null
			&& O instanceof DomainName
			&& lowerDomain.equals(((DomainName)O).lowerDomain)
		;
	}

	@Override
	public int hashCode() {
		return lowerDomain.hashCode();
	}

	/**
	 * TODO: Should not be public once all classes using validator types.
	 */
	public static int compareLabels(String labels1, String labels2) {
		if(labels1==labels2) return 0; // Shortcut for interned
		while(labels1.length()>0 && labels2.length()>0) {
			int pos=labels1.lastIndexOf('.');
			String section1;
			if(pos==-1) {
				section1=labels1;
				labels1="";
			} else {
				section1=labels1.substring(pos+1);
				labels1=labels1.substring(0, pos);
			}

			pos=labels2.lastIndexOf('.');
			String section2;
			if(pos==-1) {
				section2=labels2;
				labels2="";
			} else {
				section2=labels2.substring(pos+1);
				labels2=labels2.substring(0, pos);
			}

			int diff=ComparatorUtils.compareIgnoreCaseConsistentWithEquals(section1, section2);
			if(diff!=0) return diff;
		}
		return ComparatorUtils.compareIgnoreCaseConsistentWithEquals(labels1, labels2);
	}

	/**
	 * Sorts by top level domain, then subdomain, then sub-subdomain, ...
	 */
	@Override
	public int compareTo(DomainName other) {
		if(this==other) return 0;
		return compareLabels(domain, other.domain);
	}

	@Override
	public String toString() {
		return domain;
	}

	/**
	 * Gets the lower-case form of the domain.  If two different domains are
	 * interned and their toLowerCase is the same String instance, then they are
	 * equal in case-insensitive manner.
	 */
	public String toLowerCase() {
		return lowerDomain;
	}

	public boolean isArpa() {
		return isArpa(domain);
	}

	/**
	 * Interns this domain much in the same fashion as <code>String.intern()</code>.
	 *
	 * @see  String#intern()
	 */
	@Override
	public DomainName intern() {
		try {
			DomainName existing = interned.get(domain);
			if(existing==null) {
				String internedDomain = domain.intern();
				String internedLowerDomain = lowerDomain.intern();
				DomainName addMe = domain==internedDomain && lowerDomain==internedLowerDomain ? this : new DomainName(internedDomain);
				existing = interned.putIfAbsent(internedDomain, addMe);
				if(existing==null) existing = addMe;
			}
			return existing;
		} catch(ValidationException err) {
			// Should not fail validation since original object passed
			throw new AssertionError(err.getMessage());
		}
	}

	@Override
	public com.aoindustries.net.dto.DomainName getDto() {
		return new com.aoindustries.net.dto.DomainName(domain);
	}

	// <editor-fold defaultstate="collapsed" desc="FastExternalizable">
	private static final long serialVersionUID = 2384488670340662487L;

	public DomainName() {
	}

	@Override
	public long getSerialVersionUID() {
		return serialVersionUID;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		FastObjectOutput fastOut = FastObjectOutput.wrap(out);
		try {
			fastOut.writeFastUTF(domain);
		} finally {
			fastOut.unwrap();
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		if(domain!=null) throw new IllegalStateException();
		FastObjectInput fastIn = FastObjectInput.wrap(in);
		try {
			domain = fastIn.readFastUTF();
			lowerDomain = domain.toLowerCase(Locale.ROOT);
		} finally {
			fastIn.unwrap();
		}
	}

	@Override
	public void validateObject() throws InvalidObjectException {
		try {
			validate();
		} catch(ValidationException err) {
			InvalidObjectException newErr = new InvalidObjectException(err.getMessage());
			newErr.initCause(err);
			throw newErr;
		}
	}
	// </editor-fold>
}
