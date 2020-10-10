package de.potera.klysma.kits;

public class TimeUtils {

	private static long DIFF_ONE_MINUTE = 60;
	private static long DIFF_ONE_HOUR = DIFF_ONE_MINUTE * 60;
	private static long DIFF_ONE_DAY = DIFF_ONE_HOUR * 24;

	public static String getTime(long cooldown){
		if(cooldown == Integer.MAX_VALUE)
			if(cooldown < DIFF_ONE_MINUTE){
				long seks = cooldown;
				return seks == 1 ? "eine Sekunde" : seks + " Sekunden";
			}
		if(cooldown < DIFF_ONE_HOUR){
			long minutes = (cooldown / DIFF_ONE_MINUTE);
			return minutes == 1 ? "eine Minute" : minutes + " Minuten";
		}
		if(cooldown < DIFF_ONE_DAY){
			long hours = (cooldown / DIFF_ONE_HOUR);
			return hours == 1 ? "eine Stunde" : hours + " Stunden";
		}
		long days = (cooldown / DIFF_ONE_DAY);
		return days == 1 ? "ein Tag" : days + " Tage";
	}

	public static String getTimeWithN(long cooldown){
		if(cooldown < DIFF_ONE_MINUTE){
			long seks = cooldown;
			return seks == 1 ? "einer Sekunde" : seks + " Sekunden";
		}
		if(cooldown < DIFF_ONE_HOUR){
			long minutes = (cooldown / DIFF_ONE_MINUTE);
			return minutes == 1 ? "einer Minute" : minutes + " Minuten";
		}
		if(cooldown < DIFF_ONE_DAY){
			long hours = (cooldown / DIFF_ONE_HOUR);
			return hours == 1 ? "einer Stunde" : hours + " Stunden";
		}
		long days = (cooldown / DIFF_ONE_DAY);
		return days == 1 ? "einem Tag" : days + " Tagen";
	}

	public static long parseCooldown(String str){
		str = str.toLowerCase();
		long cooldown = -1;
		long multiplier = 1;

		if(str.endsWith("s")){
			str = str.replaceAll("s", "");
			multiplier = 1;
		}

		if(str.endsWith("m")){
			str = str.replaceAll("m", "");
			multiplier = DIFF_ONE_MINUTE;
		}

		if(str.endsWith("h")){
			str = str.replaceAll("h", "");
			multiplier = DIFF_ONE_HOUR;
		}

		if(str.endsWith("d")){
			str = str.replaceAll("d", "");
			multiplier = DIFF_ONE_DAY;
		}

		try{

			cooldown = Long.parseLong(str);
			cooldown *= multiplier;

		}catch(Exception e){}
		return cooldown;
	}

}
