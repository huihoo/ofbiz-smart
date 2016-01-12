package org.huihoo.ofbiz.smart.webapp;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.webapp.ActionModel.Action;
import org.huihoo.ofbiz.smart.webapp.ActionModel.Response;
import org.huihoo.ofbiz.smart.webapp.ActionModel.ServiceCall;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



public class ActionModelXmlConfigLoader {
  private final static String TAG = ActionModelXmlConfigLoader.class.getName();
  
  private final static ActionModelXmlConfigLoader ME = new ActionModelXmlConfigLoader();
  
  
  private ActionModelXmlConfigLoader() {
    
  }
  
  
  
  public static ActionModelXmlConfigLoader me() {
    return ME;
  }
  
  
  public void loadXml(String path,List<ActionModel> actionModels) {
    Log.d("XML config path : " + path, TAG);
    File f = new File(path);
    
    File[] configFiles = f.listFiles(new FileFilter() {
      @Override
      public boolean accept(File f) {
        String filename = f.getName();
        if (f.isDirectory() ||
                    (f.isFile() && filename.startsWith("action-map") && filename.endsWith(".xml")))
          return true;
        return false;
      }
    });
    
    if (configFiles == null) {
      throw new IllegalArgumentException("Path [" + path + "] has nothing.");
    }
    
    for (File ff : configFiles) {
      if (ff.isDirectory()) {
        loadXml(ff.getPath(),actionModels);
      } else {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser;
        try {
          parser = factory.newSAXParser();
          ActionModel actionModel = new ActionModel();
          //NOTICE: 如果以File对象进行解析，有可能会报Content is not allowed in prolog的异常
          //parser.parse(ff, new SaxHandler(actionModel));
          parser.parse(new FileInputStream(ff), new SaxHandler(actionModel));
          Log.d("Loaded action config [" + ff.getPath() + "]", TAG);
          actionModels.add(actionModel);
        } catch (ParserConfigurationException e) {
          Log.e(e, "Unable to load action config file [" + ff.getPath() + "]", TAG);
        } catch (SAXException e) {
          Log.e(e, "Unable to load action config file [" + ff.getPath() + "]", TAG);
        } catch (IOException e) {
          Log.e(e, "Unable to load action config file [" + ff.getPath() + "]", TAG);
        }
      }
    }
  }
  
  public static class SaxHandler extends DefaultHandler {
    private String content = null;
    private Action action = null;
    
    private ActionModel actionModel;
    
    public SaxHandler(ActionModel actionModel) {
      this.actionModel = actionModel;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      switch (qName) {
        case "action":
          action = new Action();
          action.uri = attributes.getValue("uri");
          action.navTag = attributes.getValue("nav-tag");
          action.method = attributes.getValue("method") == null ? "all" : attributes.getValue("method");
          action.requireAuth = Boolean.valueOf(attributes.getValue("require-auth") == null ? "false" : attributes.getValue("auth"));
          action.processType = attributes.getValue("process-type") == null ? "byConfig" : attributes.getValue("process-type");
          action.queryCondition = attributes.getValue("query-condition");
          break;
        case "service-call":
          ServiceCall serviceCall = new ServiceCall();
          serviceCall.serviceName = attributes.getValue("service-name");
          serviceCall.entityName = attributes.getValue("entity-name");
          serviceCall.condition = attributes.getValue("condition");
          serviceCall.paramPairs = attributes.getValue("param-pairs");
          serviceCall.orderBy = attributes.getValue("order-by");
          serviceCall.resultName = attributes.getValue("result-name");
          serviceCall.liveTimeInSeconds = attributes.getValue("livetime-in-seconds") == null ? 0 : Integer.parseInt(attributes.getValue("livetime-in-seconds"));
          action.serviceCallList.add(serviceCall);
          break;
        case "response":
          Response response = new Response();
          response.viewName = attributes.getValue("view-name");
          response.layout = attributes.getValue("layout");
          response.viewType = attributes.getValue("view-type");
          action.response = response;
          break;
      }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
      switch (qName) {
        case "action":
          actionModel.actionList.add(action);
          break;
        case "page-title":
          action.pageTitle = content;
          break;
        case "more-css":
          action.moreCss = content;
          break;
        case "more-javascripts":
          action.moreJavascripts = content;
          break;
      }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
      content = new String(ch, start, length);
    }
  }
}
