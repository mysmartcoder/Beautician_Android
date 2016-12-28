package beautician.beauty.android.parsers;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class LoginParser implements Parcelable {

	UserDataParser data;

	String ws_status = "";
	String message = "";
	String user_id = "";
	String image_url = "";
	String appointment_id = "";

	List<UserDataParser> suggestedprovider;

	public LoginParser() {

	}

	public String getWs_status() {
		return ws_status;
	}

	public void setWs_status(String ws_status) {
		this.ws_status = ws_status;
	}

	public UserDataParser getData() {
		return data;
	}

	public void setData(UserDataParser data) {
		this.data = data;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LoginParser(Parcel source) {
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
		dest.writeString(user_id);
		dest.writeString(image_url);
		dest.writeString(appointment_id);
		dest.writeParcelable(data, arg1);
		dest.writeList(suggestedprovider);
	}

	public void readFromParcel(Parcel source) {
		ws_status = source.readString();
		message = source.readString();
		user_id = source.readString();
		image_url = source.readString();
		appointment_id = source.readString();
		data = (UserDataParser) source.readParcelable(UserDataParser.class.getClassLoader());
		suggestedprovider = new ArrayList<UserDataParser>();
		source.readList(suggestedprovider, null);
	}

	public static final Creator<LoginParser> CREATOR = new Creator<LoginParser>() {

		@Override
		public LoginParser createFromParcel(Parcel source) {
			return new LoginParser(source);
		}

		@Override
		public LoginParser[] newArray(int size) {
			return new LoginParser[size];
		}
	};


	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public String getAppointment_id() {
		return appointment_id;
	}

	public void setAppointment_id(String appointment_id) {
		this.appointment_id = appointment_id;
	}

	public List<UserDataParser> getSuggestedprovider() {
		return suggestedprovider;
	}

	public void setSuggestedprovider(List<UserDataParser> suggestedprovider) {
		this.suggestedprovider = suggestedprovider;
	}
}