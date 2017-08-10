package cn.com.zlqf.airservice.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

import cn.com.zlqf.airservice.entity.Message;

public class MessageUtils {
	private static Map<String, String> addressMap = BaseUtils.getAddressMap();
	private static Map<String, String> taskMap = BaseUtils.getTaskMap();
	private static Map<String,String> flyNoMap = BaseUtils.getFlyNoMap();
	private static Map<String,Long> flyTimeMap = new HashMap<>();
	// 解析报文
	public static String parse(String msg) {
		msg = msg.replace("\r\n", "");
		msg = msg.replace("\n", "");
		int startIndex = msg.indexOf("(");
		int endIndex = msg.indexOf(")");

		msg = msg.substring(startIndex + 1, endIndex);

		String[] split = msg.split("-");
		// 报文类型
		String msgType = split[0];
		System.out.println("报文类型：" + msgType);

		switch (msgType) {
		case "FPL":
			return parseFPL(split);
		case "CHG":
			return parseCHG(split);
		case "CNL":
			return parseCNL(split);
		case "DEP":
			return parseDEP(split);
		case "ARR":
			return parseARR(split);
		case "DLA":
			return parseDLA(split);
		case "CPL":
			return parseCPL(split);
		case "EST":
			return parseEST(split);
		case "CDN":
			return parseCDN(split);
		case "ACP":
			return parseACP(split);
		case "RQP":
			return parseRQP(split);
		case "RQS":
			return parseRQS(split);
		case "SPL":
			return parseSPL(split);
		case "ALR":
			return parseALR(split);
		default:
			return null;
		}
	}

	// 领航计划报
	private static String parseFPL(String[] split) {
		// 解析编组18 其它情报
		Map<String, String> parseG18 = parseG18(split[8]);

		// 解析编组13 起飞机场和时间
		String[] parseG13 = parseG13(split[5]);
		String departureAirport = parseG13[0];
		String departureAirportTime = parseG13[1];// 可能为""

		// 如果编组13携带信息无效 从编组18中查询
		if (departureAirport == null || "未知".equals(departureAirport)) {
			departureAirport = parseG18.get("DEP");
		}

		// 解析编组16 目的机场和预计飞行总时间 备降站
		Map<String, String> parseG16 = parseG16(split[7]);
		String arrivalAirport = parseG16.get("arrivalAirport");
		if ("未知".equals(arrivalAirport) || arrivalAirport == null) {
			arrivalAirport = parseG18.get("DEST");
		}
		// 预计飞行时间
		String flyTime = parseG16.get("flyTime");

		// 备降站1
		String alternate1 = parseG16.get("alternate1");
		if (alternate1 == null || "未知".equals(alternate1)) {
			alternate1 = parseG18.get("ALTN");
		}
		// 备降站2
		String alternate2 = parseG16.get("alternate2");
		if (alternate2 == null || "未知".equals(alternate2)) {
			alternate2 = parseG18.get("ALTN");
		}

		/** 共有信息 任务 机型 航线 **/
		String task = parseG8(split[2]);
		String planeType = parseG9(split[3]);
		if (planeType.equals("ZZZZ")) {
			planeType = parseG18.get("TYP");
		}
		String flightLine = departureAirport + "-" + arrivalAirport;
		/** 以下信息需要根据 起飞机场 目的机场的不同具体判断 **/
		String incomingFlyNo = null;
		String departureFlyNo = null;
		/** 如果该条报文是无关报文 则只确定航班号 不需要确定是进港 还是出港 **/
		String flyNo = null;
		// 预计到达时间
		Long estimatedArrival = null;
		// 本站计划起飞时间
		Long planedFly = null;
		// 前站计划起飞时间
		Long prePlanedFly = null;
		// 备降站
		String alternate = null;
		// 1.出发地是长治
		if ("长治".equals(departureAirport)) {
			departureFlyNo = parseG7(split[1]);// 出发地是长治 则航班号代表出港航班号

			// 计算planedFly
			if (StringUtils.isNotBlank(departureAirportTime)) {
				// 需要判断其它信息里是否有DOF这一项的内容 如果有 则时间以DOF为准 如果没有 则时间为当天
				String dof = parseG18.get("DOF");
				if (dof != null) {
					planedFly = DateUtils.string2FormattingLongByDOF(dof, departureAirportTime);
				} else {
					planedFly = DateUtils.string2FormattingLong(departureAirportTime);
				}
			}
			// 拼接备降站
			alternate = jointAlternate(alternate1, alternate2);

		} else if ("长治".equals(arrivalAirport)) {
			incomingFlyNo = parseG7(split[1]);// 目的地是长治 则航班号代表进港航班号
			// 计算prePlanedFly
			if (StringUtils.isNotBlank(departureAirportTime)) {
				// 需要判断其它信息里是否有DOF这一项的内容 如果有 则时间以DOF为准 如果没有 则时间为当天
				String dof = parseG18.get("DOF");
				if (dof != null) {
					prePlanedFly = DateUtils.string2LongByDOF(dof, departureAirportTime);
				} else {
					prePlanedFly = DateUtils.string2Long(departureAirportTime);
				}
			}
			if(flyTime!=null) {
				Long flyTimeLong = DateUtils.getFlyTime(flyTime);
				flyTimeMap.put(incomingFlyNo, flyTimeLong);
			}

			// 拼接备降站
			alternate = jointAlternate(alternate1, alternate2);
		} else if (!"长治".equals(departureAirport) && !"长治".equals(arrivalAirport) && !"长治".equals(alternate1)
				&& !"长治".equals(alternate2)) {
			// 与长治无关的报文
			flyNo = parseG7(split[1]);
			alternate = jointAlternate(alternate1, alternate2);
		} else {// 备降站是长治
			incomingFlyNo = split[1];// 备降站是长治 则航班号代表进港航班号
			alternate = "长治";
		}
		// 将解析的数据封装成JSON
		JSONObject json = new JSONObject();
		json.put("messageType", "FPL");
		json.put("task", task);
		if (planeType != null) {
			json.put("planeType", planeType);
		}
		if (flightLine != null) {
			json.put("flightLine", flightLine);
		}
		if (incomingFlyNo != null) {
			json.put("incomingFlyNo", incomingFlyNo);
		}
		if (departureFlyNo != null) {
			json.put("departureFlyNo", departureFlyNo);
		}
		/*
		if (estimatedArrival != null) {
			json.put("estimatedArrival", estimatedArrival);
		}
		*/
		if (planedFly != null) {
			json.put("planedFly", planedFly);
		}
		if (alternate != null) {
			json.put("alternate", alternate);
		}
		if (flyNo != null) {
			json.put("flyNo", flyNo);
		}
		return json.toJSONString();
	}

	// 修订领航计划报
	private static String parseCHG(String[] split) {
		// 解析编组22 修订情报
		Map<String, String> parseG22 = parseG22(split);
		// 解析编组18 其它情报
		Map<String, String> parseG18 = parseG18(split[4]);

		// 解析编组13 起飞机场和时间
		String[] parseG13 = parseG13(split[2]);
		String departureAirport = parseG13[0];
		String departureAirportTime = parseG13[1];// 可能为""

		// 如果编组13携带信息无效 从编组18中查询
		if (departureAirport == null || "未知".equals(departureAirport)) {
			departureAirport = parseG18.get("DEP");
			if (parseG22.get("DEP") != null) {
				departureAirport = parseG22.get("DEP");
			}
		}
		// 解析编组16 目的机场和预计飞行总时间 备降站
		Map<String, String> parseG16 = parseG16(split[3]);
		String arrivalAirport = parseG16.get("arrivalAirport");
		if (parseG22.get("arrivalAirport") != null) {
			arrivalAirport = parseG22.get("arrivalAirport");
		}
		if ("未知".equals(arrivalAirport) || arrivalAirport == null) {
			arrivalAirport = parseG18.get("DEST");
			if (parseG22.get("DEST") != null) {
				arrivalAirport = parseG22.get("DEST");
			}
		}
		// 预计飞行时间
		String flyTime = parseG16.get("flyTime");
		if (parseG22.get("flyTime") != null) {
			flyTime = parseG22.get("flyTime");
		}

		// 备降站1
		String alternate1 = parseG16.get("alternate1");
		if (parseG22.get("alternate1") != null) {
			alternate1 = parseG22.get("alternate1");
		}
		if (alternate1 == null || "未知".equals(alternate1)) {
			alternate1 = parseG18.get("ALTN");
			if (parseG22.get("ALTN") != null) {
				alternate1 = parseG22.get("ALTN");
			}
		}
		// 备降站2
		String alternate2 = parseG16.get("alternate2");
		if (parseG22.get("alternate2") != null) {
			alternate2 = parseG22.get("alternate2");
		}
		if (alternate2 == null || "未知".equals(alternate2)) {
			alternate2 = parseG18.get("ALTN");
			if (parseG22.get("ALTN") != null) {
				alternate2 = parseG22.get("ALTN");
			}
		}
		String task = parseG22.get("task");
		String planeType = parseG22.get("planeType");
		if(parseG22.get("TYP")!=null) {
			planeType = parseG22.get("TYP");
		}
		String flightLine = departureAirport + "-" + arrivalAirport;
		/** 以下信息需要根据 起飞机场 目的机场的不同具体判断 **/
		String incomingFlyNo = null;
		String departureFlyNo = null;
		/** 如果该条报文是无关报文 则只确定航班号 不需要确定是进港 还是出港 **/
		String flyNo = null;
		// 预计到达时间
		Long estimatedArrival = null;
		// 本站计划起飞时间
		Long planedFly = null;
		// 前站计划起飞时间
		Long prePlanedFly = null;
		// 备降站
		String alternate = null;
		// 1.出发地是长治
		if ("长治".equals(departureAirport)) {
			departureFlyNo = parseG7(split[1]);// 出发地是长治 则航班号代表出港航班号
			// 计算planedFly
			if (StringUtils.isNotBlank(departureAirportTime)) {
				// 需要判断其它信息里是否有DOF这一项的内容 如果有 则时间以DOF为准 如果没有 则时间为当天
				String dof = parseG18.get("DOF");
				if (parseG22.get("DOF") != null) {
					dof = parseG22.get("DOF");
				}
				if (dof != null) {
					planedFly = DateUtils.string2FormattingLongByDOF(dof, departureAirportTime);
				} else {
					planedFly = DateUtils.string2FormattingLong(departureAirportTime);
				}
			}
			// 拼接备降站
			alternate = jointAlternate(alternate1, alternate2);

		} else if ("长治".equals(arrivalAirport)) {
			incomingFlyNo = parseG7(split[1]);// 目的地是长治 则航班号代表进港航班号
			// 计算prePlanedFly
			if (StringUtils.isNotBlank(departureAirportTime)) {
				// 需要判断其它信息里是否有DOF这一项的内容 如果有 则时间以DOF为准 如果没有 则时间为当天
				String dof = parseG18.get("DOF");
				if (parseG22.get("DOF") != null) {
					dof = parseG22.get("DOF");
				}
				if (dof != null) {
					prePlanedFly = DateUtils.string2LongByDOF(dof, departureAirportTime);
				} else {
					prePlanedFly = DateUtils.string2Long(departureAirportTime);
				}
			}
			/*
			if (prePlanedFly != null && flyTime != null) {
				Long flyTimeLong = DateUtils.getFlyTime(flyTime);// long类型
				// 预计达到时间为前站计划起飞时间+预计飞行时间
				estimatedArrival = prePlanedFly + flyTimeLong;// long类型
				// 将Long类型的转成long （yyyymmddHHmmss的形式）
				estimatedArrival = DateUtils.longTolong(estimatedArrival);
			}
			*/
			if(flyTime!=null) {
				Long flyTimeLong = DateUtils.getFlyTime(flyTime);
				flyTimeMap.put(incomingFlyNo, flyTimeLong);
			}

			// 拼接备降站
			alternate = jointAlternate(alternate1, alternate2);
		} else if (!"长治".equals(departureAirport) && !"长治".equals(arrivalAirport) && !"长治".equals(alternate1)
				&& !"长治".equals(alternate2)) {
			// 与长治无关的报文
			flyNo = parseG7(split[1]);
			alternate = jointAlternate(alternate1, alternate2);
		} else {// 备降站是长治
			incomingFlyNo = split[1];// 备降站是长治 则航班号代表进港航班号
			alternate = "长治";
		}

		// 将解析的数据封装成JSON
		JSONObject json = new JSONObject();
		json.put("messageType", "CHG");
		if (task != null) {
			json.put("task", task);
		}
		if (planeType != null) {
			json.put("planeType", planeType);
		}
		if (flightLine != null) {
			json.put("flightLine", flightLine);
		}
		if (incomingFlyNo != null) {
			json.put("incomingFlyNo", incomingFlyNo);
		}
		if (departureFlyNo != null) {
			json.put("departureFlyNo", departureFlyNo);
		}
		/*
		if (estimatedArrival != null) {
			json.put("estimatedArrival", estimatedArrival);
		}
		*/
		if (planedFly != null) {
			json.put("planedFly", planedFly);
		}
		if (alternate != null) {
			json.put("alternate", alternate);
		}
		if (flyNo != null) {
			json.put("flyNo", flyNo);
		}
		return json.toJSONString();
	}

	// 取消领航计划报
	private static String parseCNL(String[] split) {
		// 解析编组18 其它情报
		Map<String, String> parseG18 = parseG18(split[4]);

		// 解析编组13 起飞机场和时间
		String[] parseG13 = parseG13(split[2]);
		String departureAirport = parseG13[0];
		String departureAirportTime = parseG13[1];// 可能为""

		// 如果编组13携带信息无效 从编组18中查询
		if (departureAirport == null || "未知".equals(departureAirport)) {
			departureAirport = parseG18.get("DEP");
		}

		// 解析编组16 目的机场和预计飞行总时间 备降站
		Map<String, String> parseG16 = parseG16(split[3]);
		String arrivalAirport = parseG16.get("arrivalAirport");
		if ("未知".equals(arrivalAirport) || arrivalAirport == null) {
			arrivalAirport = parseG18.get("DEST");
		}
		// 预计飞行时间
		String flyTime = parseG16.get("flyTime");

		// 备降站1
		String alternate1 = parseG16.get("alternate1");
		if (alternate1 == null || "未知".equals(alternate1)) {
			alternate1 = parseG18.get("ALTN");
		}
		// 备降站2
		String alternate2 = parseG16.get("alternate2");
		if (alternate2 == null || "未知".equals(alternate2)) {
			alternate2 = parseG18.get("ALTN");
		}

		String planeType = parseG18.get("TYP");
		String flightLine = departureAirport + "-" + arrivalAirport;
		/** 以下信息需要根据 起飞机场 目的机场的不同具体判断 **/
		String incomingFlyNo = null;
		String departureFlyNo = null;
		/** 如果该条报文是无关报文 则只确定航班号 不需要确定是进港 还是出港 **/
		String flyNo = null;
		// 预计到达时间
		Long estimatedArrival = null;
		// 本站计划起飞时间
		Long planedFly = null;
		// 前站计划起飞时间
		Long prePlanedFly = null;
		// 备降站
		String alternate = null;
		// 1.出发地是长治
		if ("长治".equals(departureAirport)) {
			departureFlyNo = parseG7(split[1]);// 出发地是长治 则航班号代表出港航班号

			// 计算planedFly
			if (StringUtils.isNotBlank(departureAirportTime)) {
				// 需要判断其它信息里是否有DOF这一项的内容 如果有 则时间以DOF为准 如果没有 则时间为当天
				String dof = parseG18.get("DOF");
				if (dof != null) {
					planedFly = DateUtils.string2FormattingLongByDOF(dof, departureAirportTime);
				} else {
					planedFly = DateUtils.string2FormattingLong(departureAirportTime);
				}
			}
			// 拼接备降站
			alternate = jointAlternate(alternate1, alternate2);

		} else if ("长治".equals(arrivalAirport)) {
			incomingFlyNo = parseG7(split[1]);// 目的地是长治 则航班号代表进港航班号
			// 计算prePlanedFly
			if (StringUtils.isNotBlank(departureAirportTime)) {
				// 需要判断其它信息里是否有DOF这一项的内容 如果有 则时间以DOF为准 如果没有 则时间为当天
				String dof = parseG18.get("DOF");
				if (dof != null) {
					prePlanedFly = DateUtils.string2LongByDOF(dof, departureAirportTime);
				} else {
					prePlanedFly = DateUtils.string2Long(departureAirportTime);
				}
			}
			/*
			if (prePlanedFly != null && flyTime != null) {
				Long flyTimeLong = DateUtils.getFlyTime(flyTime);// long类型
				// 预计达到时间为前站计划起飞时间+预计飞行时间
				estimatedArrival = prePlanedFly + flyTimeLong;// long类型
				// 将Long类型的转成long （yyyymmddHHmmss的形式）
				estimatedArrival = DateUtils.longTolong(estimatedArrival);
			}
			*/
			flyTimeMap.remove(incomingFlyNo);

			// 拼接备降站
			alternate = jointAlternate(alternate1, alternate2);
		} else if (!"长治".equals(departureAirport) && !"长治".equals(arrivalAirport) && !"长治".equals(alternate1)
				&& !"长治".equals(alternate2)) {
			// 与长治无关的报文
			flyNo = parseG7(split[1]);
			alternate = jointAlternate(alternate1, alternate2);
		} else {// 备降站是长治
			incomingFlyNo = split[1];// 备降站是长治 则航班号代表进港航班号
			alternate = "长治";
		}
		// 将解析的数据封装成JSON
		JSONObject json = new JSONObject();
		json.put("messageType", "CNL");
		if (planeType != null) {
			json.put("planeType", planeType);
		}
		if (flightLine != null) {
			json.put("flightLine", flightLine);
		}
		if (incomingFlyNo != null) {
			json.put("incomingFlyNo", incomingFlyNo);
			json.put("incomingProg", "取消");
		}
		if (departureFlyNo != null) {
			json.put("departureFlyNo", departureFlyNo);
			json.put("departureProg", "取消");
		}
		/*
		if (estimatedArrival != null) {
			json.put("estimatedArrival", estimatedArrival);
		}
		*/
		if (planedFly != null) {
			json.put("planedFly", planedFly);
		}
		if (alternate != null) {
			json.put("alternate", alternate);
		}
		if (flyNo != null) {
			json.put("flyNo", flyNo);
		}
		return json.toJSONString();
	}

	// 起飞报
	private static String parseDEP(String[] split) {
		// 解析编组18 其它情报
		Map<String, String> parseG18 = parseG18(split[4]);

		// 解析编组13 起飞机场和时间
		String[] parseG13 = parseG13(split[2]);
		String departureAirport = parseG13[0];
		String departureAirportTime = parseG13[1];// 可能为""

		// 如果编组13携带信息无效 从编组18中查询
		if (departureAirport == null || "未知".equals(departureAirport)) {
			departureAirport = parseG18.get("DEP");
		}
		
		// 解析编组16 目的机场和预计飞行总时间 备降站
		Map<String, String> parseG16 = parseG16(split[3]);
		
		// 预计飞行时间
		String flyTime = parseG16.get("flyTime");
		
		String arrivalAirport = parseG16.get("arrivalAirport");
		if ("未知".equals(arrivalAirport) || arrivalAirport == null) {
			arrivalAirport = parseG18.get("DEST");
		}
		// 备降站1
		String alternate1 = parseG16.get("alternate1");
		if (alternate1 == null || "未知".equals(alternate1)) {
			alternate1 = parseG18.get("ALTN");
		}
		// 备降站2
		String alternate2 = parseG16.get("alternate2");
		if (alternate2 == null || "未知".equals(alternate2)) {
			alternate2 = parseG18.get("ALTN");
		}

		/** 共有信息 任务 机型 航线 **/
		String planeType = parseG18.get("TYP");
		String flightLine = departureAirport + "-" + arrivalAirport;
		/** 以下信息需要根据 起飞机场 目的机场的不同具体判断 **/
		String incomingFlyNo = null;
		String departureFlyNo = null;
		/** 如果该条报文是无关报文 则只确定航班号 不需要确定是进港 还是出港 **/
		String flyNo = null;
		// 本站实际起飞时间
		Long realFly = null;
		// 前站实际起飞时间
		Long preRealFly = null;
		// 备降站
		String alternate = null;
		// 出港状态
		String departureProg = null;
		// 进港状态
		String incomingProg = null;
		// 预计到达时间
		Long estimatedArrival = null;
		// 1.出发地是长治
		if ("长治".equals(departureAirport)) {
			departureFlyNo = parseG7(split[1]);// 出发地是长治 则航班号代表出港航班号
			// 计算realFly
			String dof = parseG18.get("DOF");
			if (dof != null) {
				realFly = DateUtils.string2FormattingLongByDOF(dof, departureAirportTime);
			} else {
				realFly = DateUtils.string2FormattingLong(departureAirportTime);
			}
			departureProg = "起飞";
			// 拼接备降站
			alternate = jointAlternate(alternate1, alternate2);
		} else if ("长治".equals(arrivalAirport)) {
			incomingFlyNo = parseG7(split[1]);// 目的地是长治 则航班号代表进港航班号
			// 计算preRealFly
			// 需要判断其它信息里是否有DOF这一项的内容 如果有 则时间以DOF为准 如果没有 则时间为当天
			String dof = parseG18.get("DOF");
			
			LogUtil.log("departureAirportTime:"+departureAirportTime, "ceshi");
			if (dof != null) {
				preRealFly = DateUtils.string2LongByDOF(dof, departureAirportTime);
			} else {
				preRealFly = DateUtils.string2Long(departureAirportTime);
			}
			LogUtil.log("preRealFly:"+preRealFly, "ceshi");
			//计算预计到达时间
			if(flyTime==null) {
				//起飞报不携带飞行总时间
				Long flyTimeLong = flyTimeMap.get(incomingFlyNo);
				LogUtil.log("incomingFlyNo:"+incomingFlyNo, "ceshi");
				LogUtil.log("flyTimeLong:"+flyTimeLong, "ceshi");
				if(flyTimeLong!=null) {
					estimatedArrival = preRealFly + flyTimeLong;// long类型
					LogUtil.log("estimatedArrival:"+estimatedArrival, "ceshi");
					// 将Long类型的转成long （yyyymmddHHmmss的形式）
					estimatedArrival = DateUtils.longTolong(estimatedArrival);
					LogUtil.log("estimatedArrival:"+estimatedArrival, "ceshi");
					flyTimeMap.remove(incomingFlyNo);
				}
			}else {
				//起飞报自身携带飞行总时间
				Long flyTimeLong = DateUtils.getFlyTime(flyTime);// long类型
				// 预计达到时间为前站计划起飞时间+预计飞行时间
				estimatedArrival = preRealFly + flyTimeLong;// long类型
				// 将Long类型的转成long （yyyymmddHHmmss的形式）
				estimatedArrival = DateUtils.longTolong(estimatedArrival);
			}
			
			incomingProg = "前站起飞";
			// 拼接备降站
			alternate = jointAlternate(alternate1, alternate2);
		} else if (!"长治".equals(departureAirport) && !"长治".equals(arrivalAirport) && !"长治".equals(alternate1)
				&& !"长治".equals(alternate2)) {
			// 与长治无关的报文
			flyNo = parseG7(split[1]);
			alternate = jointAlternate(alternate1, alternate2);
		} else {// 备降站是长治
			incomingFlyNo = split[1];// 备降站是长治 则航班号代表进港航班号
			alternate = "长治";
		}
		
		// 将解析的数据封装成JSON
		JSONObject json = new JSONObject();
		json.put("messageType", "DEP");
		if (planeType != null) {
			json.put("planeType", planeType);
		}
		if (flightLine != null) {
			json.put("flightLine", flightLine);
		}
		if (incomingFlyNo != null) {
			json.put("incomingFlyNo", incomingFlyNo);
		}
		if (departureFlyNo != null) {
			json.put("departureFlyNo", departureFlyNo);
		}
		if (alternate != null) {
			json.put("alternate", alternate);
		}
		if (flyNo != null) {
			json.put("flyNo", flyNo);
		}
		if (realFly != null) {
			json.put("realFly", realFly);
		}
		if (preRealFly != null) {
			json.put("preRealFly", DateUtils.longTolong(preRealFly));
		}
		if (departureProg != null) {
			json.put("departureProg", departureProg);
		}
		if (incomingProg != null) {
			json.put("incomingProg", incomingProg);
		}
		if (estimatedArrival!=null) {
			json.put("estimatedArrival", DateUtils.estimatedArrivalFormat(estimatedArrival));
		}
		return json.toJSONString();
	}

	// 落地报
	private static String parseARR(String[] split) {
		String departureAiport = parseG13(split[2])[0];
		String departureAiportTime = parseG13(split[2])[1];// 可能为""

		String flyNo = null;
		String incomingFlyNo = null;
		/** 原计划飞行航线 **/
		String planFlightLine = null;
		/** 实际飞行航线 **/
		String flightLine = null;
		Long realArrival = null;
		Long preRealFly = null;
		String alternate = null;
		String incomingProg = null;
		if (split.length == 4) {
			String[] parseG17 = parseG17(split[3]);
			String arrivalAirport = parseG17[0];
			String arrivalAirportTime = parseG17[1];
			// 编组3 7 13 17
			if (!"长治".equals(arrivalAirport)) {
				flyNo = parseG7(split[1]);
			} else {
				// 长治是目的机场
				incomingFlyNo = parseG7(split[1]);
				incomingProg = "到达";
			}
			if (StringUtils.isNotBlank(departureAiportTime)) {
				preRealFly = DateUtils.string2FormattingLong(departureAiportTime);
			}
			if (StringUtils.isNotBlank(arrivalAirportTime)) {
				realArrival = DateUtils.string2FormattingLong(arrivalAirportTime);
			}
			flightLine = departureAiport + "-" + arrivalAirport;
		} else {
			// 编组3 7 13 16 17
			String[] parseG17 = parseG17(split[4]);
			String arrivalAirport = parseG17[0];
			String arrivalAirportTime = parseG17[1];

			Map<String, String> parseG16 = parseG16(split[3]);
			String planArrivalAirport = parseG16.get("arrivalAirport");
			String alternate1 = parseG16.get("alternate1");
			String alternate2 = parseG16.get("alternate2");
			alternate = jointAlternate(alternate1, alternate2);
			// 如果长治不是目的机场 也不是 实际降落机场
			if (!"长治".equals(arrivalAirport) && !"长治".equals(planArrivalAirport)) {
				flyNo = parseG7(split[1]);
			} else {
				// 长治是目的机场
				incomingFlyNo = parseG7(split[1]);
				// 说明本来要在长治降落的飞机在备降站降落 要将原航线进港状态更改
				if ("长治".equals(planArrivalAirport)) {
					incomingProg = "到达备降站";
				}
				if ("长治".equals(arrivalAirport)) {
					incomingProg = "备降";
				}
			}

			planFlightLine = departureAiport + "-" + planArrivalAirport;
			flightLine = departureAiport + "-" + arrivalAirport;

			if (StringUtils.isNotBlank(arrivalAirportTime)) {
				realArrival = DateUtils.string2FormattingLong(arrivalAirportTime);
			}
			if (StringUtils.isNotBlank(departureAiportTime)) {
				preRealFly = DateUtils.string2FormattingLong(departureAiportTime);
			}
		}
		JSONObject json = new JSONObject();
		json.put("messageType", "ARR");
		if (flightLine != null) {
			json.put("flightLine", flightLine);
		}
		if (planFlightLine != null) {
			json.put("planFlightLine", planFlightLine);
		}
		if (incomingFlyNo != null) {
			json.put("incomingFlyNo", incomingFlyNo);
		}
		if (flyNo != null) {
			json.put("flyNo", flyNo);
		}
		if (preRealFly != null) {
			json.put("preRealFly", preRealFly);
		}
		if (realArrival != null) {
			json.put("realArrival", realArrival);
		}
		if (alternate != null) {
			json.put("alternate", alternate);
		}
		if (incomingProg != null) {
			json.put("incomingProg", incomingProg);
		}
		return json.toJSONString();
	}

	// 延误报
	private static String parseDLA(String[] split) {
		// 解析编组18 其它情报
		Map<String, String> parseG18 = parseG18(split[4]);

		// 解析编组13 起飞机场和时间
		String[] parseG13 = parseG13(split[2]);
		String departureAirport = parseG13[0];
		String departureAirportTime = parseG13[1];// 可能为""

		// 如果编组13携带信息无效 从编组18中查询
		if (departureAirport == null || "未知".equals(departureAirport)) {
			departureAirport = parseG18.get("DEP");
		}

		// 解析编组16 目的机场和预计飞行总时间 备降站
		Map<String, String> parseG16 = parseG16(split[3]);
		String arrivalAirport = parseG16.get("arrivalAirport");
		if ("未知".equals(arrivalAirport) || arrivalAirport == null) {
			arrivalAirport = parseG18.get("DEST");
		}
		// 预计飞行时间
		String flyTime = parseG16.get("flyTime");

		// 备降站1
		String alternate1 = parseG16.get("alternate1");
		if (alternate1 == null || "未知".equals(alternate1)) {
			alternate1 = parseG18.get("ALTN");
		}
		// 备降站2
		String alternate2 = parseG16.get("alternate2");
		if (alternate2 == null || "未知".equals(alternate2)) {
			alternate2 = parseG18.get("ALTN");
		}

		String planeType = parseG18.get("DLA");

		String flightLine = departureAirport + "-" + arrivalAirport;
		/** 以下信息需要根据 起飞机场 目的机场的不同具体判断 **/
		String incomingFlyNo = null;
		String departureFlyNo = null;
		/** 如果该条报文是无关报文 则只确定航班号 不需要确定是进港 还是出港 **/
		String flyNo = null;
		// 预计到达时间
		Long estimatedArrival = null;
		// 本站计划起飞时间
		Long planedFly = null;
		// 前站计划起飞时间
		Long prePlanedFly = null;
		// 备降站
		String alternate = null;
		// 出港状态
		String departureProg = null;
		// 进港状态
		String incomingProg = null;
		// 1.出发地是长治
		if ("长治".equals(departureAirport)) {
			departureFlyNo = parseG7(split[1]);// 出发地是长治 则航班号代表出港航班号

			// 计算planedFly
			if (StringUtils.isNotBlank(departureAirportTime)) {
				// 需要判断其它信息里是否有DOF这一项的内容 如果有 则时间以DOF为准 如果没有 则时间为当天
				String dof = parseG18.get("DOF");
				if (dof != null) {
					planedFly = DateUtils.string2FormattingLongByDOF(dof, departureAirportTime);
				} else {
					planedFly = DateUtils.string2FormattingLong(departureAirportTime);
				}
			}
			departureProg = "延误";
			// 拼接备降站
			alternate = jointAlternate(alternate1, alternate2);

		} else if ("长治".equals(arrivalAirport)) {
			incomingFlyNo = parseG7(split[1]);// 目的地是长治 则航班号代表进港航班号
			// 计算prePlanedFly
			if (StringUtils.isNotBlank(departureAirportTime)) {
				// 需要判断其它信息里是否有DOF这一项的内容 如果有 则时间以DOF为准 如果没有 则时间为当天
				String dof = parseG18.get("DOF");
				if (dof != null) {
					prePlanedFly = DateUtils.string2LongByDOF(dof, departureAirportTime);
				} else {
					prePlanedFly = DateUtils.string2Long(departureAirportTime);
				}
			}
			/*
			if (prePlanedFly != null && flyTime != null) {
				Long flyTimeLong = DateUtils.getFlyTime(flyTime);// long类型
				// 预计达到时间为前站计划起飞时间+预计飞行时间
				estimatedArrival = prePlanedFly + flyTimeLong;// long类型
				// 将Long类型的转成long （yyyymmddHHmmss的形式）
				estimatedArrival = DateUtils.longTolong(estimatedArrival);
			}
			*/
			if(flyTime!=null) {
				flyTimeMap.put(incomingFlyNo, DateUtils.getFlyTime(flyTime));
			}
			incomingProg = "延误";
			// 拼接备降站
			alternate = jointAlternate(alternate1, alternate2);
		} else if (!"长治".equals(departureAirport) && !"长治".equals(arrivalAirport) && !"长治".equals(alternate1)
				&& !"长治".equals(alternate2)) {
			// 与长治无关的报文
			flyNo = parseG7(split[1]);
			alternate = jointAlternate(alternate1, alternate2);
		} else {// 备降站是长治
			incomingFlyNo = split[1];// 备降站是长治 则航班号代表进港航班号
			alternate = "长治";
		}
		// 将解析的数据封装成JSON
		JSONObject json = new JSONObject();
		json.put("messageType", "DLA");
		if (planeType != null) {
			json.put("planeType", planeType);
		}
		if (flightLine != null) {
			json.put("flightLine", flightLine);
		}
		if (incomingFlyNo != null) {
			json.put("incomingFlyNo", incomingFlyNo);
		}
		if (departureFlyNo != null) {
			json.put("departureFlyNo", departureFlyNo);
		}
		/*
		if (estimatedArrival != null) {
			json.put("estimatedArrival", estimatedArrival);
		}
		*/
		if (planedFly != null) {
			json.put("planedFly", planedFly);
		}
		if (alternate != null) {
			json.put("alternate", alternate);
		}
		if (flyNo != null) {
			json.put("flyNo", flyNo);
		}
		if (incomingProg != null) {
			json.put("incomingProg", incomingProg);
		}
		if (departureProg != null) {
			json.put("departureProg", departureProg);
		}
		return json.toJSONString();
	}

	// 现行飞行变更报
	private static String parseCPL(String[] split) {
		// 解析编组18 其它情报
		Map<String, String> parseG18 = parseG18(split[8]);
		String parseG14 = parseG14(split[6]);
		// 解析编组13 起飞机场和时间
		String[] parseG13 = parseG13(split[5]);
		String departureAirport = parseG13[0];
		String departureAirportTime = parseG13[1];// 可能为""

		// 如果编组13携带信息无效 从编组18中查询
		if (departureAirport == null || "未知".equals(departureAirport)) {
			departureAirport = parseG18.get("DEP");
		}

		// 解析编组16 目的机场和预计飞行总时间 备降站
		Map<String, String> parseG16 = parseG16(split[8]);
		String arrivalAirport = parseG16.get("arrivalAirport");
		if ("未知".equals(arrivalAirport) || arrivalAirport == null) {
			arrivalAirport = parseG18.get("DEST");
		}
		// 预计飞行时间
		String flyTime = parseG16.get("flyTime");

		// 备降站1
		String alternate1 = parseG16.get("alternate1");
		if (alternate1 == null || "未知".equals(alternate1)) {
			alternate1 = parseG18.get("ALTN");
		}
		// 备降站2
		String alternate2 = parseG16.get("alternate2");
		if (alternate2 == null || "未知".equals(alternate2)) {
			alternate2 = parseG18.get("ALTN");
		}

		/** 共有信息 任务 机型 航线 **/
		String task = parseG8(split[2]);
		String planeType = parseG9(split[3]);
		if (planeType.equals("ZZZZ")) {
			planeType = parseG18.get("TYP");
		}
		String flightLine = departureAirport + "-" + arrivalAirport;
		/** 以下信息需要根据 起飞机场 目的机场的不同具体判断 **/
		String incomingFlyNo = null;
		String departureFlyNo = null;
		/** 如果该条报文是无关报文 则只确定航班号 不需要确定是进港 还是出港 **/
		String flyNo = null;
		// 预计到达时间
		Long estimatedArrival = null;
		// 本站实际起飞时间
		Long realFly = null;
		// 前站实际起飞时间
		Long preRealFly = null;
		// 备降站
		String alternate = null;
		Long preRealFlyLong = null;
		String incomingProg = null;
		String departureProg = null;
		// 1.出发地是长治
		if ("长治".equals(departureAirport)) {
			departureFlyNo = parseG7(split[1]);// 出发地是长治 则航班号代表出港航班号

			// 计算planedFly
			if (StringUtils.isNotBlank(departureAirportTime)) {
				// 需要判断其它信息里是否有DOF这一项的内容 如果有 则时间以DOF为准 如果没有 则时间为当天
				String dof = parseG18.get("DOF");
				if (dof != null) {
					realFly = DateUtils.string2FormattingLongByDOF(dof, departureAirportTime);
				} else {
					realFly = DateUtils.string2FormattingLong(departureAirportTime);
				}
			}
			departureProg = "起飞";
			// 拼接备降站
			alternate = jointAlternate(alternate1, alternate2);

		} else if ("长治".equals(arrivalAirport)) {
			incomingFlyNo = parseG7(split[1]);// 目的地是长治 则航班号代表进港航班号
			// 计算prePlanedFly
			if (StringUtils.isNotBlank(departureAirportTime)) {
				// 需要判断其它信息里是否有DOF这一项的内容 如果有 则时间以DOF为准 如果没有 则时间为当天
				String dof = parseG18.get("DOF");
				if (dof != null) {
					preRealFly = DateUtils.string2FormattingLongByDOF(dof, departureAirportTime);
					preRealFlyLong = DateUtils.string2LongByDOF(dof, departureAirportTime);
				} else {
					preRealFly = DateUtils.string2FormattingLong(departureAirportTime);
					preRealFlyLong = DateUtils.string2Long(departureAirportTime);
				}
			}
			/*
			if (flyTime != null && parseG14 != null) {
				Long t = DateUtils.getFlyTime(flyTime);
				// 预计达到时间为前站计划起飞时间+预计飞行时间
				String dof = parseG18.get("DOF");
				if (dof != null) {
					estimatedArrival = DateUtils.string2LongByDOF(dof, parseG14) + t;
					estimatedArrival = DateUtils.longTolong(estimatedArrival);
				} else {
					estimatedArrival = DateUtils.string2Long(parseG14) + t;
					estimatedArrival = DateUtils.longTolong(estimatedArrival);
				}
			}
			*/
			incomingProg = "备降";
			// 拼接备降站
			alternate = jointAlternate(alternate1, alternate2);
		} else if (!"长治".equals(departureAirport) && !"长治".equals(arrivalAirport) && !"长治".equals(alternate1)
				&& !"长治".equals(alternate2)) {
			// 与长治无关的报文
			flyNo = parseG7(split[1]);
		} else {// 备降站是长治
			incomingFlyNo = split[1];// 备降站是长治 则航班号代表进港航班号
			alternate = "长治";
		}
		// 将解析的数据封装成JSON
		JSONObject json = new JSONObject();
		json.put("messageType", "CPL");
		json.put("task", task);
		if (planeType != null) {
			json.put("planeType", planeType);
		}
		if (flightLine != null) {
			json.put("flightLine", flightLine);
		}
		if (incomingFlyNo != null) {
			json.put("incomingFlyNo", incomingFlyNo);
		}
		if (departureFlyNo != null) {
			json.put("departureFlyNo", departureFlyNo);
		}
		/*
		if (estimatedArrival != null) {
			json.put("estimatedArrival", estimatedArrival);
		}
		*/
		if (realFly != null) {
			json.put("realFly", realFly);
		}
		if (preRealFly != null) {
			json.put("preRealFly", preRealFly);
		}
		if (alternate != null) {
			json.put("alternate", alternate);
		}
		if (flyNo != null) {
			json.put("flyNo", flyNo);
		}
		if (incomingProg != null) {
			json.put("incomingProg", incomingProg);
		}
		if (departureProg != null) {
			json.put("departureProg", departureProg);
		}
		return json.toJSONString();
	}

	// 预计飞越报
	private static String parseEST(String[] split) {
		// 解析编组13 起飞机场和时间
		String[] parseG13 = parseG13(split[2]);
		String departureAirport = parseG13[0];
		String departureAirportTime = parseG13[1];// 可能为""

		// 解析编组16 目的机场和预计飞行总时间 备降站
		Map<String, String> parseG16 = parseG16(split[4]);
		String arrivalAirport = parseG16.get("arrivalAirport");
		// 预计飞行时间
		String flyTime = parseG16.get("flyTime");

		// 备降站1
		String alternate1 = parseG16.get("alternate1");

		// 备降站2
		String alternate2 = parseG16.get("alternate2");

		/** 共有信息 任务 机型 航线 **/
		String flightLine = departureAirport + "-" + arrivalAirport;
		/** 以下信息需要根据 起飞机场 目的机场的不同具体判断 **/
		String incomingFlyNo = null;
		String departureFlyNo = null;
		/** 如果该条报文是无关报文 则只确定航班号 不需要确定是进港 还是出港 **/
		String flyNo = null;
		// 备降站
		String alternate = null;
		// 预飞
		Long estimatedFly = null;
		// 1.出发地是长治
		if ("长治".equals(departureAirport)) {
			departureFlyNo = parseG7(split[1]);// 出发地是长治 则航班号代表出港航班号

			// 计算planedFly
			if (StringUtils.isNotBlank(departureAirportTime)) {
				estimatedFly = DateUtils.string2FormattingLong(departureAirportTime);
			}
			// 拼接备降站
			alternate = jointAlternate(alternate1, alternate2);

		} else if ("长治".equals(arrivalAirport)) {
			incomingFlyNo = parseG7(split[1]);// 目的地是长治 则航班号代表进港航班号
			// 拼接备降站
			alternate = jointAlternate(alternate1, alternate2);
		} else if (!"长治".equals(departureAirport) && !"长治".equals(arrivalAirport) && !"长治".equals(alternate1)
				&& !"长治".equals(alternate2)) {
			// 与长治无关的报文
			flyNo = parseG7(split[1]);
			alternate = jointAlternate(alternate1, alternate2);
		} else {// 备降站是长治
			incomingFlyNo = split[1];// 备降站是长治 则航班号代表进港航班号
			alternate = "长治";
		}
		// 将解析的数据封装成JSON
		JSONObject json = new JSONObject();
		json.put("messageType", "EST");
		if (flightLine != null) {
			json.put("flightLine", flightLine);
		}
		if (incomingFlyNo != null) {
			json.put("incomingFlyNo", incomingFlyNo);
		}
		if (departureFlyNo != null) {
			json.put("departureFlyNo", departureFlyNo);
		}
		if (estimatedFly != null) {
			json.put("estimatedFly", estimatedFly);
		}
		if (alternate != null) {
			json.put("alternate", alternate);
		}
		if (flyNo != null) {
			json.put("flyNo", flyNo);
		}
		return json.toJSONString();
	}

	// 管制协调报
	private static String parseCDN(String[] split) {
		// 解析编组13 起飞机场和时间
		String[] parseG13 = parseG13(split[2]);
		String departureAirport = parseG13[0];

		// 解析编组16 目的机场和预计飞行总时间 备降站
		Map<String, String> parseG16 = parseG16(split[3]);
		String arrivalAirport = parseG16.get("arrivalAirport");

		// 备降站1
		String alternate1 = parseG16.get("alternate1");

		// 备降站2
		String alternate2 = parseG16.get("alternate2");

		/** 共有信息 任务 机型 航线 **/
		String flightLine = departureAirport + "-" + arrivalAirport;
		/** 以下信息需要根据 起飞机场 目的机场的不同具体判断 **/
		String incomingFlyNo = null;
		String departureFlyNo = null;
		/** 如果该条报文是无关报文 则只确定航班号 不需要确定是进港 还是出港 **/
		String flyNo = null;
		// 备降站
		String alternate = null;
		// 1.出发地是长治
		if ("长治".equals(departureAirport)) {
			departureFlyNo = parseG7(split[1]);// 出发地是长治 则航班号代表出港航班号
			// 拼接备降站
			alternate = jointAlternate(alternate1, alternate2);

		} else if ("长治".equals(arrivalAirport)) {
			incomingFlyNo = parseG7(split[1]);// 目的地是长治 则航班号代表进港航班号
			// 拼接备降站
			alternate = jointAlternate(alternate1, alternate2);
		} else if (!"长治".equals(departureAirport) && !"长治".equals(arrivalAirport) && !"长治".equals(alternate1)
				&& !"长治".equals(alternate2)) {
			// 与长治无关的报文
			flyNo = parseG7(split[1]);
			alternate = jointAlternate(alternate1, alternate2);
		} else {// 备降站是长治
			incomingFlyNo = split[1];// 备降站是长治 则航班号代表进港航班号
			alternate = "长治";
		}
		// 将解析的数据封装成JSON
		JSONObject json = new JSONObject();
		json.put("messageType", "CDN");

		if (flightLine != null) {
			json.put("flightLine", flightLine);
		}
		if (incomingFlyNo != null) {
			json.put("incomingFlyNo", incomingFlyNo);
		}
		if (departureFlyNo != null) {
			json.put("departureFlyNo", departureFlyNo);
		}
		if (alternate != null) {
			json.put("alternate", alternate);
		}
		if (flyNo != null) {
			json.put("flyNo", flyNo);
		}
		return json.toJSONString();
	}

	// 管制协调接受报
	private static String parseACP(String[] split) {
		// 解析编组13 起飞机场和时间
		String[] parseG13 = parseG13(split[2]);
		String departureAirport = parseG13[0];

		// 解析编组16 目的机场和预计飞行总时间 备降站
		Map<String, String> parseG16 = parseG16(split[3]);
		String arrivalAirport = parseG16.get("arrivalAirport");

		// 备降站1
		String alternate1 = parseG16.get("alternate1");

		// 备降站2
		String alternate2 = parseG16.get("alternate2");

		/** 共有信息 任务 机型 航线 **/
		String flightLine = departureAirport + "-" + arrivalAirport;
		/** 以下信息需要根据 起飞机场 目的机场的不同具体判断 **/
		String incomingFlyNo = null;
		String departureFlyNo = null;
		/** 如果该条报文是无关报文 则只确定航班号 不需要确定是进港 还是出港 **/
		String flyNo = null;
		// 备降站
		String alternate = null;
		// 1.出发地是长治
		if ("长治".equals(departureAirport)) {
			departureFlyNo = parseG7(split[1]);// 出发地是长治 则航班号代表出港航班号
			// 拼接备降站
			alternate = jointAlternate(alternate1, alternate2);

		} else if ("长治".equals(arrivalAirport)) {
			incomingFlyNo = parseG7(split[1]);// 目的地是长治 则航班号代表进港航班号
			// 拼接备降站
			alternate = jointAlternate(alternate1, alternate2);
		} else if (!"长治".equals(departureAirport) && !"长治".equals(arrivalAirport) && !"长治".equals(alternate1)
				&& !"长治".equals(alternate2)) {
			// 与长治无关的报文
			flyNo = parseG7(split[1]);
			alternate = jointAlternate(alternate1, alternate2);
		} else {// 备降站是长治
			incomingFlyNo = split[1];// 备降站是长治 则航班号代表进港航班号
			alternate = "长治";
		}
		// 将解析的数据封装成JSON
		JSONObject json = new JSONObject();
		json.put("messageType", "ACP");

		if (flightLine != null) {
			json.put("flightLine", flightLine);
		}
		if (incomingFlyNo != null) {
			json.put("incomingFlyNo", incomingFlyNo);
		}
		if (departureFlyNo != null) {
			json.put("departureFlyNo", departureFlyNo);
		}
		if (alternate != null) {
			json.put("alternate", alternate);
		}
		if (flyNo != null) {
			json.put("flyNo", flyNo);
		}
		return json.toJSONString();
	}

	// 请求飞行计划报
	private static String parseRQP(String[] split) {
		// 解析编组18 其它情报
		Map<String, String> parseG18 = parseG18(split[4]);

		// 解析编组13 起飞机场和时间
		String[] parseG13 = parseG13(split[2]);
		String departureAirport = parseG13[0];
		String departureAirportTime = parseG13[1];// 可能为""

		// 如果编组13携带信息无效 从编组18中查询
		if (departureAirport == null || "未知".equals(departureAirport)) {
			departureAirport = parseG18.get("DEP");
		}

		// 解析编组16 目的机场和预计飞行总时间 备降站
		Map<String, String> parseG16 = parseG16(split[3]);
		String arrivalAirport = parseG16.get("arrivalAirport");
		if ("未知".equals(arrivalAirport) || arrivalAirport == null) {
			arrivalAirport = parseG18.get("DEST");
		}
		// 预计飞行时间
		String flyTime = parseG16.get("flyTime");

		// 备降站1
		String alternate1 = parseG16.get("alternate1");
		if (alternate1 == null || "未知".equals(alternate1)) {
			alternate1 = parseG18.get("ALTN");
		}
		// 备降站2
		String alternate2 = parseG16.get("alternate2");
		if (alternate2 == null || "未知".equals(alternate2)) {
			alternate2 = parseG18.get("ALTN");
		}

		/** 共有信息 任务 机型 航线 **/
		String flightLine = departureAirport + "-" + arrivalAirport;
		/** 以下信息需要根据 起飞机场 目的机场的不同具体判断 **/
		String incomingFlyNo = null;
		String departureFlyNo = null;
		/** 如果该条报文是无关报文 则只确定航班号 不需要确定是进港 还是出港 **/
		String flyNo = null;
		// 预计到达时间
		Long estimatedArrival = null;
		// 本站计划起飞时间
		Long planedFly = null;
		// 前站计划起飞时间
		Long prePlanedFly = null;
		// 备降站
		String alternate = null;
		// 1.出发地是长治
		if ("长治".equals(departureAirport)) {
			departureFlyNo = parseG7(split[1]);// 出发地是长治 则航班号代表出港航班号

			// 计算planedFly
			if (StringUtils.isNotBlank(departureAirportTime)) {
				// 需要判断其它信息里是否有DOF这一项的内容 如果有 则时间以DOF为准 如果没有 则时间为当天
				String dof = parseG18.get("DOF");
				if (dof != null) {
					planedFly = DateUtils.string2FormattingLongByDOF(dof, departureAirportTime);
				} else {
					planedFly = DateUtils.string2FormattingLong(departureAirportTime);
				}
			}
			// 拼接备降站
			alternate = jointAlternate(alternate1, alternate2);

		} else if ("长治".equals(arrivalAirport)) {
			incomingFlyNo = parseG7(split[1]);// 目的地是长治 则航班号代表进港航班号
			// 计算prePlanedFly
			if (StringUtils.isNotBlank(departureAirportTime)) {
				// 需要判断其它信息里是否有DOF这一项的内容 如果有 则时间以DOF为准 如果没有 则时间为当天
				String dof = parseG18.get("DOF");
				if (dof != null) {
					prePlanedFly = DateUtils.string2LongByDOF(dof, departureAirportTime);
				} else {
					prePlanedFly = DateUtils.string2Long(departureAirportTime);
				}
			}
			if (prePlanedFly != null && flyTime != null) {
				Long flyTimeLong = DateUtils.getFlyTime(flyTime);// long类型
				// 预计达到时间为前站计划起飞时间+预计飞行时间
				estimatedArrival = prePlanedFly + flyTimeLong;// long类型
				// 将Long类型的转成long （yyyymmddHHmmss的形式）
				estimatedArrival = DateUtils.longTolong(estimatedArrival);
			}

			// 拼接备降站
			alternate = jointAlternate(alternate1, alternate2);
		} else if (!"长治".equals(departureAirport) && !"长治".equals(arrivalAirport) && !"长治".equals(alternate1)
				&& !"长治".equals(alternate2)) {
			// 与长治无关的报文
			flyNo = parseG7(split[1]);
			alternate = jointAlternate(alternate1, alternate2);
		} else {// 备降站是长治
			incomingFlyNo = split[1];// 备降站是长治 则航班号代表进港航班号
			alternate = "长治";
		}
		// 将解析的数据封装成JSON
		JSONObject json = new JSONObject();
		json.put("messageType", "RQP");
		if (flightLine != null) {
			json.put("flightLine", flightLine);
		}
		if (incomingFlyNo != null) {
			json.put("incomingFlyNo", incomingFlyNo);
		}
		if (departureFlyNo != null) {
			json.put("departureFlyNo", departureFlyNo);
		}
		/*
		if (estimatedArrival != null) {
			json.put("estimatedArrival", estimatedArrival);
		}
		*/
		if (planedFly != null) {
			json.put("planedFly", planedFly);
		}
		if (alternate != null) {
			json.put("alternate", alternate);
		}
		if (flyNo != null) {
			json.put("flyNo", flyNo);
		}
		return json.toJSONString();
	}

	// 请求领航计划补充信息报
	private static String parseRQS(String[] split) {
		// 解析编组18 其它情报
		Map<String, String> parseG18 = parseG18(split[4]);

		// 解析编组13 起飞机场和时间
		String[] parseG13 = parseG13(split[2]);
		String departureAirport = parseG13[0];
		String departureAirportTime = parseG13[1];// 可能为""

		// 如果编组13携带信息无效 从编组18中查询
		if (departureAirport == null || "未知".equals(departureAirport)) {
			departureAirport = parseG18.get("DEP");
		}

		// 解析编组16 目的机场和预计飞行总时间 备降站
		Map<String, String> parseG16 = parseG16(split[3]);
		String arrivalAirport = parseG16.get("arrivalAirport");
		if ("未知".equals(arrivalAirport) || arrivalAirport == null) {
			arrivalAirport = parseG18.get("DEST");
		}
		// 预计飞行时间
		String flyTime = parseG16.get("flyTime");

		// 备降站1
		String alternate1 = parseG16.get("alternate1");
		if (alternate1 == null || "未知".equals(alternate1)) {
			alternate1 = parseG18.get("ALTN");
		}
		// 备降站2
		String alternate2 = parseG16.get("alternate2");
		if (alternate2 == null || "未知".equals(alternate2)) {
			alternate2 = parseG18.get("ALTN");
		}

		/** 共有信息 任务 机型 航线 **/
		String flightLine = departureAirport + "-" + arrivalAirport;
		/** 以下信息需要根据 起飞机场 目的机场的不同具体判断 **/
		String incomingFlyNo = null;
		String departureFlyNo = null;
		/** 如果该条报文是无关报文 则只确定航班号 不需要确定是进港 还是出港 **/
		String flyNo = null;
		// 预计到达时间
		Long estimatedArrival = null;
		// 本站计划起飞时间
		Long planedFly = null;
		// 前站计划起飞时间
		Long prePlanedFly = null;
		// 备降站
		String alternate = null;
		// 1.出发地是长治
		if ("长治".equals(departureAirport)) {
			departureFlyNo = parseG7(split[1]);// 出发地是长治 则航班号代表出港航班号

			// 计算planedFly
			if (StringUtils.isNotBlank(departureAirportTime)) {
				// 需要判断其它信息里是否有DOF这一项的内容 如果有 则时间以DOF为准 如果没有 则时间为当天
				String dof = parseG18.get("DOF");
				if (dof != null) {
					planedFly = DateUtils.string2FormattingLongByDOF(dof, departureAirportTime);
				} else {
					planedFly = DateUtils.string2FormattingLong(departureAirportTime);
				}
			}
			// 拼接备降站
			alternate = jointAlternate(alternate1, alternate2);

		} else if ("长治".equals(arrivalAirport)) {
			incomingFlyNo = parseG7(split[1]);// 目的地是长治 则航班号代表进港航班号
			// 计算prePlanedFly
			if (StringUtils.isNotBlank(departureAirportTime)) {
				// 需要判断其它信息里是否有DOF这一项的内容 如果有 则时间以DOF为准 如果没有 则时间为当天
				String dof = parseG18.get("DOF");
				if (dof != null) {
					prePlanedFly = DateUtils.string2LongByDOF(dof, departureAirportTime);
				} else {
					prePlanedFly = DateUtils.string2Long(departureAirportTime);
				}
			}
			if (prePlanedFly != null && flyTime != null) {
				Long flyTimeLong = DateUtils.getFlyTime(flyTime);// long类型
				// 预计达到时间为前站计划起飞时间+预计飞行时间
				estimatedArrival = prePlanedFly + flyTimeLong;// long类型
				// 将Long类型的转成long （yyyymmddHHmmss的形式）
				estimatedArrival = DateUtils.longTolong(estimatedArrival);
			}

			// 拼接备降站
			alternate = jointAlternate(alternate1, alternate2);
		} else if (!"长治".equals(departureAirport) && !"长治".equals(arrivalAirport) && !"长治".equals(alternate1)
				&& !"长治".equals(alternate2)) {
			// 与长治无关的报文
			flyNo = parseG7(split[1]);
			alternate = jointAlternate(alternate1, alternate2);
		} else {// 备降站是长治
			incomingFlyNo = split[1];// 备降站是长治 则航班号代表进港航班号
			alternate = "长治";
		}
		// 将解析的数据封装成JSON
		JSONObject json = new JSONObject();
		json.put("messageType", "RQP");
		if (flightLine != null) {
			json.put("flightLine", flightLine);
		}
		if (incomingFlyNo != null) {
			json.put("incomingFlyNo", incomingFlyNo);
		}
		if (departureFlyNo != null) {
			json.put("departureFlyNo", departureFlyNo);
		}
		/*
		if (estimatedArrival != null) {
			json.put("estimatedArrival", estimatedArrival);
		}
		*/
		if (planedFly != null) {
			json.put("planedFly", planedFly);
		}
		if (alternate != null) {
			json.put("alternate", alternate);
		}
		if (flyNo != null) {
			json.put("flyNo", flyNo);
		}
		return json.toJSONString();
	}

	// 领航计划补充信息报
	private static String parseSPL(String[] split) {
		// 解析编组18 其它情报
		Map<String, String> parseG18 = parseG18(split[4]);

		// 解析编组13 起飞机场和时间
		String[] parseG13 = parseG13(split[2]);
		String departureAirport = parseG13[0];
		String departureAirportTime = parseG13[1];// 可能为""

		// 如果编组13携带信息无效 从编组18中查询
		if (departureAirport == null || "未知".equals(departureAirport)) {
			departureAirport = parseG18.get("DEP");
		}

		// 解析编组16 目的机场和预计飞行总时间 备降站
		Map<String, String> parseG16 = parseG16(split[3]);
		String arrivalAirport = parseG16.get("arrivalAirport");
		if ("未知".equals(arrivalAirport) || arrivalAirport == null) {
			arrivalAirport = parseG18.get("DEST");
		}
		// 预计飞行时间
		String flyTime = parseG16.get("flyTime");

		// 备降站1
		String alternate1 = parseG16.get("alternate1");
		if (alternate1 == null || "未知".equals(alternate1)) {
			alternate1 = parseG18.get("ALTN");
		}
		// 备降站2
		String alternate2 = parseG16.get("alternate2");
		if (alternate2 == null || "未知".equals(alternate2)) {
			alternate2 = parseG18.get("ALTN");
		}

		/** 共有信息 任务 机型 航线 **/
		String flightLine = departureAirport + "-" + arrivalAirport;
		/** 以下信息需要根据 起飞机场 目的机场的不同具体判断 **/
		String incomingFlyNo = null;
		String departureFlyNo = null;
		/** 如果该条报文是无关报文 则只确定航班号 不需要确定是进港 还是出港 **/
		String flyNo = null;
		// 预计到达时间
		Long estimatedArrival = null;
		// 本站计划起飞时间
		Long planedFly = null;
		// 前站计划起飞时间
		Long prePlanedFly = null;
		// 备降站
		String alternate = null;
		// 1.出发地是长治
		if ("长治".equals(departureAirport)) {
			departureFlyNo = parseG7(split[1]);// 出发地是长治 则航班号代表出港航班号

			// 计算planedFly
			if (StringUtils.isNotBlank(departureAirportTime)) {
				// 需要判断其它信息里是否有DOF这一项的内容 如果有 则时间以DOF为准 如果没有 则时间为当天
				String dof = parseG18.get("DOF");
				if (dof != null) {
					planedFly = DateUtils.string2FormattingLongByDOF(dof, departureAirportTime);
				} else {
					planedFly = DateUtils.string2FormattingLong(departureAirportTime);
				}
			}
			// 拼接备降站
			alternate = jointAlternate(alternate1, alternate2);

		} else if ("长治".equals(arrivalAirport)) {
			incomingFlyNo = parseG7(split[1]);// 目的地是长治 则航班号代表进港航班号
			// 计算prePlanedFly
			if (StringUtils.isNotBlank(departureAirportTime)) {
				// 需要判断其它信息里是否有DOF这一项的内容 如果有 则时间以DOF为准 如果没有 则时间为当天
				String dof = parseG18.get("DOF");
				if (dof != null) {
					prePlanedFly = DateUtils.string2LongByDOF(dof, departureAirportTime);
				} else {
					prePlanedFly = DateUtils.string2Long(departureAirportTime);
				}
			}
			if (prePlanedFly != null && flyTime != null) {
				Long flyTimeLong = DateUtils.getFlyTime(flyTime);// long类型
				// 预计达到时间为前站计划起飞时间+预计飞行时间
				estimatedArrival = prePlanedFly + flyTimeLong;// long类型
				// 将Long类型的转成long （yyyymmddHHmmss的形式）
				estimatedArrival = DateUtils.longTolong(estimatedArrival);
			}

			// 拼接备降站
			alternate = jointAlternate(alternate1, alternate2);
		} else if (!"长治".equals(departureAirport) && !"长治".equals(arrivalAirport) && !"长治".equals(alternate1)
				&& !"长治".equals(alternate2)) {
			// 与长治无关的报文
			flyNo = parseG7(split[1]);
			alternate = jointAlternate(alternate1, alternate2);
		} else {// 备降站是长治
			incomingFlyNo = split[1];// 备降站是长治 则航班号代表进港航班号
			alternate = "长治";
		}
		// 将解析的数据封装成JSON
		JSONObject json = new JSONObject();
		json.put("messageType", "RQP");
		if (flightLine != null) {
			json.put("flightLine", flightLine);
		}
		if (incomingFlyNo != null) {
			json.put("incomingFlyNo", incomingFlyNo);
		}
		if (departureFlyNo != null) {
			json.put("departureFlyNo", departureFlyNo);
		}
		/*
		if (estimatedArrival != null) {
			json.put("estimatedArrival", estimatedArrival);
		}
		*/
		if (planedFly != null) {
			json.put("planedFly", planedFly);
		}
		if (alternate != null) {
			json.put("alternate", alternate);
		}
		if (flyNo != null) {
			json.put("flyNo", flyNo);
		}
		return json.toJSONString();
	}

	// 告警报
	private static String parseALR(String[] split) {
		// 解析编组18 其它情报
		Map<String, String> parseG18 = parseG18(split[9]);

		// 解析编组13 起飞机场和时间
		String[] parseG13 = parseG13(split[6]);
		String departureAirport = parseG13[0];
		String departureAirportTime = parseG13[1];// 可能为""

		// 如果编组13携带信息无效 从编组18中查询
		if (departureAirport == null || "未知".equals(departureAirport)) {
			departureAirport = parseG18.get("DEP");
		}

		// 解析编组16 目的机场和预计飞行总时间 备降站
		Map<String, String> parseG16 = parseG16(split[8]);
		String arrivalAirport = parseG16.get("arrivalAirport");
		if ("未知".equals(arrivalAirport) || arrivalAirport == null) {
			arrivalAirport = parseG18.get("DEST");
		}
		// 预计飞行时间
		String flyTime = parseG16.get("flyTime");

		// 备降站1
		String alternate1 = parseG16.get("alternate1");
		if (alternate1 == null || "未知".equals(alternate1)) {
			alternate1 = parseG18.get("ALTN");
		}
		// 备降站2
		String alternate2 = parseG16.get("alternate2");
		if (alternate2 == null || "未知".equals(alternate2)) {
			alternate2 = parseG18.get("ALTN");
		}

		/** 共有信息 任务 机型 航线 **/
		String task = parseG8(split[3]);
		String planeType = parseG9(split[4]);
		if (planeType.equals("ZZZZ")) {
			planeType = parseG18.get("TYP");
		}
		String flightLine = departureAirport + "-" + arrivalAirport;
		/** 以下信息需要根据 起飞机场 目的机场的不同具体判断 **/
		String incomingFlyNo = null;
		String departureFlyNo = null;
		/** 如果该条报文是无关报文 则只确定航班号 不需要确定是进港 还是出港 **/
		String flyNo = null;
		// 本站计划起飞时间
		Long realFly = null;
		// 前站计划起飞时间
		Long preRealFly = null;
		// 备降站
		String alternate = null;
		// 1.出发地是长治
		if ("长治".equals(departureAirport)) {
			departureFlyNo = parseG7(split[2]);// 出发地是长治 则航班号代表出港航班号

			// 计算planedFly
			if (StringUtils.isNotBlank(departureAirportTime)) {
				// 需要判断其它信息里是否有DOF这一项的内容 如果有 则时间以DOF为准 如果没有 则时间为当天
				String dof = parseG18.get("DOF");
				if (dof != null) {
					realFly = DateUtils.string2FormattingLongByDOF(dof, departureAirportTime);
				} else {
					realFly = DateUtils.string2FormattingLong(departureAirportTime);
				}
			}
			// 拼接备降站
			alternate = jointAlternate(alternate1, alternate2);

		} else if ("长治".equals(arrivalAirport)) {
			incomingFlyNo = parseG7(split[2]);// 目的地是长治 则航班号代表进港航班号
			if (StringUtils.isNotBlank(departureAirportTime)) {
				// 需要判断其它信息里是否有DOF这一项的内容 如果有 则时间以DOF为准 如果没有 则时间为当天
				String dof = parseG18.get("DOF");
				if (dof != null) {
					preRealFly = DateUtils.string2FormattingLongByDOF(dof, departureAirportTime);
				} else {
					preRealFly = DateUtils.string2FormattingLong(departureAirportTime);
				}
			}
			// 拼接备降站
			alternate = jointAlternate(alternate1, alternate2);
		} else if (!"长治".equals(departureAirport) && !"长治".equals(arrivalAirport) && !"长治".equals(alternate1)
				&& !"长治".equals(alternate2)) {
			// 与长治无关的报文
			flyNo = parseG7(split[2]);
			alternate = jointAlternate(alternate1, alternate2);
		} else {// 备降站是长治
			incomingFlyNo = split[2];// 备降站是长治 则航班号代表进港航班号
			alternate = "长治";
		}
		// 将解析的数据封装成JSON
		JSONObject json = new JSONObject();
		json.put("messageType", "FPL");
		json.put("task", task);
		if (planeType != null) {
			json.put("planeType", planeType);
		}
		if (flightLine != null) {
			json.put("flightLine", flightLine);
		}
		if (incomingFlyNo != null) {
			json.put("incomingFlyNo", incomingFlyNo);
		}
		if (departureFlyNo != null) {
			json.put("departureFlyNo", departureFlyNo);
		}
		if (preRealFly != null) {
			json.put("preRealFly", preRealFly);
		}
		if (realFly != null) {
			json.put("realFly", realFly);
		}
		if (alternate != null) {
			json.put("alternate", alternate);
		}
		if (flyNo != null) {
			json.put("flyNo", flyNo);
		}
		return json.toJSONString();
	}

	// 拼接备降站
	private static String jointAlternate(String alternate1, String alternate2) {
		String alternate = null;
		if (alternate1 != null && alternate2 == null) {
			alternate = alternate1;
		} else if (alternate1 != null && alternate2 != null) {
			alternate = alternate1 + "," + alternate2;
		} else if (alternate1 == null && alternate2 != null) {
			alternate = alternate2;
		}
		return alternate;
	}

	// 解析编组18 其它情报
	private static Map<String, String> parseG18(String otherMessage) {
		String[] otherMessageArray = otherMessage.split(" ");
		Map<String, String> map = new HashMap<>();
		for (int i = 0; otherMessageArray != null && i < otherMessageArray.length; ++i) {
			String str = otherMessageArray[i];
			if (str.startsWith("DEP") || str.startsWith("DEST") || str.startsWith("DOF") || str.startsWith("TYP")
					|| str.startsWith("ALTN")) {
				int indexOf = str.indexOf("/");
				String key = str.substring(0, indexOf);
				String value = str.substring(indexOf + 1, str.length());
				map.put(key, value);
			}
		}
		return map;
	}

	// 解析编组7 航班号
	private static String parseG7(String flyNo) {
		String newFlyNo = flyNo.split("/")[0];
		//String oldIncomingFlyNo = flyDynamic.getIncomingFlyNo();
		if(newFlyNo!=null) {
			String szm = newFlyNo.substring(0, 3);
			String ezm = flyNoMap.get(szm);
			if(ezm!=null) {
				newFlyNo = newFlyNo.replace(szm, ezm);
			}
		}
		return newFlyNo;
	}

	// 解析编组13 起飞机场和时间
	private static String[] parseG13(String msg) {
		// msg可能是 ZBCZ 也可能是 ZBCZ0810
		if (msg.length() == 8) {
			String[] arr = { addressMap.get(msg.substring(0, 4)), msg.substring(4, 8) };
			return arr;
		} else {
			String[] arr = { addressMap.get(msg), "" };
			return arr;
		}
	}

	// 解析编组16 目的地机场和预计飞行总时间 备降站
	private static Map<String, String> parseG16(String msg) {
		/**
		 * msg可能的格式 ZBCZ ZBCZ0202 ZBCZ0202 备降站1 ZBCZ0202 备降站1 备降站2 ZBCZ 备降站1
		 * ZBCZ 备降站1 备降站2
		 */
		Map<String, String> map = new HashMap<String, String>();
		String[] split = msg.split(" ");
		if (split.length == 1) {
			if (split[0].length() == 4) {
				map.put("arrivalAirport", addressMap.get(split[0]));
				return map;
			} else {
				map.put("arrivalAirport", addressMap.get(split[0].substring(0, 4)));
				map.put("flyTime", split[0].substring(4, 8));
				return map;
			}
		} else if (split.length == 2) {// 只有一个备降站的情况
			if (split[0].length() == 4) {
				map.put("arrivalAirport", addressMap.get(split[0]));
				map.put("alternate1", addressMap.get(split[1]));
				return map;
			} else {
				map.put("arrivalAirport", addressMap.get(split[0].substring(0, 4)));
				map.put("flyTime", split[0].substring(4, 8));
				map.put("alternate1", addressMap.get(split[1]));
				return map;
			}
		} else {// 两个备降站的情况
			if (split[0].length() == 4) {
				map.put("arrivalAirport", addressMap.get(split[0]));
				map.put("alternate1", addressMap.get(split[1]));
				map.put("alternate2", addressMap.get(split[2]));
				return map;
			} else {
				map.put("arrivalAirport", addressMap.get(split[0].substring(0, 4)));
				map.put("flyTime", split[0].substring(4, 8));
				map.put("alternate1", addressMap.get(split[1]));
				map.put("alternate2", addressMap.get(split[2]));
				return map;
			}
		}
	}

	// 解析编组17 实际降落机场和时间
	private static String[] parseG17(String msg) {
		if (msg.length() == 8) {
			String[] arr = { addressMap.get(msg.substring(0, 4)), msg.substring(4, 8) };
			return arr;
		} else {
			String[] arr = { addressMap.get(msg), "" };
			return arr;
		}
	}

	// 解析编组8 飞行规则及种类
	private static String parseG8(String msg) {
		return taskMap.get(msg.substring(1, 2));
	}

	// 解析编组9 航空器数目 机型 尾流等级
	private static String parseG9(String msg) {
		return msg.split("/")[0];
	}

	// 解析编组22 修订信息
	private static Map<String, String> parseG22(String[] split) {
		Map<String, String> map = new HashMap<String, String>();
		String task = null;
		String planeType = null;
		String arrivalAirport = null;
		String flyTime = null;
		String alternate1 = null;
		String alternate2 = null;
		String dep = null;
		String dest = null;
		String altn = null;
		String dof = null;
		String typ = null;
		Map<String, String> parseG18 = null;
		// split[5]开始是编组22的内容 个数不固定
		for (int i = 5; i < split.length; ++i) {
			String str = split[i];
			String g = str.split("/")[0];// 获得要修改的编组
			switch (g) {
			case "8":
				// 飞行规则及任务
				task = parseG8(str.split("/")[1]);
				break;
			case "9":
				// 机型
				planeType = str.split("/")[1];
				break;
			case "10":
				// 机载设备与能力 忽略
				break;
			case "15":
				// 航路 忽略
				break;
			case "16":
				// 目的地和机场
				Map<String, String> parseG16 = parseG16(str.split("/")[1]);
				if (parseG16 != null) {
					arrivalAirport = parseG16.get("arrivalAirport");
					flyTime = parseG16.get("flyTime");
					alternate1 = parseG16.get("alternate1");
					alternate2 = parseG16.get("alternate2");
				}
				break;
			case "18":
				// 其它情报
				parseG18  = parseG18(str.split("/")[1]);
				if(parseG18!=null) {
					dep = parseG18.get("DEP");
					dest = parseG18.get("DEST");
					typ = parseG18.get("TYP");
					altn = parseG18.get("ALTN");
					dof = parseG18.get("DOF");	
				}
				break;
			}
		}
		
		if (task != null) {
			map.put("task", task);
		}
		if (planeType != null) {
			map.put("planeType", planeType);
		}
		if (arrivalAirport != null) {
			map.put("arrivalAirport", arrivalAirport);
		}
		if(flyTime!=null) {
			map.put("flyTime", flyTime);
		}
		if(alternate1!=null) {
			map.put("alternate1", alternate1);
		}
		if(alternate2!=null) {
			map.put("alternate2", alternate2);
		}
		if(dep!=null) {
			map.put("DEP", dep);
		}
		if(dest!=null) {
			map.put("DEST", dest);
		}
		if(dof!=null) {
			map.put("DOF", dof);
		}
		if(typ!=null) {
			map.put("TYP", typ);
		}
		if(altn!=null) {
			map.put("ALTN", altn);
		}
		return map;
	}

	// 解析编组14 预计飞跃边界数据
	private static String parseG14(String msg) {
		if(msg.contains("/")) {
			return msg.split("/")[1].substring(0, 3);
		}else {
			return msg.substring(2,6);
		}
	}

}
