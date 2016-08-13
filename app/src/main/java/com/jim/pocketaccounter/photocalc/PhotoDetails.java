package com.jim.pocketaccounter.photocalc;

/**
 * Created by DEV on 07.08.2016.
 */

public class PhotoDetails {
    String photopath;

        String photopathCache;
        String RecordID;

    public PhotoDetails(String photopath, String photopathCache, String recordID) {
        this.photopath = photopath;
        this.photopathCache = photopathCache;
        RecordID = recordID;
    }

    public String getPhotopathCache() {
        return photopathCache;
    }

    public void setPhotopathCache(String photopathCache) {
        this.photopathCache = photopathCache;
    }


    public String getPhotopath() {
        return photopath;
    }

    public void setPhotopath(String photopath) {
        this.photopath = photopath;
    }

    public String getRecordID() {
        return RecordID;
    }

    public void setRecordID(String recordID) {
        RecordID = recordID;
    }
}
