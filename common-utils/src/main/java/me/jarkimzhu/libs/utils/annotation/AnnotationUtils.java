package me.jarkimzhu.libs.utils.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author JarkimZhu
 */
public class AnnotationUtils {

    private static final Logger logger = LoggerFactory.getLogger(AnnotationUtils.class);

    public static boolean checkAnnotationCapable(Object obj, Class<? extends Annotation> annotationType) {
        Target target = annotationType.getAnnotation(Target.class);
        ElementType[] types = target.value();

        boolean result = false;
        Class<?> clazz = obj.getClass();
        for (ElementType type : types) {
            switch (type) {
                case TYPE:
                    result = clazz.isAnnotationPresent(annotationType);
                    break;
                case METHOD:
                    Method[] methods = clazz.getDeclaredMethods();
                    for (Method method : methods) {
                        result = method.isAnnotationPresent(annotationType);
                        if (result) {
                            break;
                        }
                    }
                    break;
                case FIELD:
                    Field[] fields = clazz.getDeclaredFields();
                    for (Field field : fields) {
                        result = field.isAnnotationPresent(annotationType);
                        if (result) {
                            break;
                        }
                    }
                    break;
                default:
                    break;
            }
            if (result) {
                break;
            }
        }
        return result;
    }

    public static <A extends Annotation> A getAnnotation(Object obj, Class<A> annotationType) {
        Target target = annotationType.getAnnotation(Target.class);
        ElementType[] types = target.value();

        A result = null;
        Class<?> clazz = obj.getClass();
        for (ElementType type : types) {
            switch (type) {
                case TYPE:
                    result = clazz.getAnnotation(annotationType);
                    break;
                case METHOD:
                    Method[] methods = clazz.getDeclaredMethods();
                    for (Method method : methods) {
                        result = method.getAnnotation(annotationType);
                        if(result != null) {
                            return result;
                        }
                    }
                    break;
                case FIELD:
                    Field[] fields = clazz.getDeclaredFields();
                    for (Field field : fields) {
                        result = field.getAnnotation(annotationType);
                        if(result != null) {
                            return result;
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        return result;
    }

    public static <A extends Annotation> A getAnnotationOnMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes, Class<A> annotationType){
        try {
            Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
            if(method != null){
                return method.getAnnotation(annotationType);
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}
