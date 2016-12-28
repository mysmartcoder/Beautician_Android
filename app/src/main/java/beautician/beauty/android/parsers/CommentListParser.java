package beautician.beauty.android.parsers;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class CommentListParser implements Parcelable {


	List<CommentsDataParser> data;
	String ws_status = "";
	String message = "";

	public CommentListParser() {

	}
	public List<CommentsDataParser> getData() {
		return data;
	}

	public void setData(List<CommentsDataParser> data) {
		this.data = data;
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


	public CommentListParser(Parcel source) {
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
		data = new ArrayList<CommentsDataParser>();
		source.readList(data, null);
	}

	public static final Creator<CommentListParser> CREATOR = new Creator<CommentListParser>() {

		@Override
		public CommentListParser createFromParcel(Parcel source) {
			return new CommentListParser(source);
		}

		@Override
		public CommentListParser[] newArray(int size) {
			return new CommentListParser[size];
		}
	};


}