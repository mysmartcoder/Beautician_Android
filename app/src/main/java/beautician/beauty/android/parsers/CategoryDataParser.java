package beautician.beauty.android.parsers;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class CategoryDataParser implements Parcelable {

    String category_id = "";
    String category_namearebic = "";
    String category_name = "";
    String category_watermark = "";
    String servicetime_id = "";
    String servicetime_value = "";
    private boolean checked = false;

    String country_id = "";
    String country_namearebic = "";
    String country_name = "";

    String city_id = "";
    String city_namearabic = "";
    String city_name = "";
    String city_lat = "";
    String city_lng = "";
    String city_radius = "";
    String city_selected = "";

    List<ServiceDataParser> services;

    public CategoryDataParser() {

    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCategory_watermark() {
        return category_watermark;
    }

    public void setCategory_watermark(String category_watermark) {
        this.category_watermark = category_watermark;
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

    public List<ServiceDataParser> getServices() {
        return services;
    }

    public void setServices(List<ServiceDataParser> services) {
        this.services = services;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public CategoryDataParser(Parcel source) {
        readFromParcel(source);
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int arg1) {
        dest.writeString(category_id);
        dest.writeString(category_name);
        dest.writeString(category_namearebic);
        dest.writeString(category_watermark);
        dest.writeString(servicetime_id);
        dest.writeString(servicetime_value);
        dest.writeString(country_id);
        dest.writeString(country_name);
        dest.writeString(country_namearebic);
        dest.writeString(city_id);
        dest.writeString(city_name);
        dest.writeString(city_namearabic);
        dest.writeString(city_lat);
        dest.writeString(city_lng);
        dest.writeString(city_selected);
        dest.writeString(city_radius);
        dest.writeList(services);
    }

    public void readFromParcel(Parcel source) {
        category_id = source.readString();
        category_name = source.readString();
        category_namearebic = source.readString();
        category_watermark = source.readString();
        servicetime_id = source.readString();
        servicetime_value = source.readString();
        country_id = source.readString();
        country_name = source.readString();
        country_namearebic = source.readString();
        city_id = source.readString();
        city_name = source.readString();
        city_namearabic = source.readString();
        city_lat = source.readString();
        city_lng = source.readString();
        city_selected = source.readString();
        city_radius = source.readString();
        services = new ArrayList<ServiceDataParser>();
        source.readList(services, null);

    }

    public final Creator<CategoryDataParser> CREATOR = new Creator<CategoryDataParser>() {

        @Override
        public CategoryDataParser createFromParcel(Parcel source) {
            return new CategoryDataParser(source);
        }

        @Override
        public CategoryDataParser[] newArray(int size) {
            return new CategoryDataParser[size];
        }
    };


    public String getCategory_namearebic() {
        return category_namearebic;
    }

    public void setCategory_namearebic(String category_namearebic) {
        this.category_namearebic = category_namearebic;
    }

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
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

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
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

    public String getCity_lat() {
        return city_lat;
    }

    public void setCity_lat(String city_lat) {
        this.city_lat = city_lat;
    }

    public String getCity_lng() {
        return city_lng;
    }

    public void setCity_lng(String city_lng) {
        this.city_lng = city_lng;
    }

    public String getCity_selected() {
        return city_selected;
    }

    public void setCity_selected(String city_selected) {
        this.city_selected = city_selected;
    }

    public String getCity_radius() {
        return city_radius;
    }

    public void setCity_radius(String city_radius) {
        this.city_radius = city_radius;
    }
}