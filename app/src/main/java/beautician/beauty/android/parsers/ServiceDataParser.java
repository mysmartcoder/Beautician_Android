package beautician.beauty.android.parsers;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class ServiceDataParser implements Parcelable {

    String service_id = "";
    String service_userid = "";
    String service_categoryid = "";
    String service_name = "";
    String service_desc = "";
    String service_avgtime = "";
    String service_avgprice = "";
    String service_modified = "";
    String service_created = "";
    String category_id = "";
    String category_namearebic = "";
    String category_name = "";
    String category_desc = "";
    String category_watermark = "";
    String category_modified = "";
    String category_created = "";
    String servicetime_id = "";
    String servicetime_value = "";
    String service_delete = "";

    String appointmentdet_quantity = ""; //For appointment
    String appointmentdet_price = ""; //For appointment
    List<ServicePictureDataParser> serviceimage;
    CommentsDataParser comment;


    public ServiceDataParser() {

    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getService_userid() {
        return service_userid;
    }

    public void setService_userid(String service_userid) {
        this.service_userid = service_userid;
    }

    public String getService_categoryid() {
        return service_categoryid;
    }

    public void setService_categoryid(String service_categoryid) {
        this.service_categoryid = service_categoryid;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getService_desc() {
        return service_desc;
    }

    public void setService_desc(String service_desc) {
        this.service_desc = service_desc;
    }

    public String getService_avgtime() {
        return service_avgtime;
    }

    public void setService_avgtime(String service_avgtime) {
        this.service_avgtime = service_avgtime;
    }

    public String getService_avgprice() {
        return service_avgprice;
    }

    public void setService_avgprice(String service_avgprice) {
        this.service_avgprice = service_avgprice;
    }

    public String getService_modified() {
        return service_modified;
    }

    public void setService_modified(String service_modified) {
        this.service_modified = service_modified;
    }

    public String getService_created() {
        return service_created;
    }

    public void setService_created(String service_created) {
        this.service_created = service_created;
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

    public String getCategory_desc() {
        return category_desc;
    }

    public void setCategory_desc(String category_desc) {
        this.category_desc = category_desc;
    }

    public String getCategory_watermark() {
        return category_watermark;
    }

    public void setCategory_watermark(String category_watermark) {
        this.category_watermark = category_watermark;
    }

    public String getCategory_modified() {
        return category_modified;
    }

    public void setCategory_modified(String category_modified) {
        this.category_modified = category_modified;
    }

    public String getCategory_created() {
        return category_created;
    }

    public void setCategory_created(String category_created) {
        this.category_created = category_created;
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

    public List<ServicePictureDataParser> getServiceimage() {
        return serviceimage;
    }

    public void setServiceimage(List<ServicePictureDataParser> serviceimage) {
        this.serviceimage = serviceimage;
    }

    public String getAppointmentdet_quantity() {
        return appointmentdet_quantity;
    }

    public void setAppointmentdet_quantity(String appointmentdet_quantity) {
        this.appointmentdet_quantity = appointmentdet_quantity;
    }

    public String getAppointmentdet_price() {
        return appointmentdet_price;
    }

    public void setAppointmentdet_price(String appointmentdet_price) {
        this.appointmentdet_price = appointmentdet_price;
    }

    public CommentsDataParser getComment() {
        return comment;
    }

    public void setComment(CommentsDataParser comment) {
        this.comment = comment;
    }

    public ServiceDataParser(Parcel source) {
        readFromParcel(source);
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int arg1) {
        dest.writeString(service_id);
        dest.writeString(service_userid);
        dest.writeString(service_categoryid);
        dest.writeString(service_name);
        dest.writeString(service_desc);
        dest.writeString(service_avgtime);
        dest.writeString(service_avgprice);
        dest.writeString(service_modified);
        dest.writeString(service_created);
        dest.writeString(category_id);
        dest.writeString(category_name);
        dest.writeString(category_namearebic);
        dest.writeString(category_desc);
        dest.writeString(category_watermark);
        dest.writeString(category_modified);
        dest.writeString(category_created);
        dest.writeString(servicetime_id);
        dest.writeString(servicetime_value);
        dest.writeString(appointmentdet_quantity);
        dest.writeString(appointmentdet_price);
        dest.writeString(service_delete);
        dest.writeList(serviceimage);
        dest.writeParcelable(comment, arg1);


    }

    public void readFromParcel(Parcel source) {
        service_id = source.readString();
        service_userid = source.readString();
        service_categoryid = source.readString();
        service_name = source.readString();
        service_desc = source.readString();
        service_avgtime = source.readString();
        service_avgprice = source.readString();
        service_modified = source.readString();
        service_created = source.readString();
        category_id = source.readString();
        category_name = source.readString();
        category_namearebic = source.readString();
        category_desc = source.readString();
        category_watermark = source.readString();
        category_modified = source.readString();
        category_created = source.readString();
        servicetime_id = source.readString();
        servicetime_value = source.readString();
        appointmentdet_quantity = source.readString();
        appointmentdet_price = source.readString();
        service_delete = source.readString();
        serviceimage = new ArrayList<ServicePictureDataParser>();
        source.readList(serviceimage, null);
        comment = (CommentsDataParser) source.readParcelable(CommentsDataParser.class.getClassLoader());

    }

    public final Creator<ServiceDataParser> CREATOR = new Creator<ServiceDataParser>() {

        @Override
        public ServiceDataParser createFromParcel(Parcel source) {
            return new ServiceDataParser(source);
        }

        @Override
        public ServiceDataParser[] newArray(int size) {
            return new ServiceDataParser[size];
        }
    };


    public String getService_delete() {
        return service_delete;
    }

    public void setService_delete(String service_delete) {
        this.service_delete = service_delete;
    }

    public String getCategory_namearebic() {
        return category_namearebic;
    }

    public void setCategory_namearebic(String category_namearebic) {
        this.category_namearebic = category_namearebic;
    }
}