package beautician.beauty.android.parsers;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class ServiceListParser implements Parcelable {

	List<ServiceDataParser> servicedata;

	String ws_status = "";
	String message = "";

	List<ServicePictureDataParser> serviceimage;



	public ServiceListParser() {

	}

	public String getWs_status() {
		return ws_status;
	}

	public void setWs_status(String ws_status) {
		this.ws_status = ws_status;
	}

	public List<ServiceDataParser> getData() {
		return servicedata;
	}

	public void setData(List<ServiceDataParser> data) {
		this.servicedata = data;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<ServicePictureDataParser> getServiceimage() {
		return serviceimage;
	}

	public void setServiceimage(List<ServicePictureDataParser> serviceimage) {
		this.serviceimage = serviceimage;
	}

	public ServiceListParser(Parcel source) {
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
		dest.writeList(servicedata);
		dest.writeList(serviceimage);
	}

	public void readFromParcel(Parcel source) {
		ws_status = source.readString();
		message = source.readString();
		servicedata = new ArrayList<ServiceDataParser>();
		serviceimage = new ArrayList<ServicePictureDataParser>();
		source.readList(servicedata, null);
		source.readList(serviceimage, null);
	}

	public static final Creator<ServiceListParser> CREATOR = new Creator<ServiceListParser>() {

		@Override
		public ServiceListParser createFromParcel(Parcel source) {
			return new ServiceListParser(source);
		}

		@Override
		public ServiceListParser[] newArray(int size) {
			return new ServiceListParser[size];
		}
	};


}