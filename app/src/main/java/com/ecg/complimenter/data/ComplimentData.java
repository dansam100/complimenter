package com.ecg.complimenter.data;

/**
 * Created by Sam on 10/27/2014.
 */
public class ComplimentData {
    private long id;
    private String text;
    private String imageName;
    private byte[] imageData;

    public ComplimentData(long id, String text, String imageName, byte[] imageData){
        this.id = id;
        this.text = text;
        this.imageName = imageName;
        this.imageData = imageData;
    }

    public long getId(){ return this.id; }
    public void setId(long id){ this.id = id; }

    public String getText(){ return this.text; }
    public void setText(String text){ this.text = text; }

    public String getImageName(){ return this.imageName; }
    public void setImageName(String imageName){ this.imageName = imageName; }

    public byte[] getImageData(){ return this.imageData; }
    public void getImageData(byte[] imageData){ this.imageData = imageData; }

    @Override
    public String toString(){
        return String.format("Compliment: %s\nImage: %s", text, imageName);
    }
}
