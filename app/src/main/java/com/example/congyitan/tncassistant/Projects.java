package com.example.congyitan.tncassistant;

/**
 * Created by Congyi Tan on 10/24/2015.
 */
public class Projects {

    int BlockNo;
    int PostalCode;
    String StreetName;
    String TownCouncil;
    String Phase;
    String TimeStamp;

    public Projects() {
    }

    public Projects(int blockNo, int postalCode, String streetName, String townCouncil, String phase, String timeStamp) {
        BlockNo = blockNo;
        PostalCode = postalCode;
        StreetName = streetName;
        TownCouncil = townCouncil;
        Phase = phase;
        TimeStamp = timeStamp;
    }


    public int getBlockNo() {
        return BlockNo;
    }

    public void setBlockNo(int blockNo) {
        BlockNo = blockNo;
    }

    public int getPostalCode() {
        return PostalCode;
    }

    public void setPostalCode(int postalCode) {
        PostalCode = postalCode;
    }

    public String getStreetName() {
        return StreetName;
    }

    public void setStreetName(String streetName) {
        StreetName = streetName;
    }

    public String getTownCouncil() {
        return TownCouncil;
    }

    public void setTownCouncil(String townCouncil) {
        TownCouncil = townCouncil;
    }

    public String getPhase() {
        return Phase;
    }

    public void setPhase(String phase) {
        Phase = phase;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

}
