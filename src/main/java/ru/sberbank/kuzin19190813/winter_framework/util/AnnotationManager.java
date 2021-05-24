package ru.sberbank.kuzin19190813.winter_framework.util;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AnnotationManager {
    public static <T> Method findMethod(Class<T> clazz, Predicate<Method> methodPredicate) {
        for (Method method : clazz.getMethods()) {
            if (methodPredicate.test(method)) return method;
        }
        return null;
    }

    public static Method findMethodByAnnotationParameter(Class<?> clazz, Class<? extends Annotation> annotationClass, String annotationParameterKey, String annotationParameterValue) {
        return findMethod(clazz, method -> {
            Method fieldOfAnnotation = findMethod(annotationClass, field -> field.getName().equals(annotationParameterKey));
            if (fieldOfAnnotation != null) {
                try {
                    String value = fieldOfAnnotation.invoke(annotationClass).toString();
                    if (value.equals(annotationParameterValue)) return true;
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            return false;
        });
    }

    public static <A extends Annotation> Method findMethodByAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass, Predicate<A> annotationPredicate) {
        return findMethod(clazz, method -> {
            A annotation = (A) method.getDeclaredAnnotation(annotationClass);
            return annotationPredicate.test(annotation);
        });
    }

    public static List<Class<?>> findClasses(String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        File directory = new File(System.getProperty("user.dir") + "/src/main/java/" + packageName.replaceAll("\\.", "/"));
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                String javaSuffix = ".java";
                for (File file : files) {
                    if (file.getName().endsWith(javaSuffix)) {
                        classes.add(Class.forName(packageName + '.' + file.getName().replaceAll(javaSuffix, "")));
                    } else if (file.isDirectory()) {
                        classes.addAll(findClasses(packageName+'.'+file.getName().replaceAll(javaSuffix, "")));
                    }
                }
            }
        }
        return classes;
    }

    public static List<Class<?>> findClassesByAnnotation(String packageName, Class<? extends Annotation> annotationClass) throws ClassNotFoundException {
        return findClasses(packageName).stream()
                .filter(aClass -> aClass.getAnnotation(annotationClass) != null)
                .collect(Collectors.toList());
    }
}
