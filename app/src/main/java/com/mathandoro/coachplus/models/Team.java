package com.mathandoro.coachplus.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dominik on 31.03.17.
 */

public class Team implements Parcelable{
    protected String _id;
    protected String name;
    protected boolean isPublic;
    protected String image;
    protected int memberCount;


    public Team(String _id, String name, boolean isPublic, int memberCount) {
        this._id = _id;
        this.name = name;
        this.isPublic = isPublic;
        this.memberCount = memberCount;
    }

    protected Team(Parcel in) {
        _id = in.readString();
        name = in.readString();
        image = in.readString();
        isPublic = in.readByte() != 0;
        memberCount = in.readInt();
    }

    public static final Creator<Team> CREATOR = new Creator<Team>() {
        @Override
        public Team createFromParcel(Parcel in) {
            return new Team(in);
        }

        @Override
        public Team[] newArray(int size) {
            return new Team[size];
        }
    };

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(name);
        dest.writeString(image);
        dest.writeByte((byte) (isPublic ? 1 : 0));
        dest.writeInt(memberCount);
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }
}
