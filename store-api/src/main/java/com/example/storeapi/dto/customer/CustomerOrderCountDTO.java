package com.example.storeapi.dto.customer;

public class CustomerOrderCountDTO {
    private long total;
    private long open;

    public CustomerOrderCountDTO() {}

    public CustomerOrderCountDTO(long total, long open) {
        this.total = total;
        this.open = open;
    }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }

    public long getOpen() { return open; }
    public void setOpen(long open) { this.open = open; }
}
