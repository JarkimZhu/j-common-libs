package me.jarkimzhu.libs.protocol.json.support.spring;

import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import me.jarkimzhu.libs.protocol.json.ProtocolConverterFastJsonImpl;
import me.jarkimzhu.libs.protocol.json.dto.JsonResultProtocol;
import me.jarkimzhu.libs.utils.reflection.ReflectionUtils;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author JarkimZhu
 *         Created on 2015/12/28.
 * @version 0.1.0-SNAPSHOT
 * @since JDK1.8
 */
public class SupportFastJsonHttpMessageConverter extends FastJsonHttpMessageConverter {

    @Resource
    private ProtocolConverterFastJsonImpl protocolConverter;

    private Map<String, List<SerializeFilter>> filterMapping;
    
//    public String chinaToUnicode(String str){
//        StringBuffer result= new StringBuffer();
//        for (int i = 0; i < str.length(); i++){
//            int chr1 = (char) str.charAt(i);
//            if(chr1>=19968&&chr1<=171941){//汉字范围 \u4e00-\u9fa5 (中文)
//            	result.append("\\u" + Integer.toHexString(chr1));
//            }else{
//            	result.append(str.charAt(i));
//            }
//        }
//        return result.toString();
//    }
    
    @Override
    protected void writeInternal(Object obj, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        FastJsonConfig config = getFastJsonConfig();
        Charset charset = config.getCharset();
        OutputStream out = outputMessage.getBody();

        SerializerFeature[] features = config.getSerializerFeatures();
        String text = null;
        if (obj instanceof String) {
            text = (String) obj;
        } else if (obj instanceof JsonResultProtocol) {
            Object message = ((JsonResultProtocol) obj).getMessage();
            List<SerializeFilter> filters = unpackObject(message);
            if (filters != null) {
                text = protocolConverter.toProtocol(obj, filters.toArray(new SerializeFilter[filters.size()]), features);
            }
        } else {
            List<SerializeFilter> filters = unpackObject(obj);
            if (filters != null) {
                text = protocolConverter.toProtocol(obj, filters.toArray(new SerializeFilter[filters.size()]), features);
            }
        }

        if (text == null) {
            text = protocolConverter.toProtocol(obj, null, features);
        }
//        text =  chinaToUnicode(text);
//        HttpHeaders headers = outputMessage.getHeaders();
        byte[] bytes = text.getBytes(charset);
//        headers.setContentLength(bytes.length);
        out.write(bytes);
    }

    private List<SerializeFilter> unpackObject(Object obj) {
        if (obj instanceof Collection<?>) {
            Collection<?> list = (Collection<?>) obj;
            if (list.size() > 0) {
                Object item = list.iterator().next();
                return getSerializeFilters(item);
            }
        } else if (obj instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>) obj;
            if (map.size() > 0) {
                Entry<?, ?> entry = map.entrySet().iterator().next();
                ArrayList<SerializeFilter> filters = new ArrayList<>(2);

                List<SerializeFilter> keyFilters = getSerializeFilters(entry.getKey());
                if (keyFilters != null) {
                    filters.addAll(keyFilters);
                }
                List<SerializeFilter> valueFilters = getSerializeFilters(entry.getValue());
                if (valueFilters != null) {
                    filters.addAll(valueFilters);
                }
                return filters;
            }
        } else {
            return getSerializeFilters(obj);
        }
        return null;
    }

    private List<SerializeFilter> getSerializeFilters(Object obj) {
        if (filterMapping != null && obj != null) {
            for (String className : filterMapping.keySet()) {
                if (ReflectionUtils.isInstanceOf(obj, className)) {
                    return filterMapping.get(className);
                }
            }
        }
        return null;
    }

    public void setFilterMapping(Map<String, List<SerializeFilter>> filterMapping) {
        this.filterMapping = filterMapping;
    }
}
