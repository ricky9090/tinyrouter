package com.example.tinyrouter.core;

import com.example.tinyrouter.model.RouterInfo;

import java.util.HashMap;
import java.util.Map;

public class RawRouterGroup {

    public String groupName;
    public final Map<String, RouterInfo> routerInfoMap = new HashMap<>();
}
