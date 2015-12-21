package org.huihoo.ofbiz.smart.base.util;


import org.huihoo.ofbiz.smart.base.cache.Cache;
import org.huihoo.ofbiz.smart.base.cache.DefaultCache;
import org.huihoo.ofbiz.smart.base.location.FlexibleLocation;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.*;

public class I18NUtil {

  private final static String TAG = I18NUtil.class.getName();

  private final static Cache<String, ResourceBundle> RESOURCE_BUNDLE_CACHE = new DefaultCache<>();

  static {
    RESOURCE_BUNDLE_CACHE.start("i18n-resources-cache");
  }

  public static ResourceBundle getResourceBundle(String resourceName, Locale locale) {
    if (CommUtil.isEmpty(resourceName)) {
      throw new IllegalArgumentException("The resourceName is empty.");
    }
    if (locale == null) {
      throw new IllegalArgumentException("The locale is NULL.");
    }

    String key = resourceName + "_" + locale.getLanguage();
    ResourceBundle resourceBundle = RESOURCE_BUNDLE_CACHE.get(key);

    if (resourceBundle != null) {
      Log.w("The resource [" + key + "] from cache.", TAG);
      return resourceBundle;
    }

    if (resourceName.startsWith("i18n")) {
      try {
        if (!resourceName.endsWith(".xml")) {
          resourceName += ".xml";
        }
        ExtendedProperties properties =
            new ExtendedProperties(FlexibleLocation.resolveLocation(resourceName).openConnection().getURL(), locale);
        resourceBundle = new XmlResourceBundle(properties, locale);
        RESOURCE_BUNDLE_CACHE.put(key, resourceBundle);
      } catch (IOException e) {
        Log.w("Unable to load resource [" + resourceName + "]", TAG);
      }
    } else {
      resourceBundle = ResourceBundle.getBundle(resourceName, locale);
    }

    if (resourceBundle != null) {
      RESOURCE_BUNDLE_CACHE.put(key, resourceBundle);
    }

    return resourceBundle;
  }

  public static void clear() {
    RESOURCE_BUNDLE_CACHE.clear();
  }

  public static class ExtendedProperties extends Properties implements Serializable {
    private static final long serialVersionUID = 1L;

    public ExtendedProperties() {
      super();
    }

    public ExtendedProperties(Properties defaults) {
      super(defaults);
    }


    public ExtendedProperties(URL url, Locale locale) throws IOException, InvalidPropertiesFormatException {
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

  /**
   * 将自定义XML格式的属性文件转换成<code>Properties</code>实例
   * <p>
   * 自定义XML资源文件的格式为:<br />
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
    if (properties == null) properties = new Properties();

    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser parser;
    try {
      parser = factory.newSAXParser();
      parser.parse(in, new SaxHandler(properties, locale));
    } catch (ParserConfigurationException | SAXException | IOException e) {
      throw new IllegalArgumentException(e);
    }

    return properties;
  }

  public static class SaxHandler extends DefaultHandler {
    String key;
    String langValue;
    String content;
    Properties properties;
    Locale locale;

    public SaxHandler(Properties properties, Locale locale) {
      this.properties = properties;
      this.locale = locale;
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
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
          String correctedLocaleString = localeString.replace('_', '-');
          if (localeString.equals(langValue) || correctedLocaleString.equals(langValue)) {
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


  public static class XmlResourceBundle extends ResourceBundle implements Serializable {

    protected Properties properties = null;
    protected Locale locale = null;
    protected int hashCode = hashCode();

    public XmlResourceBundle() {

    }

    public XmlResourceBundle(Properties properties, Locale locale) {
      this.properties = properties;
      this.locale = locale;
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


    @NotNull
    @Override
    public Enumeration<String> getKeys() {
      return new Enumeration<String>() {
        Iterator<String> iter = CommUtil.cast(properties.keySet().iterator());

        @Override
        public boolean hasMoreElements() {
          return iter.hasNext();
        }

        @Override
        public String nextElement() {
          return iter.next();
        }
      };
    }
  }
}
