package com.nfs.youlin.utils;

import com.nfs.youlin.R;

public class ChooseWeaherTB {
	// 天气图标判断
	public int choosetb(String smg) {
		int res = R.drawable.sun;
		if (Sun(smg)) {
			if (Could(smg)) {
				res = R.drawable.cloud_sun;
				return res;
			} else if (Rain(smg)) {
				if (smg.indexOf("小雨") != -1) {
					res = R.drawable.cloud_sun_drizzle;
					return res;
				} else if (smg.indexOf("阵雨") != -1) {
					res = R.drawable.cloud_sun_lightning;
					return res;
				} else {
					res = R.drawable.cloud_sun_rain;
					return res;
				}
			} else if (Snow(smg)) {
				res = R.drawable.cloud_sun_snow;
				return res;
			} else if (Fog(smg)) {
				res = R.drawable.cloud_fog;
				return res;
			} else if (Hail(smg)) {
				res = R.drawable.cloud_hail;
				return res;
			} else {
				res = R.drawable.sun;
				return res;
			}
		}
		if (Could(smg)) {
			res = R.drawable.cloud;
			return res;
		}
		if (Rain(smg)) {
			if (smg.indexOf("小雨") != -1) {
				res = R.drawable.cloud_drizzle;
				return res;
			} else if (smg.indexOf("阵雨") != -1) {
				res = R.drawable.cloud_lightning;
				return res;
			} else {
				res = R.drawable.cloud_rain;
				return res;
			}
		}
		if (smg.indexOf("阴") != -1) {
			res = R.drawable.cloud;
			return res;
		}
		if (Fog(smg)) {
			res = R.drawable.cloud_fog;
			return res;
		}
		if (light(smg)) {
			res = R.drawable.cloud_lightning;
			return res;
		}
		if (Snow(smg) || Hail(smg)) {
			res = R.drawable.cloud_snow;
			return res;
		}
		return res;
	}

	// 星座图标判断
	public String choosexz(String smg) {
		//int res = R.drawable.tianxiezuo;
		String res = "天蝎座";
		if (smg.indexOf("白羊座") != -1) {
			//res = R.drawable.baiyangzuo;
			res = "白羊座";
		} else if (smg.indexOf("处女座") != -1) {
			//res = R.drawable.chunvzuo;
			res = "处女座";
		} else if (smg.indexOf("金牛座") != -1) {
			//res = R.drawable.jinniuzuo;
			res = "金牛座";
		} else if (smg.indexOf("巨蟹座") != -1) {
			//res = R.drawable.juziezuo;
			res = "巨蟹座";
		} else if (smg.indexOf("摩羯座") != -1) {
			//res = R.drawable.mojiezuo;
			res = "摩羯座";
		} else if (smg.indexOf("射手座") != -1) {
			//res = R.drawable.sheshouzuo;
			res = "射手座";
		} else if (smg.indexOf("狮子座") != -1) {
			//res = R.drawable.shizizuo;
			res = "狮子座";
		} else if (smg.indexOf("双鱼座") != -1) {
			//res = R.drawable.shuangyuzuo;
			res = "双鱼座";
		} else if (smg.indexOf("双子座") != -1) {
			//res = R.drawable.shuangzizuo;
			res = "双子座";
		} else if (smg.indexOf("水瓶座") != -1) {
			//res = R.drawable.shuipingzuo;
			res = "水瓶座";
		} else if (smg.indexOf("天秤座") != -1) {
			//res = R.drawable.tianchengzuo;
			res = "天秤座";
		} else if (smg.indexOf("天蝎座") != -1) {
			//res = R.drawable.tianxiezuo;
			res = "天蝎座";
		}
		return res;

	}

	// 雨
	public Boolean Rain(String smg) {

		return smg.indexOf("雨") != -1;

	}

	// 雪
	public Boolean Snow(String smg) {

		return smg.indexOf("雪") != -1;

	}

	// 雾
	public Boolean Fog(String smg) {

		return smg.indexOf("雾") != -1;

	}

	// 云
	public Boolean Could(String smg) {

		return smg.indexOf("云") != -1;

	}

	// 冰雹
	public Boolean Hail(String smg) {

		return smg.indexOf("冰雹") != -1;

	}

	// 雷
	public Boolean light(String smg) {

		return smg.indexOf("雷") != -1;

	}

	// //风
	// public Boolean Wind(String smg){
	//
	// return smg.indexOf("风")!=-1;
	//
	// }
	// 晴
	public Boolean Sun(String smg) {

		return smg.indexOf("晴") != -1;

	}

	// 阴
	public Boolean Yin(String smg) {

		return smg.indexOf("阴") != -1;

	}

}
