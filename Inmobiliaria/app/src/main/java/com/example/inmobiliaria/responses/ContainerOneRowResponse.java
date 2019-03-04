package com.example.inmobiliaria.responses;

public class ContainerOneRowResponse <T>{
    private T rows;
    private long count;

    public ContainerOneRowResponse(){

    }

    public ContainerOneRowResponse(T rows, long count) {
        this.rows = rows;
        this.count = count;
    }

    public T getRows() {
        return rows;
    }

    public void setRows(T rows) {
        this.rows = rows;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
