package Models;

import lombok.NoArgsConstructor;


@NoArgsConstructor
public abstract class Device {

    String id;

    private String class_name;

    private String data_destination_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getData_destination_id() {
        return data_destination_id;
    }

    public void setData_destination_id(String data_destination_id) {
        this.data_destination_id = data_destination_id;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }


}
