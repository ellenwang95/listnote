package listnote.jchronic;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.mdimension.jchronic.Chronic;
import com.mdimension.jchronic.Options;
import com.mdimension.jchronic.utils.Span;

public class DateParser {

	public static final Calendar now = Calendar.getInstance();

	  public static Span parse_now(String string) {
	    return parse_now(string, new Options());
	  }

	  public static Span parse_now(String string, Options options) {
	    options.setNow(now);
	    options.setCompatibilityMode(true);
	    return Chronic.parse(string, options);
	  }
	  
	  public static void main(String[] args) throws ParseException {
			
			Span time;
		    time = parse_now("hello this happened on june 27 something interesting yay");
		    
		    System.out.println(time);
		    	    
//		    Date d = new SimpleDateFormat("dd-MMM-yyyy").parse(date);
		    
//		    Date d = new SimpleDateFormat("dd/MM/yyyy").parse(date);
//		    Timestamp timestamp = new Timestamp(d.getTime());
//		    
//		    System.out.println(timestamp);
		}
}
