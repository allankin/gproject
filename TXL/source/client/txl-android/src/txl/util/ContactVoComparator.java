package txl.util;

import java.text.Collator;
import java.util.Comparator;

import txl.contact.po.ContactVo;

public class ContactVoComparator  implements Comparator  {

	/*public int compare(ContactVo o1, ContactVo o2) {
		Collator myCollator = Collator.getInstance(java.util.Locale.CHINA);
		return myCollator.compare(o1.displayName, o2.displayName);
		if (myCollator.compare(o1, o2) < 0)
			return -1;

		else if (myCollator.compare(o1, o2) > 0)

			return 1;
		else

			return 0;

	}
*/
	@Override
	public int compare(Object o1, Object o2) {
		ContactVo cv1 =(ContactVo)o1;
		ContactVo cv2 =(ContactVo)o2;
		Collator myCollator = Collator.getInstance(java.util.Locale.CHINA);
		return myCollator.compare(cv1.displayName, cv2.displayName);
	}

}