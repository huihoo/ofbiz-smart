package org.huihoo.ofbiz.smart.base;

public final class C {
    public static final String APPLICATION_CONFIG_NAME = "/application.properties";
    public static final String UTF_8 = "UTF-8";
    public static final String PROFILE_NAME = "profile";
    public static final String PROFILE_PRODUCTION = "production";
    //========================================================================================
    //                     Reflect Related Flag
    //========================================================================================
    public static final String PROPERTY_ACCESSOR_PREFIX_GET = "get";
    public static final String PROPERTY_ACCESSOR_PREFIX_HAS = "has";
    public static final String PROPERTY_ACCESSOR_PREFIX_IS = "is";
    public static final String[] PROPERTY_ACCESSOR_PERFIX = {
            PROPERTY_ACCESSOR_PREFIX_GET,
            PROPERTY_ACCESSOR_PREFIX_HAS,
            PROPERTY_ACCESSOR_PREFIX_IS
    };

    //========================================================================================
    //            Service Config Flag
    //========================================================================================
    public static final String SERVICE_SCANNING_NAMES = "service.scanning.names";

    //========================================================================================
    //            Datasource Config Flag
    //========================================================================================
    public static final String CONFIG_DATASOURCE_CACHE_PROVIDER = "database.cache.provider";
    public static final String CONFIG_DATASOURCE = "datasource";
    public static final String CONFIG_DATASOURCE_DEFAULT = "datasource.default";
    public static final String CONFIG_DATASOURCE_USERNAME = "username";
    public static final String CONFIG_DATASOURCE_PROVIDER = "provider";

    //========================================================================================
    //            Pagination Flag
    //========================================================================================
    public static final String PAGE_TOTAL_ENTRY = "totalEntry";
    public static final String PAGE_TOTAL_PAGE = "totalPage";
    public static final String PAGE_LIST = "list";
    public static final String PAGE_PAGE_NO = "pageNo";
    public static final String PAGE_PAGE_SIZE = "pageSize";


    //========================================================================================
    //            Query Where Expression Flag
    //========================================================================================
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
