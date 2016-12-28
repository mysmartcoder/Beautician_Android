package beautician.beauty.android.parsers;

import android.os.Parcel;
import android.os.Parcelable;

public class ServicePictureDataParser implements Parcelable {

    String servicepicture_id = "";
    String servicepicture_serviceid = "";
    String servicepicture_name = "";
    String servicetime_id ="";
    String servicetime_value="";

    public ServicePictureDataParser() {

    }

    public String getServicepicture_id() {
        return servicepicture_id;
    }

    public void setServicepicture_id(String servicepicture_id) {
        this.servicepicture_id = servicepicture_id;
    }

    public String getServicepicture_serviceid() {
        return servicepicture_serviceid;
    }

    public void setServicepicture_serviceid(String servicepicture_serviceid) {
        this.servicepicture_serviceid = servicepicture_serviceid;
    }

    public String getServicepicture_name() {
        return servicepicture_name;
    }

    public void setServicepicture_name(String servicepicture_name) {
        this.servicepicture_name = servicepicture_name;
    }

    public String getServicetime_id() {
        return servicetime_id;
    }

    public void setServicetime_id(String servicetime_id) {
        this.servicetime_id = servicetime_id;
    }

    public String getServicetime_value() {
        return servicetime_value;
    }

    public void setServicetime_value(String servicetime_value) {
        this.servicetime_value = servicetime_value;
    }



    public ServicePictureDataParser(Parcel source) {
        readFromParcel(source);
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int arg1) {
        dest.writeString(servicepicture_id);
        dest.writeString(servicepicture_serviceid);
        dest.writeString(servicepicture_name);
        dest.writeString(servicetime_id);
        dest.writeString(servicetime_value);

    }

    public void readFromParcel(Parcel source) {
        servicepicture_id = source.readString();
        servicepicture_serviceid = source.readString();
        servicepicture_name = source.readString();
        servicetime_id = source.readString();
        servicetime_value = source.readString();

    }

    public final Creator<ServicePictureDataParser> CREATOR = new Creator<ServicePictureDataParser>() {

        @Override
        public ServicePictureDataParser createFromParcel(Parcel source) {
            return new ServicePictureDataParser(source);
        }

        @Override
        public ServicePictureDataParser[] newArray(int size) {
            return new ServicePictureDataParser[size];
        }
    };


}