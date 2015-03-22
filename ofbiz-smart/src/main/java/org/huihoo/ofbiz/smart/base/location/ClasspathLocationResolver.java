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
package org.huihoo.ofbiz.smart.base.location;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;



public class ClasspathLocationResolver implements LocationResolver {

  @Override
  public URL resolveLocation(String location) throws MalformedURLException {
    return resolveLocation(location, null);
  }
  
  
  public URL resolveLocation(String location, ClassLoader loader) throws MalformedURLException {
    String baseLocation = FlexibleLocation.stripLocationType(location);
    // if there is a leading forward slash, remove it
    if (baseLocation.charAt(0) == '/') {
      baseLocation = baseLocation.substring(1);
    }
    return fromResource(baseLocation, loader);
  }



  public static URL fromResource(String resourceName, ClassLoader loader) {
    if (loader == null) {
      try {
        loader = Thread.currentThread().getContextClassLoader();
      } catch (SecurityException e) {
        // Huh? The new object will be created by the current thread, so how is this any different
        // than the previous code?
        ClasspathLocationResolver clr = new ClasspathLocationResolver();
        loader = clr.getClass().getClassLoader();
      }
    }
    URL url = loader.getResource(resourceName);
    if (url != null) {
      return url;
    }
    String propertiesResourceName = null;
    if (!resourceName.endsWith(".properties")) {
      propertiesResourceName = resourceName.concat(".properties");
      url = loader.getResource(propertiesResourceName);
      if (url != null) {
        return url;
      }
    }
    url = ClassLoader.getSystemResource(resourceName);
    if (url != null) {
      return url;
    }
    if (propertiesResourceName != null) {
      url = ClassLoader.getSystemResource(propertiesResourceName);
      if (url != null) {
        return url;
      }
    }
    url = fromFilename(resourceName);
    if (url != null) {
      return url;
    }
    url = fromUrlString(resourceName);
    return url;
  }

  public static URL fromFilename(String filename) {
    if (filename == null) return null;
    File file = new File(filename);
    URL url = null;

    try {
      if (file.exists()) url = file.toURI().toURL();
    } catch (java.net.MalformedURLException e) {
      e.printStackTrace();
      url = null;
    }
    return url;
  }


  public static URL fromUrlString(String urlString) {
    URL url = null;
    try {
      url = new URL(urlString);
    } catch (MalformedURLException e) {}

    return url;
  }
}
