package beautician.beauty.android.parsers;

import android.os.Parcel;
import android.os.Parcelable;

public class DepositStatusParser implements Parcelable {

    DepositDataParser data;

    String ws_status = "";
    String message = "";
    String paymentstatus = "";
    String url = "";


    public DepositStatusParser() {

    }


    public DepositStatusParser(Parcel source) {
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
        dest.writeString(paymentstatus);
        dest.writeString(url);
        dest.writeParcelable(data, arg1);
    }

    public void readFromParcel(Parcel source) {
        ws_status = source.readString();
        message = source.readString();
        paymentstatus = source.readString();
        url = source.readString();
        data = (DepositDataParser) source.readParcelable(DepositDataParser.class.getClassLoader());
    }

    public static final Creator<DepositStatusParser> CREATOR = new Creator<DepositStatusParser>() {

        @Override
        public DepositStatusParser createFromParcel(Parcel source) {
            return new DepositStatusParser(source);
        }

        @Override
        public DepositStatusParser[] newArray(int size) {
            return new DepositStatusParser[size];
        }
    };


    public DepositDataParser getData() {
        return data;
    }

    public void setData(DepositDataParser data) {
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

    public String getPaymentstatus() {
        return paymentstatus;
    }

    public void setPaymentstatus(String paymentstatus) {
        this.paymentstatus = paymentstatus;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}