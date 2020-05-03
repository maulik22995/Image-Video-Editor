package com.imagevideoeditor.Utils;

public class DimensionData {

    public int width;
    public int height;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public DimensionData(int new_width, int new_height) {
        width = new_width;
        height = new_height;
    }
}
