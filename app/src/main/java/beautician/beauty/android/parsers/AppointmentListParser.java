package beautician.beauty.android.parsers;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class AppointmentListParser implements Parcelable {

    List<AppointmentDataParser> data;

    String ws_status = "";
    String message = "";


    public AppointmentListParser() {

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

    public List<AppointmentDataParser> getData() {
        return data;
    }

    public void setData(List<AppointmentDataParser> data) {
        this.data = data;
    }

    public AppointmentListParser(Parcel source) {
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
        dest.writeList(data);
    }

    public void readFromParcel(Parcel source) {
        ws_status = source.readString();
        message = source.readString();
        data = new ArrayList<AppointmentDataParser>();
        source.readList(data, null);
    }

    public static final Creator<AppointmentListParser> CREATOR = new Creator<AppointmentListParser>() {

        @Override
        public AppointmentListParser createFromParcel(Parcel source) {
            return new AppointmentListParser(source);
        }

        @Override
        public AppointmentListParser[] newArray(int size) {
            return new AppointmentListParser[size];
        }
    };


}