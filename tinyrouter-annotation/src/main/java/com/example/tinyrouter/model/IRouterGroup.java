package com.example.tinyrouter.model;

import java.util.Map;

public interface IRouterGroup {

    String getGroupName();

    void registerRouter(Map<String, RouterInfo> routerInfoMap);
}
