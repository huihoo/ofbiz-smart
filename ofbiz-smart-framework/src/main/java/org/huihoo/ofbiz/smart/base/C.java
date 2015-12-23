package org.huihoo.ofbiz.smart.base;

public final class C {
  public static final String APPLICATION_CONFIG_NAME = "/application.properties";
  public static final String UTF_8 = "UTF-8";
  public static final String PROFILE_NAME = "profile";
  public static final String PROFILE_PRODUCTION = "production";
  
  // ========================================================================================
  // Servlet spec related attributies
  // ========================================================================================
  public static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";
  public static final String TEMP_DIR_CONTEXT_ATTRIBUTE = "javax.servlet.context.tempdir";
  public static final String INCLUDE_REQUEST_URI_ATTRIBUTE = "javax.servlet.include.request_uri";
  
  // ========================================================================================
  // Context Related Flag
  // ========================================================================================
  public static final String APPLICATION_CONFIG_PROP_KEY = "_application_config_properties_";
  public static final String CTX_DELETAGOR = "_ctx_deletagor_"; 
  public static final String CTX_SERVICE_DISPATCHER = "_ctx_service_dispatcher_"; 
  public static final String CTX_WEB_HTTP_SERVLET_REQUEST = "_ctx_httpservletrequest_"; 
  public static final String CTX_WEB_HTTP_SERVLET_RESPONSE = "_ctx_httpservletresponse_"; 
  // ========================================================================================
  // Cache Related Flag
  // ========================================================================================
  public static final String CACHE_PROVIDER_NAME = "cache.provider";
  public static final String CACHE_DEFAULT_TIMETOLIVESECONDS = "cache.default.timeToLiveSeconds";
  public static final String CACHE_DEFAULT_MAXENTRIES = "cache.default.maxEntries";

  // ========================================================================================
  // Reflect Related Flag
  // ========================================================================================
  public static final String PROPERTY_ACCESSOR_PREFIX_GET = "get";
  public static final String PROPERTY_ACCESSOR_PREFIX_HAS = "has";
  public static final String PROPERTY_ACCESSOR_PREFIX_IS = "is";
  public static final String[] PROPERTY_ACCESSOR_PERFIX =
      {PROPERTY_ACCESSOR_PREFIX_GET, PROPERTY_ACCESSOR_PREFIX_HAS, PROPERTY_ACCESSOR_PREFIX_IS};

  // ========================================================================================
  // Service Config Flag
  // ========================================================================================
  public static final String ENTITY_ID_NAME = "id";
  public static final String ENTITY_MODEL_NAME = "model";
  public static final String ENTITY_RETURN_NAME = "returnName";
  public static final String ENTITY_REMOVED_NAME = "removed";
  public static final String ENTITY_MODEL_LIST  = "list";
  public static final String ENTITY_USE_CACHE = "useCache";
  
  public static final String ENTITY_CONDTION = "condition";
  public static final String ENTITY_ANDMAP = "andMap";
  public static final String ENTITY_FIELDS_TO_SELECT = "fieldsToSelect";
  public static final String ENTITY_ORDERBY = "orderBy";
  public static final String ENTITY_ORDERBY_DEFAULT_FIELD = "updatedAt desc";
  public static final String ENTITY_UPDATED_AT = "updatedAt";
  
  public static final String RESPOND_VALIDATION_ERRORS = "validation_errors";
  public static final String SERVICE_ENGITYAUTO_CREATE = "create";
  public static final String SERVICE_ENGITYAUTO_UPDATE = "update";
  public static final String SERVICE_ENGITYAUTO_REMOVE = "remove";
  public static final String SERVICE_ENGITYAUTO_FINDBYID = "findById";
  public static final String SERVICE_ENGITYAUTO_FINDUNIQUEBYAND= "findUniqueByAnd";
  public static final String SERVICE_ENGITYAUTO_FINDLISTBYAND = "findListByAnd";
  public static final String SERVICE_ENGITYAUTO_FINDLISTBYCOND = "findListByCond";
  public static final String SERVICE_ENGITYAUTO_FINDPAGEBYAND = "findPageByAnd";
  public static final String SERVICE_ENGITYAUTO_FINDPAGEBYCOND = "findPageByCondition";
  public static final String SERVICE_SLOWTIME_MILLISECONDS = "service.slowtime.milliseconds";
  
  public static final String SERVICE_SCANNING_NAMES = "service.scanning.resource.names";
  public static final String ENTITY_SCANNING_PACKAGES = "entity.scanning.packages";
  
  // ========================================================================================
  // Datasource Config Flag
  // ========================================================================================
  public static final String CONFIG_DATASOURCE_CACHE_PROVIDER = "database.cache.provider";
  public static final String CONFIG_DATASOURCE = "datasource";
  public static final String CONFIG_DATASOURCE_DEFAULT = "datasource.default";
  public static final String CONFIG_DATASOURCE_USERNAME = "username";
  public static final String CONFIG_DATASOURCE_PROVIDER = "provider";

  // ========================================================================================
  // Pagination Flag
  // ========================================================================================
  public static final String PAGE_TOTAL_ENTRY = "totalEntry";
  public static final String PAGE_TOTAL_PAGE = "totalPage";
  public static final String PAGE_LIST = "list";
  public static final String PAGE_PAGE_NO = "pageNo";
  public static final String PAGE_PAGE_SIZE = "pageSize";


  // ========================================================================================
  // Query Where Expression Flag
  // ========================================================================================
  /**
   * Equal To
   */
  public static final String EXPR_EQ = "eq";
  /**
   * Not Equal To
   */
  public static final String EXPR_NE = "ne";
  /**
   * In
   */
  public static final String EXPR_IN = "in";
  /**
   * Not In
   */
  public static final String EXPR_NIN = "notIn";
  /**
   * Greater Than
   */
  public static final String EXPR_GT = "gt";
  /**
   * Greater Than or Equal to
   */
  public static final String EXPR_GE = "ge";
  /**
   * Less Than
   */
  public static final String EXPR_LT = "lt";
  /**
   * Less Than or Equal to
   */
  public static final String EXPR_LE = "le";
  /**
   * Is Null
   */
  public static final String EXPR_IS_NULL = "isNull";
  /**
   * Is Not Null
   */
  public static final String EXPR_IS_NOT_NULL = "isNotNull";
  /**
   * Like
   */
  public static final String EXPR_LIKE = "like";
  /**
   * Left Like
   */
  public static final String EXPR_LLIKE = "llike";
  /**
   * Right Like
   */
  public static final String EXPR_RLIKE = "rlike";
  /**
   * Between
   */
  public static final String EXPR_BETWEEN = "between";
  /**
   * Or
   */
  public static final String EXPR_OR = "or";
}
