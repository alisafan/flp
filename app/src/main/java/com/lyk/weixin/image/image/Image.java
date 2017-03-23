package com.lyk.weixin.image.image;

import com.nostra13.universalimageloader.utils.L;

/**
 * Created by Pan_ on 2015/2/3.
 */
public class Image {
    private String url;
    private int width;
    private int height;
    public Image() {
    }

    public Image(String url, int width, int height) {
        this.url = url;
        this.width = width;
        this.height = height;
        L.i(toString());
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

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

    @Override
    public String toString() {

        return ""+url;
    }
}
