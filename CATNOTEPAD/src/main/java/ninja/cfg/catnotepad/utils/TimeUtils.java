package ninja.cfg.catnotepad.utils;

import java.util.Date;
import org.ocpsoft.prettytime.PrettyTime;

/**
 * Created by MohMah on 8/21/2016. Modified 6/15/2020
 */
public class TimeUtils{
	public static final PrettyTime prettyTime;

	static{
		prettyTime = new PrettyTime();
	}

	public static String getHumanReadableTimeDiff(Date lastTime){
		if (lastTime == null) return "";
		prettyTime.setReference(new Date());
		return prettyTime.format(lastTime);
	}
}
