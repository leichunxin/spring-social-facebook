/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.social.facebook.web;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.security.crypto.codec.Base64;

/**
 * Helper class for extracting the payload of a signed request sent by Facebook.
 * @author Craig Walls
 */
class SignedRequestDecoder {
	
	private String secret;

	private ObjectMapper objectMapper;

	/**
	 * @param secret the application secret used in creating and verifying the signature of the signed request.
	 */
	public SignedRequestDecoder(String secret) {
		this.secret = secret;
		this.objectMapper = new ObjectMapper();
	}
	
	/**
	 * Decodes a signed request, returning the payload of the signed request as a String
	 * @param signedRequest the value of the signed_request parameter sent by Facebook.
	 * @return the payload of the signed request
	 */
	@SuppressWarnings("unchecked")
	public Map<String, ?> decodeSignedRequest(String signedRequest) throws SignedRequestException {
		String[] split = signedRequest.split("\\.");
		String encodedSignature = split[0];
		String payload = split[1];		
		byte[] signature = base64DecodeToBytes(encodedSignature);
		try {
			Map<String, ?> data = objectMapper.readValue(base64DecodeToString(payload), Map.class);
			if (!data.get("algorithm").equals("HMAC-SHA256")) {
				throw new SignedRequestException("Unknown encryption algorithm: " + data.get("algorithm"));
			}			
			byte[] expectedSignature = encrypt(payload, secret);
			if (!Arrays.equals(expectedSignature, signature)) {
				throw new SignedRequestException("Invalid signature.");
			}			
			return data;
		} catch (IOException e) {
			throw new SignedRequestException("Error parsing payload.", e);
		}
	}

	private String padForBase64(String base64) {
		return base64 + PADDING.substring(0, (4-base64.length() % 4) % 4);
	}

	private byte[] base64DecodeToBytes(String in) {
		return Base64.decode(padForBase64(in.replace('_', '/').replace('-', '+')).getBytes());
	}

	private String base64DecodeToString(String in) {
		return new String(base64DecodeToBytes(in));
	}

	private byte[] encrypt(String base, String key) {
		try {
			SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), HMAC_SHA256_MAC_NAME);
			Mac mac = Mac.getInstance(HMAC_SHA256_MAC_NAME);
			mac.init(secretKeySpec);
			return mac.doFinal(base.getBytes());
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		} catch (InvalidKeyException e) {
			throw new IllegalStateException(e);
		}
	}
	
	private static final String PADDING = "===";

	private static final String HMAC_SHA256_MAC_NAME = "HMACSHA256";

}