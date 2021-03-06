package cn.com.zlqf.airservice.utils;

import com.alibaba.fastjson.JSONObject;

public class SQLUtils {

	public static String createSQL(JSONObject jsonObject) {
		String incomingFlyNo = jsonObject.getString("incomingFlyNo");
		String departureFlyNo = jsonObject.getString("departureFlyNo");
		String planeType = jsonObject.getString("planeType");
		String task = jsonObject.getString("task");
		String incomingProg = jsonObject.getString("incomingProg");
		String departureProg = jsonObject.getString("departureProg");
		Long estimatedArrival = jsonObject.getLong("estimatedArrival");
		Long planedFly = jsonObject.getLong("planedFly");
		Long preRealFly = jsonObject.getLong("preRealFly");
		Long realFly = jsonObject.getLong("realFly");
		Long realArrival = jsonObject.getLong("realArrival");
		String alternate = jsonObject.getString("alternate");

		if (incomingFlyNo == null && departureFlyNo == null) {
			return null;
		}
		int flag = 0;
		StringBuilder sb = new StringBuilder("");
		if (planeType != null) {
			if (flag == 0) {
				sb.append("update t_fly_info set planetype='" + planeType + "'");
				flag++;
			}
		}
		if (task != null) {
			if (flag == 0) {
				sb.append("update t_fly_info set task='" + task + "'");
				flag++;
			} else {
				sb.append(",task='" + task + "'");
			}

		}
		if (incomingProg != null) {
			if (flag == 0) {
				sb.append("update t_fly_info set incomingprog='" + incomingProg + "'");
				flag++;
			} else {
				sb.append(",incomingprog='" + incomingProg + "'");
			}

		}
		if (departureProg != null) {
			if (flag == 0) {
				sb.append("update t_fly_info set departureprog='" + departureProg + "'");
				flag++;
			} else {
				sb.append(",departureprog='" + departureProg + "'");
			}

		}
		if (estimatedArrival != null) {
			if (flag == 0) {
				sb.append("update t_fly_info set estimatedarrival=" + estimatedArrival);
				flag++;
			} else {
				sb.append(",estimatedarrival=" + estimatedArrival);
			}

		}
		if (planedFly != null) {
			if (flag == 0) {
				sb.append("update t_fly_info set planedfly=" + planedFly);
				flag++;
			} else {
				sb.append(",planedfly=" + planedFly);
			}

		}
		if (preRealFly != null) {
			if (flag == 0) {
				sb.append("update t_fly_info set prerealfly=" + preRealFly);
				flag++;
			} else {
				sb.append(",prerealfly=" + preRealFly);
			}

		}
		if (realFly != null) {
			if (flag == 0) {
				sb.append("update t_fly_info set realfly=" + realFly);
				flag++;
			} else {
				sb.append(",realfly=" + realFly);
			}

		}
		if (realArrival != null) {
			if (flag == 0) {
				sb.append("update t_fly_info set realarrival=" + realArrival);
				flag++;
			} else {
				sb.append(",realarrival=" + realArrival);
			}

		}
		if (alternate != null) {
			if (flag == 0) {
				sb.append("update t_fly_info set alternate='" + alternate + "'");
				flag++;
			} else {
				sb.append(",alternate='" + alternate + "'");
			}

		}
		if(sb.length()==0) {
			return null;
		}
		if (incomingFlyNo != null) {
			sb.append(" where incomingflyno='" + incomingFlyNo + "'");

		}
		if (departureFlyNo != null) {
			sb.append(" where departureflyno='" + departureFlyNo + "'");
		}
		//sb.append(" and publishtime='" + time + "'");
		return sb.toString();
	}

	public static String createSQLByFlyInfoId(JSONObject jsonObject, String id) {
		//String incomingFlyNo = jsonObject.getString("incomingFlyNo");
		//String departureFlyNo = jsonObject.getString("departureFlyNo");
		String planeType = jsonObject.getString("planeType");
		String task = jsonObject.getString("task");
		String incomingProg = jsonObject.getString("incomingProg");
		String departureProg = jsonObject.getString("departureProg");
		Long estimatedArrival = jsonObject.getLong("estimatedArrival");
		Long planedFly = jsonObject.getLong("planedFly");
		Long preRealFly = jsonObject.getLong("preRealFly");
		Long realFly = jsonObject.getLong("realFly");
		Long realArrival = jsonObject.getLong("realArrival");
		String alternate = jsonObject.getString("alternate");

		/*if (incomingFlyNo == null && departureFlyNo == null) {
			return null;
		}*/
		int flag = 0;
		StringBuilder sb = new StringBuilder("");
		if (planeType != null) {
			if (flag == 0) {
				sb.append("update t_fly_info set planetype='" + planeType + "'");
				flag++;
			}
		}
		if (task != null) {
			if (flag == 0) {
				sb.append("update t_fly_info set task='" + task + "'");
				flag++;
			} else {
				sb.append(",task='" + task + "'");
			}

		}
		if (incomingProg != null) {
			if (flag == 0) {
				sb.append("update t_fly_info set incomingprog='" + incomingProg + "'");
				flag++;
			} else {
				sb.append(",incomingprog='" + incomingProg + "'");
			}

		}
		if (departureProg != null) {
			if (flag == 0) {
				sb.append("update t_fly_info set departureprog='" + departureProg + "'");
				flag++;
			} else {
				sb.append(",departureprog='" + departureProg + "'");
			}

		}
		if (estimatedArrival != null) {
			if (flag == 0) {
				sb.append("update t_fly_info set estimatedarrival=" + estimatedArrival);
				flag++;
			} else {
				sb.append(",estimatedarrival=" + estimatedArrival);
			}

		}
		if (planedFly != null) {
			if (flag == 0) {
				sb.append("update t_fly_info set planedfly=" + planedFly);
				flag++;
			} else {
				sb.append(",planedfly=" + planedFly);
			}

		}
		if (preRealFly != null) {
			if (flag == 0) {
				sb.append("update t_fly_info set prerealfly=" + preRealFly);
				flag++;
			} else {
				sb.append(",prerealfly=" + preRealFly);
			}

		}
		if (realFly != null) {
			if (flag == 0) {
				sb.append("update t_fly_info set realfly=" + realFly);
				flag++;
			} else {
				sb.append(",realfly=" + realFly);
			}

		}
		if (realArrival != null) {
			if (flag == 0) {
				sb.append("update t_fly_info set realarrival=" + realArrival);
				flag++;
			} else {
				sb.append(",realarrival=" + realArrival);
			}

		}
		if (alternate != null) {
			if (flag == 0) {
				sb.append("update t_fly_info set alternate='" + alternate + "'");
				flag++;
			} else {
				sb.append(",alternate='" + alternate + "'");
			}

		}
		/*
		if (incomingFlyNo != null) {
			sb.append(" where incomingflyno='" + incomingFlyNo + "'");

		}
		if (departureFlyNo != null) {
			sb.append(" where departureflyno='" + departureFlyNo + "'");
		}
		*/
		if(sb.length()==0) {
			return null;
		}
		sb.append(" where id='" + id + "'");
		return sb.toString();
	}

}
