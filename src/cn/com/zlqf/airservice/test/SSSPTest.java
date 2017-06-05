package cn.com.zlqf.airservice.test;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import cn.com.zlqf.airservice.entity.FlyInfo;
import redis.clients.jedis.Jedis;

public class SSSPTest {
	
	private ApplicationContext ac = null;
	{
		ac = new ClassPathXmlApplicationContext("applicationContext.xml");
	}
	
	@Test
	public void test() throws Exception{
		DataSource dataSource = ac.getBean(DataSource.class);
		System.out.println(dataSource.getConnection());
	}
	
	@Test
	public void StringUtilsTest() throws Exception{
		String msg = " ";
		boolean notBlank = StringUtils.isNotBlank(msg);
		boolean notEmpty = StringUtils.isNotEmpty(msg);
		System.out.println(notBlank);
		System.out.println(notEmpty);
	}
	
	@Test
	public void redisTest() throws Exception{
		RedisTemplate<String,List<FlyInfo>> redisTemplate = ac.getBean(RedisTemplate.class);
		ValueOperations<String,List<FlyInfo>> opsForValue = redisTemplate.opsForValue();
		/*List<FlyInfo> list = new ArrayList<>();
		FlyInfo f1 = new FlyInfo();
		f1.setId("001");
		f1.setDepartureFlyNo("CK445");
		f1.setDepartureProg("延误");
		FlyInfo f2 = new FlyInfo();
		f2.setId("001");
		f2.setDepartureFlyNo("KK115");
		f2.setDepartureProg("起飞");
		list.add(f1);
		list.add(f2);
		opsForValue.set("flyInfoList", list);
		System.out.println("OK");*/
		List<FlyInfo> list = opsForValue.get("flyInfoList");
		for(FlyInfo f : list) {
			System.out.println(f.getId() + "," + f.getIncomingFlyNo() + "," + f.getDepartureFlyNo() + "," + f.getRemark() + "," + f.getIncomingProg());
		}
	}
	
	@Test
	public void test2() throws Exception {
		String string = URLEncoder.encode("测试","UTF-8");
		System.out.println(string);
	}
	
	@Test
	public void redisTest2() {
		Jedis jedis = new Jedis("192.168.9.128",6379);
		String msgs = jedis.get("msgs");
		System.out.println(msgs);
		String[] split = msgs.split("\\|");
		//jedis.append("msgs", "hahahaha");
		//System.out.println(msgs);
		for(int i=0 ; i<split.length ; ++i) {
			System.out.println(split[i]);
		}
	}
	
	@Test
	public void testStr() {
		String str = "abcd|aaawz";
		String[] split = str.split("\\|");
		System.out.println(split.length);
	}
	
	@Test
	public void testDate() {
		System.out.println(System.currentTimeMillis());
	}
}
