package com.example.tinyrouter.compiler;

import com.example.tinyrouter.model.IRouterGroup;
import com.example.tinyrouter.annotation.Router;
import com.example.tinyrouter.model.RouteType;
import com.example.tinyrouter.model.RouterInfo;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.example.tinyrouter.compiler.RouterConst.*;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.example.tinyrouter.annotation.Router")
@AutoService(Processor.class)
public class RouterProcessor extends AbstractProcessor {

    Filer filer;
    Elements elementUtil;
    Types typeUtil;

    String groupName = "";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        LOG_V("Annotation Processor --- init start ---");
        filer = processingEnv.getFiler();

        Map<String, String> options = processingEnvironment.getOptions();
        if (options != null && !options.isEmpty()) {
            groupName = options.get(KEY_GROUP_NAME);
            LOG_V("Target Module: " + groupName);
        }
        elementUtil = processingEnvironment.getElementUtils();
        typeUtil = processingEnvironment.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        LOG_V("Annotation Processor --- Processing start ---");
        if (set == null || set.isEmpty()) {
            return false;
        }

        Set<? extends Element> routeElements = roundEnvironment.getElementsAnnotatedWith(Router.class);
        if (routeElements == null || routeElements.isEmpty()) {
            return false;
        }

        logFoundRouterObj(routeElements);
        processRouter(routeElements);

        return false;
    }

    private void logFoundRouterObj(@Nonnull Set<? extends Element> elements) {
        for (Element element : elements) {
            LOG_V("Found Router in Class: " + ClassName.get((TypeElement) element));
        }
    }

    private void processRouter(Set<? extends Element> routeElements) {
        final TypeMirror activityType = elementUtil.getTypeElement(ACTIVITY_CLASS).asType();
        final List<RouterInfo> routerInfoList = new ArrayList<>();

        for (Element element : routeElements) {
            TypeMirror elementType = element.asType();
            RouterInfo info;
            if (typeUtil.isSubtype(elementType, activityType)) {
                String path = element.getAnnotation(Router.class).path();
                info = new RouterInfo(
                        RouteType.ACTIVITY,
                        path,
                        element);
                LOG_V("Router is an Activity.  Path: [" + path + "]  Class: [" + elementType.toString() + "]");
                routerInfoList.add(info);
            } else {
                throw new RuntimeException("Only Support Router in Activity");
            }
        }

        generateRouterGroup(routerInfoList);
    }

    private void generateRouterGroup(@Nonnull List<RouterInfo> routerInfoList) {
        if (routerInfoList.size() == 0) {
            return;
        }

        //  gen:  String getGroupName();
        MethodSpec.Builder getGroupNameMethod = MethodSpec.methodBuilder(METHOD_GET_GROUP_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(ClassName.get(String.class));

        getGroupNameMethod.addCode(
                "return $S;",
                groupName
        );

        //  gen:  void registerRouter(Map<String, RouterInfo> routerInfoMap);
        ParameterizedTypeName routerMapType = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouterInfo.class));
        ParameterSpec routerMapParam = ParameterSpec.builder(routerMapType, "routerMap").build();

        MethodSpec.Builder registerMethod = MethodSpec.methodBuilder(METHOD_REGISTER_ROUTER)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(routerMapParam)
                .returns(TypeName.VOID);

        for (RouterInfo info: routerInfoList) {

            registerMethod.addStatement(
                    "routerMap.put($S, $T.build($T.$L, $S, $T.class))",
                    info.getRoutePath(),
                    ClassName.get(RouterInfo.class),
                    ClassName.get(RouteType.class),
                    info.getRouteType(),
                    info.getRoutePath(),
                    ClassName.get((TypeElement) info.getElement())
            );
        }

        //  gen file: GroupOf$$[GroupName].Java
        try {
            String groupClassName = GROUP_PERFIX + SEPARATOR + groupName;
            JavaFile.builder(PACKAGE_OF_GENERATE_FILE,
                    TypeSpec.classBuilder(groupClassName)
                            .addMethod(getGroupNameMethod.build())
                            .addMethod(registerMethod.build())
                            .addSuperinterface(ClassName.get(IRouterGroup.class))
                            .addModifiers(Modifier.PUBLIC).build()).build().writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void LOG_V(String msg) {
        System.out.println("[TinyRouter] " + msg);
    }
}