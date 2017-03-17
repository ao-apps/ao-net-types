/*
 * ao-net-types - Networking-related value types for Java.
 * Copyright (C) 2010-2013, 2016, 2017  AO Industries, Inc.
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
import com.aoindustries.lang.ObjectUtils;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Represents an email address.  Email addresses must:
 * <ul>
 *   <li>Be non-null</li>
 *   <li>Be non-empty</li>
 *   <li>Contain a single @, but not at the beginning or end</li>
 *   <li>Local part must adhere to RFC 5322: {@link http://en.wikipedia.org/wiki/E-mail_address#RFC_specification}</li>
 * </ul>
 * 
 * @author  AO Industries, Inc.
 */
final public class Email implements
	Comparable<Email>,
	FastExternalizable,
	ObjectInputValidation,
	DtoFactory<com.aoindustries.net.dto.Email>,
	Internable<Email>
{

	public static final int MAX_LENGTH = 254;

	public static final int MAX_LOCAL_PART_LENGTH = 64;

	/**
	 * Validates a complete email address.  Splits on @ and calls <code>validate</code> on local part and domain.
	 *
	 * @see #validate(java.lang.String, com.aoindustries.aoserv.client.validator.DomainName)
	 */
	public static ValidationResult validate(String email) {
		// Be non-null
		if(email==null) return new InvalidResult(ApplicationResourcesAccessor.accessor, "Email.validate.isNull");
		// Be non-empty
		if(email.length()==0) return new InvalidResult(ApplicationResourcesAccessor.accessor, "Email.validate.empty");
		int atPos = email.indexOf('@');
		if(atPos==-1) return new InvalidResult(ApplicationResourcesAccessor.accessor, "Email.validate.noAt");
		return validate(email.substring(0, atPos), email.substring(atPos+1));
	}

	/**
	 * Validates the local part of the email address (before the @ symbol), as well as additional domain rules.
	 */
	public static ValidationResult validate(String localPart, String domain) {
		if(domain!=null) {
			ValidationResult result = DomainName.validate(domain);
			if(!result.isValid()) return result;
		}
		return validateImpl(localPart, domain);
	}

	/**
	 * Validates the local part of the email address (before the @ symbol), as well as additional domain rules.
	 */
	public static ValidationResult validate(String localPart, DomainName domain) {
		return validateImpl(localPart, ObjectUtils.toString(domain));
	}

	private static final boolean[] validChars = new boolean[128];
	static {
		for(int ch=0; ch<128; ch++) {
			validChars[ch] =
				(ch>='A' && ch<='Z')
				|| (ch>='a' && ch<='z')
				|| (ch>='0' && ch<='9')
				|| ch=='!'
				|| ch=='#'
				|| ch=='$'
				|| ch=='%'
				|| ch=='&'
				|| ch=='\''
				|| ch=='*'
				|| ch=='+'
				|| ch=='-'
				|| ch=='/'
				|| ch=='='
				|| ch=='?'
				|| ch=='^'
				|| ch=='_'
				|| ch=='`'
				|| ch=='{'
				|| ch=='|'
				|| ch=='}'
				|| ch=='~'
				|| ch=='.' // Dot here for completeness, but algorithm below will not use it
			;
		}
	}

	/**
	 * Validates the local part of the email address (before the @ symbol), as well as additional domain rules.
	 */
	private static ValidationResult validateImpl(String localPart, String domain) {
		if(localPart==null) return new InvalidResult(ApplicationResourcesAccessor.accessor, "Email.validate.localePart.isNull");
		if(domain==null) return new InvalidResult(ApplicationResourcesAccessor.accessor, "Email.validate.domain.isNull");
		if(domain.lastIndexOf('.')==-1) return new InvalidResult(ApplicationResourcesAccessor.accessor, "Email.validate.domain.noDot");
		if(DomainName.isArpa(domain)) return new InvalidResult(ApplicationResourcesAccessor.accessor, "Email.validate.domain.isArpa");
		int len = localPart.length();
		int totalLen = len + 1 + domain.length();
		if(totalLen>MAX_LENGTH) return new InvalidResult(ApplicationResourcesAccessor.accessor, "Email.validate.tooLong", MAX_LENGTH, totalLen);

		if(len==0) return new InvalidResult(ApplicationResourcesAccessor.accessor, "Email.validate.localePart.empty");
		if(len>MAX_LOCAL_PART_LENGTH) return new InvalidResult(ApplicationResourcesAccessor.accessor, "Email.validate.localePart.tooLong", MAX_LOCAL_PART_LENGTH, len);
		for(int pos=0; pos<len; pos++) {
			char ch = localPart.charAt(pos);
			if(ch=='.') {
				if(pos==0) return new InvalidResult(ApplicationResourcesAccessor.accessor, "Email.validate.localePart.startsDot");
				if(pos==(len-1)) return new InvalidResult(ApplicationResourcesAccessor.accessor, "Email.validate.localePart.endsDot");
				if(localPart.charAt(pos-1)=='.') return new InvalidResult(ApplicationResourcesAccessor.accessor, "Email.validate.localePart.doubleDot", pos-1);
			} else if(ch>=128 || !validChars[ch]) return new InvalidResult(ApplicationResourcesAccessor.accessor, "Email.validate.localePart.invalidCharacter", ch, pos);
		}
		return ValidResult.getInstance();
	}

	private static final ConcurrentMap<DomainName,ConcurrentMap<String,Email>> interned = new ConcurrentHashMap<>();

	/**
	 * If email is null, then returns is null.
	 *
	 * @see #valueOf(java.lang.String, com.aoindustries.aoserv.client.validator.DomainName)
	 */
	public static Email valueOf(String email) throws ValidationException {
		if(email==null) return null;
		// Be non-empty
		if(email.length()==0) throw new ValidationException(new InvalidResult(ApplicationResourcesAccessor.accessor, "Email.validate.empty"));
		int atPos = email.indexOf('@');
		if(atPos==-1) throw new ValidationException(new InvalidResult(ApplicationResourcesAccessor.accessor, "Email.validate.noAt"));
		return valueOf(email.substring(0, atPos), DomainName.valueOf(email.substring(atPos+1)));
	}

	public static Email valueOf(String localPart, DomainName domain) throws ValidationException {
		//ConcurrentMap<String,Email> domainMap = interned.get(domain);
		//if(domainMap!=null) {
		//    Email existing = domainMap.get(localPart);
		//    if(existing!=null) return existing;
		//}
		return new Email(localPart, domain);
	}

	private String localPart;
	private DomainName domain;

	private Email(String localPart, DomainName domain) throws ValidationException {
		this.localPart = localPart;
		this.domain = domain;
		validate();
	}

	private void validate() throws ValidationException {
		ValidationResult result = validate(localPart, domain.toString());
		if(!result.isValid()) throw new ValidationException(result);
	}

	@Override
	public boolean equals(Object O) {
		if(O==null || !(O instanceof Email)) return false;
		Email other = (Email)O;
		return
			localPart.equals(other.localPart)
			&& domain.equals(other.domain)
		;
	}

	@Override
	public int hashCode() {
		return localPart.hashCode() * 31 + domain.hashCode();
	}

	/**
	 * Sorts by domain and then by local part.
	 */
	@Override
	public int compareTo(Email other) {
		if(this==other) return 0;
		int diff = domain.compareTo(other.domain);
		if(diff!=0) return diff;
		return ComparatorUtils.compareIgnoreCaseConsistentWithEquals(localPart, other.localPart);
	}

	@Override
	public String toString() {
		return localPart + '@' + domain;
	}

	/**
	 * Interns this email much in the same fashion as <code>String.intern()</code>.
	 *
	 * @see  String#intern()
	 */
	@Override
	public Email intern() {
		try {
			// Intern the domain
			DomainName internedDomain = domain.intern();

			// Atomically get/create the per-domain map
			ConcurrentMap<String,Email> domainMap = interned.get(internedDomain);
			if(domainMap==null) {
				ConcurrentMap<String,Email> newDomainInterned = new ConcurrentHashMap<>();
				domainMap = interned.putIfAbsent(internedDomain, newDomainInterned);
				if(domainMap==null) domainMap = newDomainInterned;
			}

			// Atomically get/create the Email object within the domainMap
			Email existing = domainMap.get(localPart);
			if(existing==null) {
				String internedLocalPart = localPart.intern();
				Email addMe = localPart==internedLocalPart && domain==internedDomain ? this : new Email(internedLocalPart, internedDomain);
				existing = domainMap.putIfAbsent(internedLocalPart, addMe);
				if(existing==null) existing = addMe;
			}
			return existing;
		} catch(ValidationException err) {
			// Should not fail validation since original object passed
			throw new AssertionError(err.getMessage());
		}
	}

	public String getLocalPart() {
		return localPart;
	}

	public DomainName getDomain() {
		return domain;
	}

	@Override
	public com.aoindustries.aoserv.client.dto.Email getDto() {
		return new com.aoindustries.aoserv.client.dto.Email(localPart, domain.getDto());
	}

	// <editor-fold defaultstate="collapsed" desc="FastExternalizable">
	private static final long serialVersionUID = 1812494521843295031L;

	public Email() {
	}

	@Override
	public long getSerialVersionUID() {
		return serialVersionUID;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		FastObjectOutput fastOut = FastObjectOutput.wrap(out);
		try {
			fastOut.writeFastUTF(localPart);
			fastOut.writeObject(domain);
		} finally {
			fastOut.unwrap();
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		if(localPart!=null) throw new IllegalStateException();
		FastObjectInput fastIn = FastObjectInput.wrap(in);
		try {
			localPart = fastIn.readFastUTF();
			domain = (DomainName)fastIn.readObject();
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
