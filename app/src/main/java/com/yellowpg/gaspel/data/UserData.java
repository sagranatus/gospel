package com.yellowpg.gaspel.data;

public class UserData {

    private String mUid = null;
    private String mUserId = null;
    private String mEmail = null;
    private String mName = null;
    private String mChristName = null;
    private String mAge = null;
    private String mRegion = null;
    private String mCathedral = null;
    private String mCreated = null;

    public UserData(String aUid, String aUserId, String aEmail, String aName, String aChristName, String aAge, String aRegion, String aCathedral, String aCreated){
        this.mUid = aUid;
        this.mUserId = aUserId;
        this.mEmail = aEmail;
        this.mName = aName;
        this.mAge = aAge;
        this.mRegion = aRegion;
        this.mCathedral = aCathedral;
        this.mChristName = aChristName;
        this.mCreated = aCreated;
    }

    public String getUid(){ return this.mUid; }
    public String getUserId(){ return this.mUserId; }
    public String getEmail(){return this.mEmail; }
    public String getName(){return this.mName; }
    public String getChristName(){return this.mChristName; }
    public String getAge(){return this.mAge; }
    public String getRegion(){return this.mRegion; }
    public String getCathedral(){return this.mCathedral; }
    public String getCreated(){return this.mCreated; }

    public String[] getcDataArray(){
        String[] cData = {
                this.mUid,
                this.mUserId,
                this.mEmail,
                this.mName,
                this.mChristName,
                this.mAge,
                this.mRegion,
                this.mCathedral,
                this.mCreated
        };
        return cData;
    }
}
