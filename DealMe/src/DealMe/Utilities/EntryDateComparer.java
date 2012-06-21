package DealMe.Utilities;

import java.util.Comparator;
import DealMe.Model.RSSItem;

public class EntryDateComparer implements Comparator<RSSItem> {

	public int compare(RSSItem arg0, RSSItem arg1) {
		// TODO Auto-generated method stub
		return arg0.getPubDate().compareTo(arg1.getPubDate())*-1;
	}

}
