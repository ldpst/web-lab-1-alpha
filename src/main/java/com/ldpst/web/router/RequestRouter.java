package com.ldpst.web.router;

import com.ldpst.web.annotations.GetMapping;
import com.ldpst.web.annotations.PostMapping;
import com.ldpst.web.annotations.RestController;
import com.ldpst.web.server.ShootController;
import com.ldpst.web.utils.Logger;
import com.ldpst.web.utils.ResultManager;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RequestRouter {
    private final Map<String, RoutManager> routes = new HashMap<>();

    private final static Logger logger = new Logger("logs/logs.txt");

    public RequestRouter(String basePackage) {
        scanProject(basePackage);
    }

    public void scanProject(String basePackage) {
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(RestController.class);
        for (Class<?> controller : controllers) {
            try {
                Object controllerInstance = controller.getDeclaredConstructor().newInstance();
                for (Method method : controller.getDeclaredMethods()) {
                    registerMethod(method, controllerInstance);
                }
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                logger.write("error: " + e.toString());
            }
        }
    }

    public void registerMethod(Method method, Object controller) {
        if (method.isAnnotationPresent(PostMapping.class) && method.getReturnType().equals(String.class)) {
            PostMapping postMapping = method.getAnnotation(PostMapping.class);
            routes.put("POST:" + postMapping.value(), new RoutManager(method, controller));
        }
        if (method.isAnnotationPresent(GetMapping.class) && method.getReturnType().equals(String.class)) {
            GetMapping getMapping = method.getAnnotation(GetMapping.class);
            routes.put("GET:" + getMapping.value(), new RoutManager(method, controller));
        }
    }

    public String handleRequest(String requestMethod, String uri) {
        RoutManager routManager = routes.get(requestMethod + ":" + uri);
        if (routManager == null) {
            return ResultManager.errorResult("Unsupported HTTP URI: " + uri);
        }
        Method method = routManager.getMethod();
        Object controller = routManager.getController();
        try {
            return (String) method.invoke(controller);
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.write(e.getCause().toString());
            return ResultManager.errorResult("Unsupported error");
        }
    }
}
