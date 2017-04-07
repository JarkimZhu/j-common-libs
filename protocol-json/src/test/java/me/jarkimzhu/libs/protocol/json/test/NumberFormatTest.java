/*
 * Copyright (c) 2014-2016. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.libs.protocol.json.test;

import me.jarkimzhu.libs.protocol.json.dto.JsonResultProtocol;
import me.jarkimzhu.libs.protocol.ResultCode;
import me.jarkimzhu.libs.utils.JsonUtils;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 2016/8/22.
 *
 * @author JarkimZhu
 * @since JDK1.8
 */
public class NumberFormatTest {
    @Test
    public void testNumberFormat() {
        TestDto dto = new TestDto();
        JsonResultProtocol result = new JsonResultProtocol();
        HashMap<String, Object> ret = new HashMap<>();
        ret.put("haha", dto);
        ret.put("lala", null);
        result.setMessage(ret);
        String json = JsonUtils.toJSONString(result, null);
        System.out.println(json);
    }

    @Test
    public void testJsonUtils() {
        PaymentAccountBean bean = new PaymentAccountBean();
        bean.setPaymentAccountId("abc");
        bean.setBalance(1.23456);
        bean.setFreeze(1.0);

        JsonResultProtocol result = new JsonResultProtocol();
        result.setup(ResultCode.SUCCESS, bean, "record");

        String json = JsonUtils.toJSONString(result, null);
        System.out.println(json);
    }

    @Test
    public void testSubClass() {
        SubTestDto subTestDto = new SubTestDto();
        JsonResultProtocol result = new JsonResultProtocol();
        result.setup(ResultCode.SUCCESS, subTestDto, "sub");

        String json = JsonUtils.toJSONString(result, null);
        System.out.println(json);
    }

    @Test
    public void testMap() {
        Map map = new HashMap();
        map.put("k1", "v1");
        map.put("k2", 0.001);
        String json = JsonUtils.toJSONString(map, null);
        System.out.println(json);
    }
}
