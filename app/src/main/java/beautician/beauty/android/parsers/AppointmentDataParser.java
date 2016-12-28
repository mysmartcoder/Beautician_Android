package beautician.beauty.android.parsers;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class AppointmentDataParser implements Parcelable {

    String appointment_id = "";
    String appointment_seekerid = "";
    String appointment_providerid = "";
    String appointment_refnumber = "";
    String appointment_starttime = "";
    String appointment_location = "";
    String appointment_lat = "";
    String appointment_lng = "";
    String appointment_status = "";
    String appointment_status_seeker = "";
    String appointment_status_provider = "";
    String appointment_locationpolicy = "";
    String seekername = "";
    String seekeremail = "";
    String seekerphone = "";
    String providername = "";
    String providerphone = "";
    String provideremail = "";
    String user_type = "";
    String seekerhascommented = "";
    String providerhascommented = "";
    String appointment_depositamt = "";
    String appointment_transportfee = "";

    List<ServiceDataParser> appointmentinfo;


    public AppointmentDataParser() {

    }



    public AppointmentDataParser(Parcel source) {
        readFromParcel(source);
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int arg1) {
        dest.writeString(appointment_id);
        dest.writeString(appointment_seekerid);
        dest.writeString(appointment_providerid);
        dest.writeString(appointment_refnumber);
        dest.writeString(appointment_starttime);
        dest.writeString(appointment_location);
        dest.writeString(appointment_lat);
        dest.writeString(appointment_lng);
        dest.writeString(appointment_status);
        dest.writeString(appointment_status_seeker);
        dest.writeString(appointment_status_provider);
        dest.writeString(appointment_locationpolicy);
        dest.writeString(seekername);
        dest.writeString(providername);
        dest.writeString(seekeremail);
        dest.writeString(provideremail);
        dest.writeString(seekerphone);
        dest.writeString(providerphone);
        dest.writeString(user_type);
        dest.writeString(seekerhascommented);
        dest.writeString(providerhascommented);
        dest.writeString(appointment_depositamt);
        dest.writeString(appointment_transportfee);
        dest.writeList(appointmentinfo);


    }

    public void readFromParcel(Parcel source) {
        appointment_id = source.readString();
        appointment_seekerid = source.readString();
        appointment_providerid = source.readString();
        appointment_refnumber = source.readString();
        appointment_starttime = source.readString();
        appointment_location = source.readString();
        appointment_lat = source.readString();
        appointment_lng = source.readString();
        appointment_status = source.readString();
        appointment_status_seeker = source.readString();
        appointment_status_provider = source.readString();
        appointment_locationpolicy = source.readString();
        seekername = source.readString();
        providername = source.readString();
        seekeremail = source.readString();
        provideremail = source.readString();
        seekerphone = source.readString();
        providerphone = source.readString();
        user_type = source.readString();
        seekerhascommented = source.readString();
        providerhascommented = source.readString();
        appointment_depositamt = source.readString();
        appointment_transportfee = source.readString();
        appointmentinfo = new ArrayList<ServiceDataParser>();
        source.readList(appointmentinfo, null);

    }

    public final Creator<AppointmentDataParser> CREATOR = new Creator<AppointmentDataParser>() {

        @Override
        public AppointmentDataParser createFromParcel(Parcel source) {
            return new AppointmentDataParser(source);
        }

        @Override
        public AppointmentDataParser[] newArray(int size) {
            return new AppointmentDataParser[size];
        }
    };


    public String getAppointment_id() {
        return appointment_id;
    }

    public void setAppointment_id(String appointment_id) {
        this.appointment_id = appointment_id;
    }

    public String getAppointment_seekerid() {
        return appointment_seekerid;
    }

    public void setAppointment_seekerid(String appointment_seekerid) {
        this.appointment_seekerid = appointment_seekerid;
    }

    public String getAppointment_providerid() {
        return appointment_providerid;
    }

    public void setAppointment_providerid(String appointment_providerid) {
        this.appointment_providerid = appointment_providerid;
    }

    public String getAppointment_refnumber() {
        return appointment_refnumber;
    }

    public void setAppointment_refnumber(String appointment_refnumber) {
        this.appointment_refnumber = appointment_refnumber;
    }

    public String getAppointment_starttime() {
        return appointment_starttime;
    }

    public void setAppointment_starttime(String appointment_starttime) {
        this.appointment_starttime = appointment_starttime;
    }

    public String getAppointment_location() {
        return appointment_location;
    }

    public void setAppointment_location(String appointment_location) {
        this.appointment_location = appointment_location;
    }

    public String getAppointment_lat() {
        return appointment_lat;
    }

    public void setAppointment_lat(String appointment_lat) {
        this.appointment_lat = appointment_lat;
    }

    public String getAppointment_lng() {
        return appointment_lng;
    }

    public void setAppointment_lng(String appointment_lng) {
        this.appointment_lng = appointment_lng;
    }

    public String getAppointment_status_seeker() {
        return appointment_status_seeker;
    }

    public void setAppointment_status_seeker(String appointment_status_seeker) {
        this.appointment_status_seeker = appointment_status_seeker;
    }

    public String getAppointment_status_provider() {
        return appointment_status_provider;
    }

    public void setAppointment_status_provider(String appointment_status_provider) {
        this.appointment_status_provider = appointment_status_provider;
    }

    public String getAppointment_locationpolicy() {
        return appointment_locationpolicy;
    }

    public void setAppointment_locationpolicy(String appointment_locationpolicy) {
        this.appointment_locationpolicy = appointment_locationpolicy;
    }

    public String getSeekername() {
        return seekername;
    }

    public void setSeekername(String seekername) {
        this.seekername = seekername;
    }

    public String getProvidername() {
        return providername;
    }

    public void setProvidername(String providername) {
        this.providername = providername;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public List<ServiceDataParser> getAppointmentinfo() {
        return appointmentinfo;
    }

    public void setAppointmentinfo(List<ServiceDataParser> appointmentinfo) {
        this.appointmentinfo = appointmentinfo;
    }

    public String getSeekeremail() {
        return seekeremail;
    }

    public void setSeekeremail(String seekeremail) {
        this.seekeremail = seekeremail;
    }

    public String getSeekerphone() {
        return seekerphone;
    }

    public void setSeekerphone(String seekerphone) {
        this.seekerphone = seekerphone;
    }

    public String getProviderphone() {
        return providerphone;
    }

    public void setProviderphone(String providerphone) {
        this.providerphone = providerphone;
    }

    public String getProvideremail() {
        return provideremail;
    }

    public void setProvideremail(String provideremail) {
        this.provideremail = provideremail;
    }

    public String getAppointment_status() {
        return appointment_status;
    }

    public void setAppointment_status(String appointment_status) {
        this.appointment_status = appointment_status;
    }

    public String getSeekerhascommented() {
        return seekerhascommented;
    }

    public void setSeekerhascommented(String seekerhascommented) {
        this.seekerhascommented = seekerhascommented;
    }

    public String getProviderhascommented() {
        return providerhascommented;
    }

    public void setProviderhascommented(String providerhascommented) {
        this.providerhascommented = providerhascommented;
    }

    public String getAppointment_depositamt() {
        return appointment_depositamt;
    }

    public void setAppointment_depositamt(String appointment_depositamt) {
        this.appointment_depositamt = appointment_depositamt;
    }

    public String getAppointment_transportfee() {
        return appointment_transportfee;
    }

    public void setAppointment_transportfee(String appointment_transportfee) {
        this.appointment_transportfee = appointment_transportfee;
    }
}