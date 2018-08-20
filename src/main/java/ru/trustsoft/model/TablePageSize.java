package ru.trustsoft.model;

import org.springframework.stereotype.Component;

import javax.persistence.Id;

@Component
public class TablePageSize {

    @Id
    private int id;

    private Integer size;

    public TablePageSize() {
    }

    public TablePageSize(int id, Integer size) {
        this.id = id;
        this.size = size;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "TablePageSize{" +
                "id=" + id +
                ", size=" + size +
                '}';
    }
}
