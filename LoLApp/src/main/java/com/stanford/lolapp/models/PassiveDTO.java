package com.stanford.lolapp.models;

/**
 * Created by Mark Stanford on 4/28/14.
 */
public class PassiveDTO {

    String description;
    ImageDTO image;
    String name;
    String sanitizedDescription;

    public PassiveDTO() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ImageDTO getImage() {
        return image;
    }

    public void setImage(ImageDTO image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSanitizedDescription() {
        return sanitizedDescription;
    }

    public void setSanitizedDescription(String sanitizedDescription) {
        this.sanitizedDescription = sanitizedDescription;
    }

    @Override
    public String toString() {
        return "PassiveDTO{" +
                "description='" + description + '\'' +
                ", image=" + image +
                ", name='" + name + '\'' +
                ", sanitizedDescription='" + sanitizedDescription + '\'' +
                '}';
    }
}
