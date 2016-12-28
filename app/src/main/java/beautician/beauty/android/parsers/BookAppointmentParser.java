package beautician.beauty.android.parsers;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class BookAppointmentParser implements Parcelable {

    String category_id = "";
    String quantity = "";
    String service_id = "";
    String total_price = "";
    String service_name = "";
    String category_name = "";



    public BookAppointmentParser() {

    }

    public BookAppointmentParser(Parcel source) {
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
        dest.writeString(quantity);
        dest.writeString(service_id);
        dest.writeString(total_price);
        dest.writeString(category_name);
        dest.writeString(service_name);
    }

    public void readFromParcel(Parcel source) {
        category_id = source.readString();
        quantity = source.readString();
        service_id = source.readString();
        total_price = source.readString();
        category_name = source.readString();
        service_name = source.readString();
    }

    public final Creator<BookAppointmentParser> CREATOR = new Creator<BookAppointmentParser>() {

        @Override
        public BookAppointmentParser createFromParcel(Parcel source) {
            return new BookAppointmentParser(source);
        }

        @Override
        public BookAppointmentParser[] newArray(int size) {
            return new BookAppointmentParser[size];
        }
    };


    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }
}