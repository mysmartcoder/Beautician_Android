package beautician.beauty.android.parsers;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class CategoryListParser implements Parcelable {

	List<CategoryDataParser> data;

	String ws_status = "";
	String message = "";

	public CategoryListParser() {

	}

	public String getWs_status() {
		return ws_status;
	}

	public void setWs_status(String ws_status) {
		this.ws_status = ws_status;
	}

	public List<CategoryDataParser> getData() {
		return data;
	}

	public void setData(List<CategoryDataParser> data) {
		this.data = data;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public CategoryListParser(Parcel source) {
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
		data = new ArrayList<CategoryDataParser>();
		source.readList(data, null);
	}

	public static final Creator<CategoryListParser> CREATOR = new Creator<CategoryListParser>() {

		@Override
		public CategoryListParser createFromParcel(Parcel source) {
			return new CategoryListParser(source);
		}

		@Override
		public CategoryListParser[] newArray(int size) {
			return new CategoryListParser[size];
		}
	};


}