package beautician.beauty.android.helper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import beautician.beauty.android.R;


public class GridCellAdapter extends BaseAdapter implements OnClickListener
{
    private final Context _context;

    private final List<String> list;
    private static final int DAY_OFFSET = 1;
    private final String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private int daysInMonth;
    private int currentDayOfMonth;
    private int currentWeekDay;
//    private Button gridcell;
    TextView gridcell;
    private final HashMap<String, Integer> eventsPerMonthMap;

    // Days in Current Month
    public GridCellAdapter(Context context, int textViewResourceId, int month, int year){
            super();
            this._context = context;
            this.list = new ArrayList<String>();
            Calendar calendar = Calendar.getInstance();
            setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
            setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));

            // Print Month
            printMonth(month, year);

            // Find Number of Events
            eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
        }
    private String getMonthAsString(int i){
            return months[i];
        }

    private int getNumberOfDaysOfMonth(int i){
            return daysOfMonth[i];
        }

    public String getItem(int position){
            return list.get(position);
        }

    @Override
    public int getCount(){
            return list.size();
        }

    private void printMonth(int mm, int yy){
            int trailingSpaces = 0;
            int daysInPrevMonth = 0;
            int prevMonth = 0;
            int prevYear = 0;
            int nextMonth = 0;
            int nextYear = 0;

            int currentMonth = mm - 1;
            daysInMonth = getNumberOfDaysOfMonth(currentMonth);


            // Gregorian Calendar : MINUS 1, set to FIRST OF MONTH
            GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);

            if (currentMonth == 11){
                    prevMonth = currentMonth - 1;
                    daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
                    nextMonth = 0;
                    prevYear = yy;
                    nextYear = yy + 1;
                }
            else if (currentMonth == 0){
                    prevMonth = 11;
                    prevYear = yy - 1;
                    nextYear = yy;
                    daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
                    nextMonth = 1;
                }
            else{
                    prevMonth = currentMonth - 1;
                    nextMonth = currentMonth + 1;
                    nextYear = yy;
                    prevYear = yy;
                    daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
                }

            int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
            trailingSpaces = currentWeekDay;

            if (cal.isLeapYear(cal.get(Calendar.YEAR)) && mm == 1){
                    ++daysInMonth;
                }

            // Trailing Month days
            for (int i = 0; i < trailingSpaces; i++){
                    list.add(String.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET) + i) + "-GREY" + "-" + getMonthAsString(prevMonth) + "-" + prevYear);
                }

            int cMonth = Calendar.getInstance().get(Calendar.MONTH)+1;
            System.out.println(" c month..."+mm);
            
            // Current Month Days
            for (int i = 1; i <= daysInMonth; i++){
                    if (i == getCurrentDayOfMonth() && mm==cMonth)
                            list.add(String.valueOf(i) + "-BLUE" + "-" + getMonthAsString(currentMonth) + "-" + yy);
                    else
                            list.add(String.valueOf(i) + "-WHITE" + "-" + getMonthAsString(currentMonth) + "-" + yy);
                }

            // Leading Month days
            for (int i = 0; i < list.size() % 7; i++){
                    list.add(String.valueOf(i + 1) + "-GREY" + "-" + getMonthAsString(nextMonth) + "-" + nextYear);
                }
        }

    private HashMap<String, Integer> findNumberOfEventsPerMonth(int year, int month){
            HashMap<String, Integer> map = new HashMap<String, Integer>();
            return map;
        }

    @Override
    public long getItemId(int position){
            return position;
        }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
            View row = convertView;
            if (row == null){
                    LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    row = inflater.inflate(R.layout.row_calendar, parent, false);
                }

            // Get a reference to the Day gridcell
//            gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);
//            gridcell.setOnClickListener(this);
            
            //row.setBackgroundResource(R.drawable.white_square);
            gridcell = (TextView)row.findViewById(R.id.date);

            // ACCOUNT FOR SPACING

            String[] day_color = list.get(position).split("-");
            String theday = day_color[0];
            String themonth = day_color[2];
            String theyear = day_color[3];
           

            // Set the Day GridCell
            gridcell.setText(theday);
            gridcell.setTag(theday + "-" + themonth + "-" + theyear);

            if (day_color[1].equals("GREY"))
            {
            	gridcell.setBackgroundResource(R.color.white);
            	gridcell.setTextColor(Color.LTGRAY);
            	gridcell.setTypeface(gridcell.getTypeface(), Typeface.NORMAL);
            }
            if (day_color[1].equals("WHITE"))
            {
            	gridcell.setBackgroundResource(R.color.white);
            	gridcell.setTextColor(ContextCompat.getColor(_context,R.color.myPrimaryColor));
            	gridcell.setTypeface(gridcell.getTypeface(), Typeface.NORMAL);	
            }
            if (day_color[1].equals("BLUE"))
            {
            	gridcell.setBackgroundResource(R.drawable.circle_current_date);
            	gridcell.setTextColor(Color.WHITE);
            	gridcell.setTypeface(gridcell.getTypeface(), Typeface.BOLD);
            }
                
            
			if ((!eventsPerMonthMap.isEmpty()) && (eventsPerMonthMap != null)) {
				if (eventsPerMonthMap.containsKey(theday)) {
					// num_events_per_day = (TextView)
					// row.findViewById(R.id.num_events_per_day);
					// Integer numEvents = (Integer) eventsPerMonthMap.get(theday);
					// num_events_per_day.setText(numEvents.toString());
	
					gridcell.setBackgroundResource(R.color.white_secondary);
				}
			}
            
            return row;
        }
    @Override
    public void onClick(View view){
//            date_month_year = (String) view.getTag();
//            flag ="Date selected ...";
//            selectedDayMonthYearButton.setText("Selected: " + date_month_year);
        }

    public int getCurrentDayOfMonth(){
            return currentDayOfMonth;
        }

    private void setCurrentDayOfMonth(int currentDayOfMonth){
            this.currentDayOfMonth = currentDayOfMonth;
        }
    public void setCurrentWeekDay(int currentWeekDay){
            this.currentWeekDay = currentWeekDay;
        }
    public int getCurrentWeekDay(){
            return currentWeekDay;
        }
}