package cn.com.zlqf.airservice.test;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.com.zlqf.airservice.entity.Message;
import cn.com.zlqf.airservice.service.MessageService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class TestBatch {
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private MessageService messageService;
	
	@Test
	public void test() {
		messageService.testBatch(entityManager);
	}
}
