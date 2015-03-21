package org.huihoo.ofbiz.smart.webapp.control;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.huihoo.ofbiz.smart.base.utils.StringUtils;
import org.huihoo.ofbiz.smart.service.ModelService.ServiceEventAction;

/**
 * 核心配置文件加载器
 * 
 * @author huangbohua
 * 
 */
public class ConfigXMLLoader {



  private static final String DEFAULT_CONFIG_FILE_NAME = "/action-map.xml";

  public static ActionMap actionMap = new ActionMap();

  public static void loadXmlConfig(String name) throws ParserConfigurationException, SAXException,
          IOException {
    if (name == null) name = DEFAULT_CONFIG_FILE_NAME;


    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser parser = factory.newSAXParser();
    InputStream in = ConfigXMLLoader.class.getResourceAsStream(name);
    parser.parse(in, new SaxHandler());


  }

  public static class SaxHandler extends DefaultHandler {
    private String content = null;
    private Action action = null;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      switch (qName) {
        case "action":
          action = new Action();
          action.uri = attributes.getValue("uri");
          action.allowMethod = attributes.getValue("allow-method");
          break;
        case "event":
          Event event = new Event();
          event.type = attributes.getValue("type");
          event.path = attributes.getValue("path");
          event.sname = attributes.getValue("sname");
          event.invoke = attributes.getValue("invoke");
          event.defaultEntityName = attributes.getValue("default-entity-name");
          event.condition = attributes.getValue("condition");
          action.event = event;
          break;
        case "response":
          Response response = new Response();
          response.type = attributes.getValue("type");
          response.layout = attributes.getValue("layout");
          response.value = attributes.getValue("value");
          action.response = response;
          break;
        case "action-interceptor":
          ActionInterceptor actionInterceptor = new ActionInterceptor();
          actionInterceptor.interceptorName = attributes.getValue("name");
          actionInterceptor.triggerAt = attributes.getValue("trigger-at");
          action.actionInterceptor = actionInterceptor;
          break;
        case "service-event-action":
          ServiceEventAction sea = new ServiceEventAction();
          sea.seaName = attributes.getValue("name");
          sea.triggerAt = attributes.getValue("trigger-at");
          action.sea = sea;
          break;
      }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
      switch (qName) {
        case "action":
          actionMap.actions.add(action);
          break;
        case "app-name":
          actionMap.appName = content;
          break;
        case "include-action-file":
          actionMap.includeActionFile = content;
          if (!StringUtils.isEmpty(actionMap.includeActionFile)) {
            String[] includeFiles = actionMap.includeActionFile.split(",");
            for (String includeFile : includeFiles) {
              try {
                loadXmlConfig(includeFile);
              } catch (ParserConfigurationException e) {
                throw new SAXException(e);
              } catch (IOException e) {
                throw new SAXException(e);
              }
            }
          }
          break;
        case "env-mode":
          actionMap.envMode = content;
          break;
        case "model-base-package":
          actionMap.modelBasePackage = content;
          break;
        case "jsp-view-base-path":
          actionMap.jspViewBasePath = content;
          break;
        case "action-uri-suffix":
          actionMap.actionUriSuffix = content;
          break;
        case "config-properties-file":
          actionMap.configPropertiesFile = content;
          break;
        case "page-title":
          action.pageTitle = content;
          break;
        case "more-css":
          action.moreCss = content;
          break;
        case "more-styles":
          action.moreJavascripts = content;
          break;

      }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
      content = new String(ch, start, length);
    }


  }

  public static class ActionMap implements Cloneable {
    public String appName;
    public String includeActionFile;
    public String envMode;
    public String modelBasePackage;
    public String jspViewBasePath;
    public String actionUriSuffix;
    public String uploadRelativePath;
    public String configPropertiesFile;
    public List<Action> actions = new ArrayList<ConfigXMLLoader.Action>();

    @Override
    public Object clone() {
      ActionMap o = null;
      try {
        o = (ActionMap) super.clone();
      } catch (CloneNotSupportedException e) {
        e.printStackTrace();
      }
      return o;
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("ActionMap [appName=");
      builder.append(appName);
      builder.append(", includeActionFile=");
      builder.append(includeActionFile);
      builder.append(", envMode=");
      builder.append(envMode);
      builder.append(", modelBasePackage=");
      builder.append(modelBasePackage);
      builder.append(", jspViewBasePath=");
      builder.append(jspViewBasePath);
      builder.append(", actionUriSuffix=");
      builder.append(actionUriSuffix);
      builder.append(", uploadRelativePath=");
      builder.append(uploadRelativePath);
      builder.append(", configPropertiesFile=");
      builder.append(configPropertiesFile);
      builder.append(", actions=");
      builder.append(actions);
      builder.append("]");
      return builder.toString();
    }



  }

  public static class Action implements Cloneable {
    public String uri;
    public String allowMethod;
    public String moreCss;
    public String pageTitle;
    public String moreJavascripts;
    public Event event;
    public Response response;
    public ActionInterceptor actionInterceptor;
    public ServiceEventAction sea;

    @Override
    public Object clone() {
      Action o = null;
      try {
        o = (Action) super.clone();
      } catch (CloneNotSupportedException e) {
        e.printStackTrace();
      }
      return o;
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("Action [uri=");
      builder.append(uri);
      builder.append(", allowMethod=");
      builder.append(allowMethod);
      builder.append(", moreCss=");
      builder.append(moreCss);
      builder.append(", pageTitle=");
      builder.append(pageTitle);
      builder.append(", moreJavascripts=");
      builder.append(moreJavascripts);
      builder.append(", event=");
      builder.append(event);
      builder.append(", response=");
      builder.append(response);
      builder.append("]");
      return builder.toString();
    }
  }

  public static class Event implements Cloneable {
    public String type;
    public String sname;
    public String path;
    public String invoke;
    public String defaultEntityName;
    public String condition;

    @Override
    public Object clone() {
      Event o = null;
      try {
        o = (Event) super.clone();
      } catch (CloneNotSupportedException e) {
        e.printStackTrace();
      }
      return o;
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("Event [type=");
      builder.append(type);
      builder.append(", sname=");
      builder.append(sname);
      builder.append(", path=");
      builder.append(path);
      builder.append(", invoke=");
      builder.append(invoke);
      builder.append(", defaultEntityName=");
      builder.append(defaultEntityName);
      builder.append(", condition=");
      builder.append(condition);
      builder.append("]");
      return builder.toString();
    }
  }

  public static class ActionInterceptor implements Cloneable {
    public String interceptorName;
    public String triggerAt;

    @Override
    public String toString() {
      return "ActionInterceptor [interceptorName=" + interceptorName + ", triggerAt=" + triggerAt
              + "]";
    }

    @Override
    public Object clone() {
      ActionInterceptor o = null;
      try {
        o = (ActionInterceptor) super.clone();
      } catch (CloneNotSupportedException e) {
        e.printStackTrace();
      }
      return o;
    }
  }



  public static class Response implements Cloneable {
    public String type;
    public String layout;
    public String value;

    @Override
    public Object clone() {
      Response o = null;
      try {
        o = (Response) super.clone();
      } catch (CloneNotSupportedException e) {
        e.printStackTrace();
      }
      return o;
    }

    @Override
    public String toString() {
      return "Response [type=" + type + ", layout=" + layout + ", value=" + value + "]";
    }
  }

}
