package in.neuw.spring.redis;

import java.util.Date;

public class RedisData<T> {

    private String id;
    private Date date = new Date();

    private T data;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
