package beautician.beauty.android.parsers;

import android.os.Parcel;
import android.os.Parcelable;

public class DepositDataParser implements Parcelable {

    String sadad = "";
    String creditcard = "";
    String paypal = "";
    String totalcredit = "";
    String depositamt = "";
    String featureads_id = "";

    public DepositDataParser() {

    }



    public DepositDataParser(Parcel source) {
        readFromParcel(source);
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int arg1) {
        dest.writeString(totalcredit);
        dest.writeString(sadad);
        dest.writeString(creditcard);
        dest.writeString(paypal);
        dest.writeString(depositamt);
        dest.writeString(featureads_id);
    }

    public void readFromParcel(Parcel source) {
        totalcredit = source.readString();
        sadad = source.readString();
        creditcard = source.readString();
        paypal = source.readString();
        depositamt = source.readString();
        featureads_id = source.readString();
    }

    public final Creator<DepositDataParser> CREATOR = new Creator<DepositDataParser>() {

        @Override
        public DepositDataParser createFromParcel(Parcel source) {
            return new DepositDataParser(source);
        }

        @Override
        public DepositDataParser[] newArray(int size) {
            return new DepositDataParser[size];
        }
    };


    public String getSadad() {
        return sadad;
    }

    public void setSadad(String sadad) {
        this.sadad = sadad;
    }

    public String getCreditcard() {
        return creditcard;
    }

    public void setCreditcard(String creditcard) {
        this.creditcard = creditcard;
    }

    public String getPaypal() {
        return paypal;
    }

    public void setPaypal(String paypal) {
        this.paypal = paypal;
    }

    public String getTotalcredit() {
        return totalcredit;
    }

    public void setTotalcredit(String totalcredit) {
        this.totalcredit = totalcredit;
    }

    public String getDepositamt() {
        return depositamt;
    }

    public void setDepositamt(String depositamt) {
        this.depositamt = depositamt;
    }

    public String getFeatureads_id() {
        return featureads_id;
    }

    public void setFeatureads_id(String featureads_id) {
        this.featureads_id = featureads_id;
    }
}