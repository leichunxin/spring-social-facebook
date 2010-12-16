/*
 * Copyright 2010 the original author or authors.
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
package org.springframework.social.facebook;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that indicates to {@link FacebookWebArgumentResolver} that a
 * controller method parameter should be bound to the Facebook access token
 * given when the user signed in with Facebook.
 * 
 * @author Craig Walls
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FacebookAccessToken {
	/**
	 * Whether the Facebook access token is required.
	 * <p>
	 * Default is <code>true</code>, leading to an exception being thrown in
	 * case the Facebook cookie is missing or if the access token can't be found
	 * in the cookie. Switch this to <code>false</code> if you prefer a
	 * <code>null</value> in case of the missing cookie/token.
	 * </p>
	 */
	boolean required() default true;
}
