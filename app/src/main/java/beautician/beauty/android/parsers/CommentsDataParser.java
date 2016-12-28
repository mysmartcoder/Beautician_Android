package beautician.beauty.android.parsers;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class CommentsDataParser implements Parcelable {

    String comment_id = "";
    String userrating_comment = "";
    String user_image = "";
    String username = "";
    String totalcomment = "";
    String userrating_answer = "";
    String userrating_created = "";
    String userrating_id = "";
    String userrating_userid = "";
    String userrating_userby = "";
    List<CategoryDataParser> catarr;


    public CommentsDataParser() {

    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getUserrating_comment() {
        return userrating_comment;
    }

    public void setUserrating_comment(String userrating_comment) {
        this.userrating_comment = userrating_comment;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTotalcomment() {
        return totalcomment;
    }

    public void setTotalcomment(String totalcomment) {
        this.totalcomment = totalcomment;
    }

    public String getUserrating_answer() {
        return userrating_answer;
    }

    public void setUserrating_answer(String userrating_answer) {
        this.userrating_answer = userrating_answer;
    }

    public String getUserrating_created() {
        return userrating_created;
    }

    public void setUserrating_created(String userrating_created) {
        this.userrating_created = userrating_created;
    }

    public List<CategoryDataParser> getCatarr() {
        return catarr;
    }

    public void setCatarr(List<CategoryDataParser> catarr) {
        this.catarr = catarr;
    }





    public CommentsDataParser(Parcel source) {
        readFromParcel(source);
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int arg1) {
        dest.writeString(comment_id);
        dest.writeString(userrating_comment);
        dest.writeString(user_image);
        dest.writeString(username);
        dest.writeString(totalcomment);
        dest.writeString(userrating_answer);
        dest.writeString(userrating_created);
        dest.writeString(userrating_id);
        dest.writeString(userrating_userid);
        dest.writeString(userrating_userby);
        dest.writeList(catarr);

    }

    public void readFromParcel(Parcel source) {
        comment_id = source.readString();
        userrating_comment = source.readString();
        user_image = source.readString();
        username = source.readString();
        totalcomment = source.readString();
        userrating_answer = source.readString();
        userrating_created = source.readString();
        userrating_id = source.readString();
        userrating_userid = source.readString();
        userrating_userby = source.readString();
        catarr = new ArrayList<CategoryDataParser>();
        source.readList(catarr, null);

    }

    public final Creator<CommentsDataParser> CREATOR = new Creator<CommentsDataParser>() {

        @Override
        public CommentsDataParser createFromParcel(Parcel source) {
            return new CommentsDataParser(source);
        }

        @Override
        public CommentsDataParser[] newArray(int size) {
            return new CommentsDataParser[size];
        }
    };


    public String getUserrating_id() {
        return userrating_id;
    }

    public void setUserrating_id(String userrating_id) {
        this.userrating_id = userrating_id;
    }

    public String getUserrating_userid() {
        return userrating_userid;
    }

    public void setUserrating_userid(String userrating_userid) {
        this.userrating_userid = userrating_userid;
    }

    public String getUserrating_userby() {
        return userrating_userby;
    }

    public void setUserrating_userby(String userrating_userby) {
        this.userrating_userby = userrating_userby;
    }
}