package com.major.potholerectifier.model;

import java.io.Serializable;
import java.util.Date;

public class PotHole implements Serializable {

    private long id;
    private PotHoleLocation location;
    private String locality;
    private String landmark;
    private long createdBy;
    private String createdAt;
    private String updatedAt;
    private String image;
    private PotHoleStatus status;
    private int potHoleResId;
    private String completedAt;
    private String  completedimage;

    public PotHole() {
    }
    public PotHole(String locality, String landmark,int potHoleResId,PotHoleStatus status){
        this.locality = locality;
        this.landmark = landmark;
        this.potHoleResId = potHoleResId;
        this.status = status;
    }

    public PotHole(String locality, String landmark){
        this.locality = locality;
        this.landmark = landmark;
    }
    public PotHole(long id, PotHoleLocation location, String locality, String landmark, String image) {
        this.id = id;
        this.location = location;
        this.locality = locality;
        this.landmark = landmark;
        this.image = image;
        this.createdAt = (new Date()).toString();
    }

    public PotHole(long id, PotHoleLocation location, String locality, String landmark, long createdBy, String createdAt, String updatedAt, String image, PotHoleStatus status) {
        this.id = id;
        this.location = location;
        this.locality = locality;
        this.landmark = landmark;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.image = image;
        this.status = status;
    }

    public long getId() {
        return id;
    }



    public void setId(long id) {
        this.id = id;
    }

    public PotHoleLocation getLocation() {
        return location;
    }

    public void setLocation(PotHoleLocation location) {
        this.location = location;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public PotHoleStatus getStatus() {
        return status;
    }

    public void setStatus(PotHoleStatus status) {
        this.status = status;
    }

    public int getPotHoleResId() {
        return potHoleResId;
    }

    public void setPotHoleResId(int potHoleResId) {
        this.potHoleResId = potHoleResId;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public String getCompletedimage() {
        return completedimage;
    }

    public void setCompletedimage(String completedimage) {
        this.completedimage = completedimage;
    }
}
