package beautician.beauty.android.parsers;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class SearchListParser implements Parcelable {


    List<UserDataParser> topfeatured;
    List<UserDataParser> topnormal;

    public List<UserDataParser> getTopfeatured() {
        return topfeatured;
    }

    public void setTopfeatured(List<UserDataParser> topfeatured) {
        this.topfeatured = topfeatured;
    }

    public List<UserDataParser> getTopnormal() {
        return topnormal;
    }

    public void setTopnormal(List<UserDataParser> topnormal) {
        this.topnormal = topnormal;
    }

    public SearchListParser() {

    }


    public SearchListParser(Parcel source) {
        readFromParcel(source);
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int arg1) {
        dest.writeList(topfeatured);
        dest.writeList(topnormal);

    }

    public void readFromParcel(Parcel source) {
        topfeatured = new ArrayList<UserDataParser>();
        source.readList(topfeatured, null);
        topnormal = new ArrayList<UserDataParser>();
        source.readList(topnormal, null);


    }

    public final Creator<SearchListParser> CREATOR = new Creator<SearchListParser>() {

        @Override
        public SearchListParser createFromParcel(Parcel source) {
            return new SearchListParser(source);
        }

        @Override
        public SearchListParser[] newArray(int size) {
            return new SearchListParser[size];
        }
    };




}