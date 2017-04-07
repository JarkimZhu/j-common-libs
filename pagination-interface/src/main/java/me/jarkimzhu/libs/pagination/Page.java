package me.jarkimzhu.libs.pagination;

import java.util.List;

/**
 * @author JarkimZhu
 *         Created on 2016/11/23.
 * @since jdk1.8
 */
public class Page<E> {

    public Page() {

    }

    public Page(int total, List<E> rows) {
        this.total = total;
        this.rows = rows;
    }

    private int total;
    private List<E> rows;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<E> getRows() {
        return rows;
    }

    public void setRows(List<E> rows) {
        this.rows = rows;
    }
}
