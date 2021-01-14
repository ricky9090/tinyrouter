package com.example.tinyrouter.model;

import javax.lang.model.element.Element;

public class RouterInfo {

    private RouteType routeType;
    private String routePath;
    private Class<?> routeClazz;

    private Element element;

    public static RouterInfo build(RouteType type, String route, Class<?> clazz) {
        return new RouterInfo(type, route, clazz, null);
    }

    public RouterInfo(RouteType routeType, String routePath, Element element) {
        this(routeType, routePath, null, element);
    }

    public RouterInfo(RouteType routeType, String routePath, Class<?> routeClazz, Element element) {
        this.routeType = routeType;
        this.routePath = routePath;
        this.routeClazz = routeClazz;
        this.element = element;
    }

    public RouteType getRouteType() {
        return routeType;
    }

    public String getRoutePath() {
        return routePath;
    }

    public Class<?> getRouteClazz() {
        return routeClazz;
    }

    public Element getElement() {
        return element;
    }
}
