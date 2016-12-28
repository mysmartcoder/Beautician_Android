package beautician.beauty.android.parsers;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class UserDataParser implements Parcelable {

    String user_id = "";
    String username = "";
    String user_fname = "";
    String user_lname = "";
    String email = "";
    String user_phone = "";
    String user_image = "";
    String user_location = "";
    String user_lat = "";
    String user_lng = "";
    String user_hash = "";
    String user_type = "";
    String location_policy = "";
    String user_city = "";
    String user_country = "";
    String user_lowestsatisfy = "";
    String user_lowestcommited = "";
    String user_maxpicture = "";
    String totalsatisfy="";
    String totalcommited="";
    String confirmedappointment="";
    String user_mindays="";
    String user_maxdays="";
    String totalcredit="";
    String user_active = "";
    String country_namearebic = "";
    String country_name = "";
    String city_namearabic = "";
    String city_name = "";
    String user_minapptfee = "";
    String user_transportfee = "";


    List<ScheduleDataParser> schedule;
    List<CategoryDataParser> categories;
    List<CommentsDataParser> comments;




    public UserDataParser() {

    }


    public UserDataParser(Parcel source) {
        readFromParcel(source);
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int arg1) {
        dest.writeString(user_id);
        dest.writeString(username);
        dest.writeString(user_fname);
        dest.writeString(user_lname);
        dest.writeString(email);
        dest.writeString(user_image);
        dest.writeString(user_location);
        dest.writeString(user_lat);
        dest.writeString(user_lng);
        dest.writeString(user_hash);
        dest.writeString(user_type);
        dest.writeString(location_policy);
        dest.writeString(user_city);
        dest.writeString(user_country);
        dest.writeString(user_lowestsatisfy);
        dest.writeString(user_lowestcommited);
        dest.writeString(user_maxpicture);
        dest.writeString(totalsatisfy);
        dest.writeString(totalcommited);
        dest.writeString(confirmedappointment);
        dest.writeString(user_maxdays);
        dest.writeString(user_mindays);
        dest.writeString(totalcredit);
        dest.writeString(user_active);
        dest.writeString(country_name);
        dest.writeString(country_namearebic);
        dest.writeString(city_name);
        dest.writeString(city_namearabic);
        dest.writeString(user_minapptfee);
        dest.writeString(user_transportfee);
        dest.writeList(schedule);
        dest.writeList(categories);
        dest.writeList(comments);

    }

    public void readFromParcel(Parcel source) {
        user_id = source.readString();
        username = source.readString();
        user_fname = source.readString();
        user_lname = source.readString();
        email = source.readString();
        user_image = source.readString();
        user_location = source.readString();
        user_lat = source.readString();
        user_lng = source.readString();
        user_hash = source.readString();
        user_type = source.readString();
        location_policy = source.readString();
        user_city = source.readString();
        user_country = source.readString();
        user_lowestsatisfy = source.readString();
        user_lowestcommited = source.readString();
        user_maxpicture = source.readString();
        totalsatisfy= source.readString();
        totalcommited = source.readString();
        confirmedappointment= source.readString();
        user_maxdays= source.readString();
        user_mindays= source.readString();
        totalcredit= source.readString();
        user_active= source.readString();
        country_name = source.readString();
        country_namearebic = source.readString();
        city_name = source.readString();
        city_namearabic = source.readString();
        user_minapptfee = source.readString();
        user_transportfee = source.readString();
        schedule = new ArrayList<ScheduleDataParser>();
        source.readList(schedule, null);
        categories = new ArrayList<CategoryDataParser>();
        source.readList(categories, null);
        comments = new ArrayList<CommentsDataParser>();
        source.readList(comments, null);


    }

    public final Creator<UserDataParser> CREATOR = new Creator<UserDataParser>() {

        @Override
        public UserDataParser createFromParcel(Parcel source) {
            return new UserDataParser(source);
        }

        @Override
        public UserDataParser[] newArray(int size) {
            return new UserDataParser[size];
        }
    };


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUser_fname() {
        return user_fname;
    }

    public void setUser_fname(String user_fname) {
        this.user_fname = user_fname;
    }

    public String getUser_lname() {
        return user_lname;
    }

    public void setUser_lname(String user_lname) {
        this.user_lname = user_lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUser_location() {
        return user_location;
    }

    public void setUser_location(String user_location) {
        this.user_location = user_location;
    }

    public String getUser_lat() {
        return user_lat;
    }

    public void setUser_lat(String user_lat) {
        this.user_lat = user_lat;
    }

    public String getUser_lng() {
        return user_lng;
    }

    public void setUser_lng(String user_lng) {
        this.user_lng = user_lng;
    }

    public String getUser_hash() {
        return user_hash;
    }

    public void setUser_hash(String user_hash) {
        this.user_hash = user_hash;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getLocation_policy() {
        return location_policy;
    }

    public void setLocation_policy(String location_policy) {
        this.location_policy = location_policy;
    }

    public String getUser_city() {
        return user_city;
    }

    public void setUser_city(String user_city) {
        this.user_city = user_city;
    }

    public String getUser_country() {
        return user_country;
    }

    public void setUser_country(String user_country) {
        this.user_country = user_country;
    }

    public String getUser_lowestsatisfy() {
        return user_lowestsatisfy;
    }

    public void setUser_lowestsatisfy(String user_lowestsatisfy) {
        this.user_lowestsatisfy = user_lowestsatisfy;
    }

    public String getUser_lowestcommited() {
        return user_lowestcommited;
    }

    public void setUser_lowestcommited(String user_lowestcommited) {
        this.user_lowestcommited = user_lowestcommited;
    }

    public String getUser_maxpicture() {
        return user_maxpicture;
    }

    public void setUser_maxpicture(String user_maxpicture) {
        this.user_maxpicture = user_maxpicture;
    }
    public String getTotalsatisfy() {
        return totalsatisfy;
    }

    public void setTotalsatisfy(String totalsatisfy) {
        this.totalsatisfy = totalsatisfy;
    }

    public String getTotalcommited() {
        return totalcommited;
    }

    public void setTotalcommited(String totalcommited) {
        this.totalcommited = totalcommited;
    }

    public List<ScheduleDataParser> getSchedule() {
        return schedule;
    }

    public String getConfirmedappointment() {
        return confirmedappointment;
    }

    public void setConfirmedappointment(String confirmedappointment) {
        this.confirmedappointment = confirmedappointment;
    }
    public void setSchedule(List<ScheduleDataParser> schedule) {
        this.schedule = schedule;
    }

    public List<CategoryDataParser> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryDataParser> categories) {
        this.categories = categories;
    }

    public List<CommentsDataParser> getComments() {
        return comments;
    }

    public void setComments(List<CommentsDataParser> comments) {
        this.comments = comments;
    }


    public String getUser_mindays() {
        return user_mindays;
    }

    public void setUser_mindays(String user_mindays) {
        this.user_mindays = user_mindays;
    }

    public String getUser_maxdays() {
        return user_maxdays;
    }

    public void setUser_maxdays(String user_maxdays) {
        this.user_maxdays = user_maxdays;
    }

    public String getTotalcredit() {
        return totalcredit;
    }

    public void setTotalcredit(String totalcredit) {
        this.totalcredit = totalcredit;
    }

    public String getUser_active() {
        return user_active;
    }

    public void setUser_active(String user_active) {
        this.user_active = user_active;
    }

    public String getCountry_namearebic() {
        return country_namearebic;
    }

    public void setCountry_namearebic(String country_namearebic) {
        this.country_namearebic = country_namearebic;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public String getCity_namearabic() {
        return city_namearabic;
    }

    public void setCity_namearabic(String city_namearabic) {
        this.city_namearabic = city_namearabic;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getUser_minapptfee() {
        return user_minapptfee;
    }

    public void setUser_minapptfee(String user_minapptfee) {
        this.user_minapptfee = user_minapptfee;
    }

    public String getUser_transportfee() {
        return user_transportfee;
    }

    public void setUser_transportfee(String user_transportfee) {
        this.user_transportfee = user_transportfee;
    }
}
