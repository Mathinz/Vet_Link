
package com.vet.link.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SymptomsPojo {

    @SerializedName("commonSymptoms")
    @Expose
    private String commonSymptoms;
    @SerializedName("possibleDisease")
    @Expose
    private String possibleDisease;

    public String getCommonSymptoms() {
        return commonSymptoms;
    }

    public void setCommonSymptoms(String commonSymptoms) {
        this.commonSymptoms = commonSymptoms;
    }

    public String getPossibleDisease() {
        return possibleDisease;
    }

    public void setPossibleDisease(String possibleDisease) {
        this.possibleDisease = possibleDisease;
    }

}
