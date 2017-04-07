package me.jarkimzhu.libs.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.*;
import me.jarkimzhu.libs.utils.annotation.NumberFormat;
import me.jarkimzhu.libs.utils.annotation.SerializedName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author JarkimZhu
 */
public abstract class JsonUtils {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    public static <T> String toJSONString(T dto, SerializeFilter[] filters, SerializerFeature... features) {
        Class<?> dtoClass = dto.getClass();
        Field[] fields = ReflectionUtils.getAllField(dtoClass);
        final HashMap<String, String> nameMapping = new HashMap<>();
        final HashMap<String, NumberFormat> formatMapping = new HashMap<>();
        final HashMap<Type, ObjectSerializer> serializers = new HashMap<>();
        for (Field field : fields) {
            processSerializedName(dto, field, nameMapping);

            processNumberFormat(dto, field, formatMapping, serializers);
        }
        if (nameMapping.size() > 0) {
            SerializedNameFilter nameFilter = new SerializedNameFilter(nameMapping);
            if (filters != null) {
                SerializeFilter[] newFilters = new SerializeFilter[filters.length + 1];
                System.arraycopy(filters, 0, newFilters, 0, filters.length);
                newFilters[filters.length] = nameFilter;
                filters = newFilters;
            } else {
                filters = new SerializeFilter[]{nameFilter};
            }
        }

        SerializeConfig serializeConfig = null;
        if(formatMapping.size() > 0) {
            MappedDoubleSerializer doubleSerializer = new MappedDoubleSerializer(formatMapping);
            serializeConfig = new SerializeConfig();
            for(Map.Entry<Type, ObjectSerializer> s : serializers.entrySet()) {
                if(s.getValue() != null) {
                    serializeConfig.put(s.getKey(), s.getValue());
                } else {
                    serializeConfig.put(s.getKey(), doubleSerializer);
                }

            }
        }

        if(serializeConfig == null) {
            serializeConfig = SerializeConfig.globalInstance;
        }
        if(features == null) {
            features = new SerializerFeature[0];
        }

        return JSON.toJSONString(dto, serializeConfig, filters, null, JSON.DEFAULT_GENERATE_FEATURE, features);
    }

    private static void processNumberFormat(Object dto, Field field, HashMap<String, NumberFormat> formatMapping, HashMap<Type, ObjectSerializer> serializers) {
        if(!ReflectionUtils.isFramework(dto.getClass())) {
            if (field.isAnnotationPresent(NumberFormat.class)) {
                NumberFormat nf = field.getAnnotation(NumberFormat.class);
                formatMapping.put(field.getName(), nf);
                serializers.put(field.getType(), null);
                serializers.put(dto.getClass(), new JavaBeanSerializer(dto.getClass()));
            } else if(!ReflectionUtils.isFramework(field.getGenericType())){
                try {
                    Object obj = ReflectionUtils.getGetterValue(dto, field.getName());
                    if(obj instanceof Map) {
                        Map<?,?> map = (Map<?,?>) obj;
                        for(Object value : map.values()) {
                            if(value != null && !ReflectionUtils.isFramework(value.getClass())) {
                                Field[] fields = ReflectionUtils.getAllField(value.getClass());
                                for(Field f : fields) {
                                    processNumberFormat(value, f, formatMapping, serializers);
                                }
                            }
                        }
                    } else if(obj instanceof List) {
                        List<?> list = (List<?>) obj;
                        for(Object value : list) {
                            if(value != null && !ReflectionUtils.isFramework(value.getClass())) {
                                Field[] fields = ReflectionUtils.getAllField(value.getClass());
                                for(Field f : fields) {
                                    processNumberFormat(value, f, formatMapping, serializers);
                                }
                            }
                        }
                    } else if(obj != null && !ReflectionUtils.isFramework(obj.getClass())) {
                        Field[] fields = ReflectionUtils.getAllField(obj.getClass());
                        for(Field f : fields) {
                            processNumberFormat(obj, f, formatMapping, serializers);
                        }
                    }
                } catch (NoSuchMethodException ignored){} catch (IllegalAccessException | NoSuchFieldException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static <T> void processSerializedName(T dto, Field field, HashMap<String, String> nameMapping) {
        Class<?> dtoClass = dto.getClass();
        if (field.isAnnotationPresent(SerializedName.class)) {
            SerializedName sn = field.getAnnotation(SerializedName.class);
            String sName = sn.value();
            if (sName.startsWith("get")) {
                try {
                    Method get = dtoClass.getMethod(sName, (Class[]) null);
                    nameMapping.put(field.getName(), get.invoke(dto, (Object[]) null).toString());
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    nameMapping.put(field.getName(), sn.value());
                }
            } else {
                nameMapping.put(field.getName(), sn.value());
            }
        }
    }

    public static <T> T parseObject(File file, Class<T> clazz) {
        return parseObject(file, (Type) clazz);
    }

    public static <T> T parseObject(File file, TypeReference<T> typeReference) {
        return parseObject(file, typeReference.getType());
    }

    public static <T> T parseObject(File file, Type type) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
            br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString().replaceAll("\\s", "");
            return JSON.parseObject(json, type);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    public static <T> T parseObject(InputStream inputStream, Type type) {
        try {
            return JSON.parseObject(inputStream, type, (Feature[]) null);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return null;
    }

    public static <T> void writeObject(File file, T obj) {
        writeObject(file, obj, (SerializeFilter[]) null);
    }

    public static <T> void writeObject(File file, T obj, SerializeFilter... filters) {
        writeObject(file, obj, filters, (SerializerFeature[]) null);
    }

    public static <T> void writeObject(File file, T obj, SerializeFilter[] filters, SerializerFeature... features) {
        String json = toJSONString(obj, filters, features);
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(json);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    private static class SerializedNameFilter implements NameFilter {

        private Map<String, String> nameMapping;

        SerializedNameFilter(Map<String, String> nameMapping) {
            this.nameMapping = nameMapping;
        }

        @Override
        public String process(Object source, String name, Object value) {
            String mapping = nameMapping.get(name);
            if (mapping != null) {
                return mapping;
            } else {
                return name;
            }
        }

    }

    private static class MappedDoubleSerializer implements ObjectSerializer {
        private Map<String, NumberFormat> mapping;

        public MappedDoubleSerializer(Map<String, NumberFormat> nameMapping) {
            this.mapping = nameMapping;
        }

        @Override
        public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
            NumberFormat nf = mapping.get(fieldName.toString());

            ObjectSerializer objectSerializer = null;
            if(fieldType == Double.class || fieldType == double.class
                    || fieldType == Float.class || fieldType == float.class) {
                if(nf != null) {
                    objectSerializer = new DoubleStringSerializer(nf.value(), nf.stringify());
                } else {
                    objectSerializer = DoubleSerializer.instance;
                }
            } else if(fieldType == null) {
                if(object instanceof Double) {
                    objectSerializer = DoubleSerializer.instance;
                } else if(object instanceof Float) {
                    objectSerializer = FloatCodec.instance;
                }
            }
            if(objectSerializer != null) {
                objectSerializer.write(serializer, object, fieldName, fieldType, features);
            } else {
                SerializeWriter out = serializer.out;
                out.writeNull(SerializerFeature.WriteNullNumberAsZero);
            }
        }
    }

    private static class DoubleStringSerializer implements ObjectSerializer {
        public final static DoubleStringSerializer instance      = new DoubleStringSerializer("0.00", false);

        private DecimalFormat decimalFormat;
        private boolean stringify;

        public DoubleStringSerializer(DecimalFormat decimalFormat, boolean stringify){
            this.decimalFormat = decimalFormat;
            this.stringify = stringify;
        }

        public DoubleStringSerializer(String decimalFormat, boolean stringify){
            this(new DecimalFormat(decimalFormat), stringify);
        }

        public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
            SerializeWriter out = serializer.out;

            if (object == null) {
                out.writeNull(SerializerFeature.WriteNullNumberAsZero);
                return;
            }

            double doubleValue;
            if(object instanceof Float) {
                doubleValue = ((Float) object).doubleValue();
            } else {
                doubleValue = (Double) object;
            }

            if (Double.isNaN(doubleValue) //
                    || Double.isInfinite(doubleValue)) {
                out.writeNull();
            } else {
                String doubleText = decimalFormat.format(doubleValue);
                if(stringify) {
                    out.write("\"" + doubleText + "\"");
                } else {
                    out.write(doubleText);
                }
            }
        }
    }
}
