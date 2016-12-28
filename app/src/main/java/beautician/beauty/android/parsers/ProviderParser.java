package beautician.beauty.android.parsers;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class ProviderParser implements Parcelable {


    String ws_status = "";
    String message = "";
   // List<UserDataParser> servicedata;
    UserDataParser data;
    SearchListParser servicedata;


    public ProviderParser() {

    }

    public String getWs_status() {
        return ws_status;
    }

    public void setWs_status(String ws_status) {
        this.ws_status = ws_status;
    }

//    public List<UserDataParser> getServicedata() {
//        return servicedata;
//    }
//
//    public void setServicedata(List<UserDataParser> servicedata) {
//        this.servicedata = servicedata;
//    }

    public SearchListParser getServicedata() {
        return servicedata;
    }

    public void setServicedata(SearchListParser servicedata) {
        this.servicedata = servicedata;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public UserDataParser getData() {
        return data;
    }

    public void setData(UserDataParser data) {
        this.data = data;
    }

    public ProviderParser(Parcel source) {
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
        //dest.writeList(servicedata);
        dest.writeParcelable(data, arg1);
        dest.writeParcelable(servicedata, arg1);
    }

    public void readFromParcel(Parcel source) {
        ws_status = source.readString();
        message = source.readString();
       // servicedata = new ArrayList<UserDataParser>();
        //source.readList(servicedata, null);
        data = (UserDataParser) source.readParcelable(UserDataParser.class.getClassLoader());
        servicedata = (SearchListParser) source.readParcelable(SearchListParser.class.getClassLoader());
    }

    public static final Creator<ProviderParser> CREATOR = new Creator<ProviderParser>() {

        @Override
        public ProviderParser createFromParcel(Parcel source) {
            return new ProviderParser(source);
        }

        @Override
        public ProviderParser[] newArray(int size) {
            return new ProviderParser[size];
        }
    };


}