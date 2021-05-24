package ru.sberbank.kuzin19190813.winter_framework.rest.annotations;

import ru.sberbank.kuzin19190813.winter_framework.constants.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RestMapping {
    String path();
    HttpMethod method();
}