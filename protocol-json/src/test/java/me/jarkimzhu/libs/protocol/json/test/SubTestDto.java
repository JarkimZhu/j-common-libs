package me.jarkimzhu.libs.protocol.json.test;

import me.jarkimzhu.libs.utils.annotation.NumberFormat;

/**
 * Created by JarkimZhu on 16/8/27.
 */
public class SubTestDto extends TestDto {

    @NumberFormat(stringify = true)
    private double sub = 1.12345;

    public double getSub() {
        return sub;
    }

    public void setSub(double sub) {
        this.sub = sub;
    }
}
