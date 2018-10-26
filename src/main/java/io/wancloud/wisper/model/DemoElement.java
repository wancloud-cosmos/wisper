package io.wancloud.wisper.model;

public class DemoElement extends NotificationElement {

    private String name;
    private int height;

    public DemoElement(String name, int height) {
        this.name = name;
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

}
