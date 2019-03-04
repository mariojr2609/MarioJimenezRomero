package com.example.inmobiliaria.responses;

import java.util.List;

public class ContainerResponse <T>{
    private List<T> rows;
    private long count;

    public ContainerResponse() {
    }

    public ContainerResponse(List<T> rows, long count) {
        this.rows = rows;
        this.count = count;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContainerResponse<?> that = (ContainerResponse<?>) o;
        if (count != that.count) return false;
        return rows != null ? rows.equals(that.rows) : that.rows == null;
    }

    @Override
    public int hashCode() {
        int result = rows != null ? rows.hashCode() : 0;
        result = 31 * result + (int) (count ^ (count >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "ContainerResponse{" +
                "rows=" + rows +
                ", count=" + count +
                '}';
    }
}
