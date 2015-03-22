/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
package org.huihoo.ofbiz.smart.base.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.huihoo.ofbiz.smart.base.cache.Cache;
import org.huihoo.ofbiz.smart.base.cache.LRUCache;
import org.huihoo.ofbiz.smart.base.location.FlexibleLocation;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



public class PropertiesUtils {

  private static final String module = PropertiesUtils.class.getName();
  protected static Set<String> propertiesNotFound = new HashSet<>();
  protected static Set<String> resourceNotFoundMessagesShown = new HashSet<>();


  public static ResourceBundle getResourceBundle(String resource, Locale locale) {
    if (CommUtils.isEmpty(resource))
      throw new IllegalArgumentException("resource cannot be null or empty");
    if (locale == null) {
      throw new IllegalArgumentException("locale cannot be null");
    }
    ResourceBundle bundle = null;
    try {
      bundle = UtilResourceBundle.getBundle(resource, locale, (ClassLoader) null);
    } catch (MissingResourceException e) {
      String resourceCacheKey = createResourceName(resource, locale, false);
      if (!resourceNotFoundMessagesShown.contains(resourceCacheKey)) {
        resourceNotFoundMessagesShown.add(resourceCacheKey);
        Debug.logInfo("[PropertiesUtils.getPropertyValue] could not find resource: " + resource
                + " for locale " + locale, module);
      }
      throw new IllegalArgumentException("Could not find resource bundle [" + resource
              + "] in the locale [" + locale + "]");
    }
    return bundle;
  }

  public static class UtilResourceBundle extends ResourceBundle {
    private static final Cache<String, UtilResourceBundle> bundleCache = new LRUCache<>(32);
    protected Properties properties = null;
    protected Locale locale = null;
    protected int hashCode = hashCode();

    protected UtilResourceBundle() {}

    public UtilResourceBundle(Properties properties, Locale locale, UtilResourceBundle parent) {
      this.properties = properties;
      this.locale = locale;
      setParent(parent);
      String hashString = properties.toString();
      if (parent != null) {
        hashString += parent.properties;
      }
      this.hashCode = hashString.hashCode();
    }

    public static ResourceBundle getBundle(String resource, Locale locale, ClassLoader loader) {
      String resourceName = createResourceName(resource, locale, true);
      UtilResourceBundle bundle = bundleCache.get(resourceName);
      if (bundle == null) {
        synchronized (bundleCache) {
          double startTime = System.currentTimeMillis();
          ResourceBundle.Control rbc =
                  ResourceBundle.Control
                          .getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT);
          Iterator<Locale> candidateLocalesIter = rbc.getCandidateLocales(resource, locale).iterator();
          UtilResourceBundle parentBundle = null;
          int numProperties = 0;

          while (candidateLocalesIter.hasNext()) {
            Locale candidateLocale = candidateLocalesIter.next();
            candidateLocalesIter.remove();

            String lookupName = createResourceName(resource, candidateLocale, true);
            UtilResourceBundle lookupBundle = bundleCache.get(lookupName);
            if (lookupBundle == null) {
              Properties newProps = getProperties(resource, candidateLocale);
              if (CommUtils.isNotEmpty(newProps)) {
                // The last bundle we found becomes the parent of the new bundle
                parentBundle = bundle;
                bundle = new UtilResourceBundle(newProps, candidateLocale, parentBundle);
                bundleCache.put(lookupName, bundle);
                numProperties = newProps.size();
              }
            } else {
              parentBundle = bundle;
              bundle = lookupBundle;
            }
          }

          if (bundle == null) {
            throw new MissingResourceException("Resource " + resource + ", locale " + locale
                    + " not found", null, null);
          }

          double totalTime = System.currentTimeMillis() - startTime;

          Debug.logInfo("ResourceBundle " + resource + " (" + locale + ") created in " + totalTime
                  / 1000.0 + "s with " + numProperties + " properties", module);


          bundleCache.put(resourceName, bundle);
        }
      }
      return bundle;
    }

    @Override
    public int hashCode() {
      return this.hashCode;
    }

    @Override
    public boolean equals(Object obj) {
      return obj == null ? false : obj.hashCode() == this.hashCode;
    }

    @Override
    public Locale getLocale() {
      return this.locale;
    }

    @Override
    protected Object handleGetObject(String key) {
      return properties.get(key);
    }

    @Override
    public Enumeration<String> getKeys() {
      return new Enumeration<String>() {
        Iterator<String> i = CommUtils.cast(properties.keySet().iterator());

        public boolean hasMoreElements() {
          return (i.hasNext());
        }

        public String nextElement() {
          return i.next();
        }
      };
    }
  }



  public static class ExtendedProperties extends Properties {
    private static final long serialVersionUID = 1L;

    public ExtendedProperties() {
      super();
    }

    public ExtendedProperties(Properties defaults) {
      super(defaults);
    }


    public ExtendedProperties(URL url, Locale locale) throws IOException,
            InvalidPropertiesFormatException {
      InputStream in = new BufferedInputStream(url.openStream());
      if (url.getFile().endsWith(".xml")) {
        xmlToProperties(in, locale, this);
      } else {
        load(in);
      }
      in.close();
    }

    @Override
    public void loadFromXML(InputStream in) throws IOException, InvalidPropertiesFormatException {
      xmlToProperties(in, null, this);
      in.close();
    }
  }



  public static String createResourceName(String resource, Locale locale, boolean removeExtension) {
    String resourceName = resource;
    if (removeExtension) {
      if (resourceName.endsWith(".xml")) {
        resourceName = resourceName.replace(".xml", "");
      } else if (resourceName.endsWith(".properties")) {
        resourceName = resourceName.replace(".properties", "");
      }
    }
    if (locale != null) {
      if (CommUtils.isNotEmpty(locale.toString())) {
        resourceName = resourceName + "_" + locale;
      }
    }
    return resourceName;
  }

  public static Properties getProperties(String resource, Locale locale) {
    if (CommUtils.isEmpty(resource))
      throw new IllegalArgumentException("resource cannot be null or empty");
    if (locale == null) throw new IllegalArgumentException("locale cannot be null");

    Properties properties = null;
    URL url = resolvePropertiesUrl(resource, locale);
    if (url != null) {
      try {
        properties = new ExtendedProperties(url, locale);
      } catch (Exception e) {
        if (CommUtils.isNotEmpty(e.getMessage())) {
          Debug.logInfo(e.getMessage(), module);
        } else {
          Debug.logInfo("Exception thrown: " + e.getClass().getName(), module);
        }
        properties = null;
      }
    }
    if (CommUtils.isNotEmpty(properties)) {
      Debug.logDebug("Loaded " + properties.size() + " properties for: " + resource + " (" + locale
              + ")", module);
    }
    return properties;
  }



  public static URL resolvePropertiesUrl(String resource, Locale locale) {
    Debug.logDebug("resolvePropertiesUrl>"+resource, module);
    if (CommUtils.isEmpty(resource))
      throw new IllegalArgumentException("resource cannot be null or empty");

    String resourceName = createResourceName(resource, locale, false);
    if (propertiesNotFound.contains(resourceName)) {
      return null;
    }
    URL url = null;
    try {
      // Check for complete URL first
      if (resource.endsWith(".xml") || resource.endsWith(".properties")) {
        url = FlexibleLocation.resolveLocation(resource);
        if (url != null) {
          return url;
        }
      }
      // Check for *.properties file
      url = FlexibleLocation.resolveLocation(resourceName + ".properties");
      if (url != null) {
        return url;
      }
      // Check for Java XML properties file
      url = FlexibleLocation.resolveLocation(resourceName + ".xml");
      if (url != null) {
        return url;
      }
      // Check for Custom XML properties file
      url = FlexibleLocation.resolveLocation(resource + ".xml");
      if (url != null) {
        return url;
      }
      url = FlexibleLocation.resolveLocation(resourceName);
      if (url != null) {
        return url;
      }
    } catch (Exception e) {
      Debug.logInfo("Properties resolver: invalid URL - " + e.getMessage(), module);
    }
    if (propertiesNotFound.size() <= 300) {
      // Sanity check - list could get quite large
      propertiesNotFound.add(resourceName);
    }
    return null;
  }

  /**
   * Convert XML property file to Properties instance. This method will convert both the Java XML
   * properties file format and the OFBiz custom XML properties file format.
   * <p>
   * The format of the custom XML properties file is:<br />
   * <br />
   * <code>
   * &lt;resource&gt;<br />
   * &nbsp;&lt;property key="key"&gt;<br />
   * &nbsp;&nbsp;&lt;value xml:lang="locale 1"&gt;Some value&lt;/value&gt<br />
   * &nbsp;&nbsp;&lt;value xml:lang="locale 2"&gt;Some value&lt;/value&gt<br />
   * &nbsp;&nbsp;...<br />
   * &nbsp;&lt;/property&gt;<br />
   * &nbsp;...<br />
   * &lt;/resource&gt;<br /><br /></code> where <em>"locale 1", "locale 2"</em> are valid Locale
   * strings.
   * </p>
   * 
   * @param in XML file InputStream
   * @param locale The desired locale
   * @param properties Optional Properties object to populate
   * @return Properties instance or null if not found
   */
  public static Properties xmlToProperties(InputStream in, Locale locale, Properties properties) {
    if (in == null) {
      throw new IllegalArgumentException("InputStream cannot be null");
    }
    if(properties == null)
      properties = new Properties();
    
    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser parser;
    try {
      parser = factory.newSAXParser();
      parser.parse(in, new SaxHandler(properties,locale));
    } catch (ParserConfigurationException | SAXException | IOException e) {
      throw new IllegalArgumentException(e);
    }

    return properties;
  }


  public static class SaxHandler extends DefaultHandler {
    String key ;
    String langValue;
    String content ;
    Properties properties;
    Locale locale;
    
    public SaxHandler(Properties properties,Locale locale){
      this.properties = properties;
      this.locale = locale;
    }
    
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      switch (qName) {
        case "property":
          key = attributes.getValue("key");
          break;
        case "value":
          langValue = attributes.getValue("xml:lang");
          break;
      }
    }
    
    
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
      switch (qName) {
        case "value":
          String localeString = locale.toString();
          String correctedLocaleString = localeString.replace('_','-');
          if(localeString.equals(langValue) || correctedLocaleString.equals(langValue)){
            properties.put(key, content);
          }
          break;
      }
    }
    
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
      content = new String(ch, start, length);
    }
  }
}
