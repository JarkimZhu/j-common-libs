/*
 * Copyright (c) 2014-2016. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.libs.protocol.json.test;

import com.alibaba.fastjson.annotation.JSONField;
import me.jarkimzhu.libs.utils.DateUtils;
import me.jarkimzhu.libs.utils.annotation.NumberFormat;

import java.util.*;

/**
 * Created on 2016/8/22.
 *
 * @author JarkimZhu
 * @since JDK1.8
 */
public class TestDto {
    @NumberFormat(stringify = true)
    private float f1 = 0.123f;
    @NumberFormat()
    private double d1 = 9.100;
    @NumberFormat("#.#######")
    private Float f2 = 999998.2345f;
    @NumberFormat("0.0")
    private Double d2 = 999999999.2345;
    private Double d3 = null;
    private double d4 = 999.456789;
    @JSONField(format = DateUtils.FMT_LONG_DATE)
    private Date date = new Date();
    private String[] strs = {"a", "b", "c"};
    private List<Map<String, Object>> maps;


    public TestDto() {
        maps = new ArrayList<>();
        HashMap<String, Object> m1 = new HashMap<>();
        m1.put("k1", 0.1);
        m1.put("k2", null);
        maps.add(m1);
    }

    public List<Map<String, Object>> getMaps() {
        return maps;
    }

    public void setMaps(List<Map<String, Object>> maps) {
        this.maps = maps;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getF1() {
        return f1;
    }

    public void setF1(float f1) {
        this.f1 = f1;
    }

    public double getD1() {
        return d1;
    }

    public void setD1(double d1) {
        this.d1 = d1;
    }

    public Float getF2() {
        return f2;
    }

    public void setF2(Float f2) {
        this.f2 = f2;
    }

    public Double getD2() {
        return d2;
    }

    public void setD2(Double d2) {
        this.d2 = d2;
    }

    public Double getD3() {
        return d3;
    }

    public void setD3(Double d3) {
        this.d3 = d3;
    }

    public double getD4() {
        return d4;
    }

    public void setD4(double d4) {
        this.d4 = d4;
    }

    public String[] getStrs() {
        return strs;
    }

    public void setStrs(String[] strs) {
        this.strs = strs;
    }
}
