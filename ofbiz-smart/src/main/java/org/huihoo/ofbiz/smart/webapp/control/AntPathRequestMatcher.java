/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.huihoo.ofbiz.smart.webapp.control;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Matcher which compares a pre-defined ant-style pattern against the URL (
 * {@code servletPath + pathInfo}) of an {@code HttpServletRequest}. The query
 * string of the URL is ignored and matching is case-insensitive.
 * <p>
 * Using a pattern value of {@code /**} or {@code **} is treated as a universal
 * match, which will match any request. Patterns which end with {@code /**} (and
 * have no other wildcards) are optimized by using a substring match &mdash; a
 * pattern of {@code /aaa/**} will match {@code /aaa}, {@code /aaa/} and any
 * sub-directories, such as {@code /aaa/bbb/ccc}.
 * <p>
 * For all other cases, Spring's {@link AntPathMatcher} is used to perform the
 * match. See the Spring documentation for this class for comprehensive
 * information on the syntax used.
 * <p>
 * This is essentially a direct copy of the
 * {@code org.springframework.security.web.util.AntPathRequestMatcher}
 * implementation in Spring Security 3.1, backported here for matching against
 * BlazeDS Endpoint URL patterns so as to retain compatibility with Spring
 * Security 3.0, and with the matching against HTTP method removed since that is
 * unnecessary with Flex requests.
 * 
 * @author Luke Taylor
 * @author Jeremy Grelle
 * @since 1.5
 * 
 */
public class AntPathRequestMatcher {
    private final static Logger LOG = LoggerFactory.getLogger(AntPathRequestMatcher.class);
    private static final String MATCH_ALL = "/**";

    private final Matcher matcher;
    private final String pattern;


    /**
     * Creates a matcher with the specific pattern which will match all HTTP
     * methods.
     * 
     * @param pattern
     *            the ant pattern to use for matching
     */
    public AntPathRequestMatcher(String pattern) {

        if (pattern.equals(MATCH_ALL) || pattern.equals("**")) {
            pattern = MATCH_ALL;
            matcher = null;
        }
        else {
            pattern = pattern.toLowerCase();

            // If the pattern ends with {@code /**} and has no other wildcards,
            // then optimize to a sub-path match
            if (pattern.endsWith(MATCH_ALL) && pattern.indexOf('?') == -1
                    && pattern.indexOf("*") == pattern.length() - 2) {
                matcher = new SubpathMatcher(pattern.substring(0, pattern.length() - 3));
            }
            else {
                matcher = new SpringAntMatcher(pattern);
            }
        }

        this.pattern = pattern;
    }


    /**
     * Returns true if the configured pattern (and HTTP-Method) match those of
     * the supplied request.
     * 
     * @param request
     *            the request to match against. The ant pattern will be matched
     *            against the {@code servletPath} + {@code pathInfo} of the
     *            request.
     */
    public boolean matches(HttpServletRequest request) {
        if (pattern.equals(MATCH_ALL)) {
            if (LOG.isDebugEnabled()) {
             //   LOG.debug("Request '" + getRequestPath(request) + "' matched by universal pattern '/**'");
            }

            return true;
        }

        String url = getRequestPath(request);
        boolean match = matcher.matches(url);
        if (LOG.isDebugEnabled()) {
        //    LOG.debug("Checking match of request : '" + url + "'; against '" + pattern + "'; matches > "+match);
        }

        return match ;
    }


    private String getRequestPath(HttpServletRequest request) {
        String url = request.getServletPath();

        if (request.getPathInfo() != null) {
            url += request.getPathInfo();
        }

        url = url.toLowerCase();

        return url;
    }


    public String getPattern() {
        return pattern;
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AntPathRequestMatcher)) {
            return false;
        }
        AntPathRequestMatcher other = (AntPathRequestMatcher) obj;
        return this.pattern.equals(other.pattern);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Ant [pattern='").append(pattern).append("'");

        sb.append("]");

        return sb.toString();
    }

    private static interface Matcher {
        boolean matches(String path);
    }

    private static class SpringAntMatcher implements Matcher {
        private static final AntPathMatcher antMatcher = new AntPathMatcher();
        private final String pattern;


        private SpringAntMatcher(String pattern) {
            this.pattern = pattern;
        }


        public boolean matches(String path) {
            return antMatcher.match(pattern, path);
        }
    }

    /**
     * Optimized matcher for trailing wildcards
     */
    private static class SubpathMatcher implements Matcher {
        private final String subpath;
        private final int length;


        private SubpathMatcher(String subpath) {
            assert !subpath.contains("*");
            this.subpath = subpath;
            this.length = subpath.length();
        }


        public boolean matches(String path) {
            return path.startsWith(subpath) && (path.length() == length || path.charAt(length) == '/');
        }
    }
}
