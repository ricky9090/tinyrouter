package com.example.tinyrouter.core;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.example.tinyrouter.model.IRouterGroup;
import com.example.tinyrouter.model.RouterInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 简易实现的路由组件
 */
public class TinyRouter {

    private static final String TAG = "RouterLog";

    private static final HashMap<String, RawRouterGroup> moduleMap = new HashMap<>();

    public static final String PACKAGE_OF_GENERATE_FILE = "com.example.tinyrouter.group";

    public static void init(Application app) {
        if (app == null) {
            return;
        }

        long startTime = System.currentTimeMillis();
        try {

            final Set<String> classNames = ClassUtil.getFileNameByPackageName(app, PACKAGE_OF_GENERATE_FILE);
            for (String className : classNames) {
                Class<?> targetClass = Class.forName(className);
                Object instance = targetClass.getConstructor().newInstance();
                if (instance instanceof IRouterGroup) {
                    IRouterGroup group = (IRouterGroup) instance;
                    RawRouterGroup rawGroup = new RawRouterGroup();
                    rawGroup.groupName = group.getGroupName();
                    group.registerRouter(rawGroup.routerInfoMap);
                    moduleMap.put(rawGroup.groupName, rawGroup);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        long delta = endTime - startTime;
        RouterLog.LOG_I(TAG, "TinyRouter init cost time: " + delta);
    }

    public static RouterIntent from(Context context) {
        RouterIntent routerIntent = new RouterIntent(context, RouterIntent.TYPE_CONTEXT);
        return routerIntent;
    }

    public static RouterIntent from(Activity activity) {
        RouterIntent routerIntent = new RouterIntent(activity, RouterIntent.TYPE_ACTIVITY);
        return routerIntent;
    }

    public static RouterIntent from(Fragment fragment) {
        RouterIntent routerIntent = new RouterIntent(fragment, RouterIntent.TYPE_FRAGMENT);
        return routerIntent;
    }

    public static class RouterIntent {

        protected static final String TYPE_CONTEXT = "__context";
        protected static final String TYPE_ACTIVITY = "__activity";
        protected static final String TYPE_FRAGMENT = "__fragment";

        private String _callerType = "";
        private Object _caller;
        private String _path;

        private Intent _intent = new Intent();

        public RouterIntent(Object caller, String type) {
            this._caller = caller;
            this._callerType = type;
        }

        public RouterIntent to(String path) {
            this._path = path;
            return this;
        }

        public Intent getRawIntent() {
            return _intent;
        }

        public RouterIntent withParam(String key, boolean value) {
            _intent.putExtra(key, value);
            return this;
        }

        public RouterIntent withParam(String key, int value) {
            _intent.putExtra(key, value);
            return this;
        }

        public RouterIntent withParam(String key, long value) {
            _intent.putExtra(key, value);
            return this;
        }

        public RouterIntent withParam(String key, String value) {
            _intent.putExtra(key, value);
            return this;
        }

        public RouterIntent withParam(String key, long[] values) {
            _intent.putExtra(key, values);
            return this;
        }

        public RouterIntent withParam(String key, int[] values) {
            _intent.putExtra(key, values);
            return this;
        }

        public void start() {
            if (_caller == null) {
                return;
            }
            if (RouterUtils.isEmptyStr(_path)) {
                return;
            }
            Class<?> activityClass = findClassInMap(_path);
            if (activityClass == null) {
                return;
            }

            //_intent.setClass(_context, activityClass);
            //_context.startActivity(_intent);

            if (TYPE_CONTEXT.equals(this._callerType)) {
                Context _context = (Context) _caller;
                _intent.setClass(_context, activityClass);
                _context.startActivity(_intent);
            } else if (TYPE_ACTIVITY.equals(this._callerType)) {
                Context _context = (Context) _caller;
                _intent.setClass(_context, activityClass);
                _context.startActivity(_intent);
            } else if (TYPE_FRAGMENT.equals(this._callerType)) {
                Fragment _fragment = (Fragment) _caller;
                _intent.setClass(_fragment.getContext(), activityClass);
                _fragment.startActivity(_intent);
            }
        }

        public void startForResult(int requestCode) {
            if (_caller == null) {
                return;
            }
            if (RouterUtils.isEmptyStr(_path)) {
                return;
            }
            Class<?> activityClass = findClassInMap(_path);
            if (activityClass == null) {
                return;
            }

            //_intent.setClass(_context, activityClass);
            //_context.startActivityForResult(_intent, requestCode);

            if (TYPE_CONTEXT.equals(this._callerType)) {
                throw new RuntimeException("Only support startActivityForResult directly from Activity or Fragment");

            } else if (TYPE_ACTIVITY.equals(this._callerType)) {
                Activity _activity = (Activity) _caller;
                _intent.setClass(_activity, activityClass);
                _activity.startActivityForResult(_intent, requestCode);

            } else if (TYPE_FRAGMENT.equals(this._callerType)) {
                Fragment _fragment = (Fragment) _caller;
                Context _fragmentContext = _fragment.getContext();
                if (_fragmentContext != null) {
                    _intent.setClass(_fragment.getContext(), activityClass);
                    _fragment.startActivityForResult(_intent, requestCode);
                }
            }
        }

        private Class<?> findClassInMap(String path) {
            Class<?> result = null;
            Set<Map.Entry<String, RawRouterGroup>> entrySet = moduleMap.entrySet();
            for (Map.Entry<String, RawRouterGroup> entry : entrySet) {
                RawRouterGroup group = entry.getValue();
                RouterInfo info = group.routerInfoMap.get(path);
                if (info != null) {
                    result = info.getRouteClazz();
                    break;
                }
            }
            return result;
        }
    }
}
