package beautician.beauty.android.parsers;

import android.os.Parcel;
import android.os.Parcelable;

public class ScheduleDataParser implements Parcelable {

    String day = "";
    String start_time = "";
    String end_time = "";

    public ScheduleDataParser() {

    }


    public ScheduleDataParser(Parcel source) {
        readFromParcel(source);
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int arg1) {
        dest.writeString(day);
        dest.writeString(start_time);
        dest.writeString(end_time);
    }

    public void readFromParcel(Parcel source) {
        day = source.readString();
        start_time = source.readString();
        end_time = source.readString();
    }

    public final Creator<ScheduleDataParser> CREATOR = new Creator<ScheduleDataParser>() {

        @Override
        public ScheduleDataParser createFromParcel(Parcel source) {
            return new ScheduleDataParser(source);
        }

        @Override
        public ScheduleDataParser[] newArray(int size) {
            return new ScheduleDataParser[size];
        }
    };


    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }
}