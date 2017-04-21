package me.jarkimzhu.libs.cache.redis.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created on 2017/4/21.
 *
 * @author JarkimZhu
 * @since jdk1.8
 */
public abstract class BulkReplyParser {
    public static Map<String, Map<String, String>> parseInfo(String redisInfo) {
        HashMap<String, Map<String, String>> result = new HashMap<>();
        StringTokenizer token = new StringTokenizer(redisInfo, "\r\n");

        Map<String, String> mGroup = null;
        while (token.hasMoreTokens()) {
            String line = token.nextToken();
            if(line.startsWith("#")) {
                mGroup = new HashMap<>();
                result.put(line.substring(2), mGroup);
            } else if(mGroup != null) {
                StringTokenizer lToken = new StringTokenizer(line, ":");
                String key = lToken.nextToken();
                String value = lToken.nextToken();
                mGroup.put(key, value);
            }
        }
        return result;
    }
}
