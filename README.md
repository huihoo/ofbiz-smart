OFBiz Smart: 一个简单的，灵活的，功能较强的Web开发框架，主要灵感来自于OFBiz Framework, Spring Boot, Moqui等框架。

## 简介

OFBiz Smart是一个简单的，灵活的，功能较强的Web开发框架，它的设计思想主要借鉴了 [Apache OFBiz](http://ofbiz.apache.org/) 框架的设计思想，同时简单引入了笔者在实际项目开发过程中使用其它项目框架的一些好的特性。 比如[Spring Boot的profile特性](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-external-config-profile-specific-properties), [Spring MVC的View Pattern](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/servlet/view/AbstractView.html)。

OFBiz Smart不是一个全栈式的Web开发框架，它能做的工作主要是 数据库操作访问，业务服务定义与执行，界面整合，URL请求映射等常见的Web开发工作。 但由于OFBiz Smart基于Java7和Servlet3.0,可以很容易的集成其它特性，比如应用安全，任务调度，消息通信等。

OFBiz Smart和主流的Web开发框架相比，还很嫩，甚至不具有可比性（这点笔者不可否认）。但框架本身经过良好的测试，背后由专业的开发团队，[Huihoo社区维护](http://code.huihoo.com/huihoo/ofbiz-smart/)，开发人员项目经验相当丰富，对主流的Java Web开发框架相当熟悉，目前已经在商业项目中采用 具有较好的稳定性。

## 特性

* 基于XML配置的URL映射，不需要书写Controller。
* 支持多种View输出，包括Jsp,Json,Xml,Captcha等，另支持自定义View。
* 服务定义，通过简单的配置支持服务的Http调用，做到编写一次服务即可以本地调用，也可以Http调用。
* 自定义服务执行
* 基于Ebean的实体ORM，通用的数据库操作。
* 多数据源/自定义数据源。
* 自定义Delegator。
* 简单的实体/参数验证。
* 国际化
* 应用配置的profile加载和读取。

## 架构

![smart](http://huihoo.org/ofbiz-smart/section_intro/smart_arch.jpg)

## 文档
* [OFBiz Smart中文手册](http://huihoo.org/ofbiz-smart/)
* [OFBiz Smart Javadoc](http://huihoo.org/ofbiz-smart/javadoc/1.0.1/)

## 许可

Apache v2 