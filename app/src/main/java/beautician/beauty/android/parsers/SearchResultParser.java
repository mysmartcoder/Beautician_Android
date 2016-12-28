package beautician.beauty.android.parsers;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class SearchResultParser implements Parcelable {


    String ws_status = "";
    String message = "";
    SearchListParser data;

    public SearchResultParser() {

    }


    public SearchListParser getData() {
        return data;
    }

    public void setData(SearchListParser data) {
        this.data = data;
    }





    public String getWs_status() {
        return ws_status;
    }

    public void setWs_status(String ws_status) {
        this.ws_status = ws_status;
    }



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public SearchResultParser(Parcel source) {
        readFromParcel(source);
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int arg1) {
        dest.writeString(ws_status);
        dest.writeString(message);
        dest.writeParcelable(data, arg1);
//        dest.writeList(data);
    }

    public void readFromParcel(Parcel source) {
        ws_status = source.readString();
        message = source.readString();
        data = (SearchListParser) source.readParcelable(SearchListParser.class.getClassLoader());
//        data = new ArrayList<UserDataParser>();
//        source.readList(data, null);
    }

    public static final Creator<SearchResultParser> CREATOR = new Creator<SearchResultParser>() {

        @Override
        public SearchResultParser createFromParcel(Parcel source) {
            return new SearchResultParser(source);
        }

        @Override
        public SearchResultParser[] newArray(int size) {
            return new SearchResultParser[size];
        }
    };


}