package org.lq.aiitc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;

import javax.persistence.EntityManagerFactory;
import java.awt.*;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.TimeZone;

@SpringBootApplication
@EnableAutoConfiguration
public class CmsApplication {
	private static final Logger LOGGER= LoggerFactory.getLogger(CmsApplication.class);
	public static void main(String[] args) {
		final TimeZone zone = TimeZone.getTimeZone("GMT+8"); //获取中国时区
		TimeZone.setDefault(zone); //设置时区
		LOGGER.info("event=Set-Default-TimeZone zone={} charset={}",TimeZone.getDefault(),Charset.defaultCharset());

		System.setProperty("file.encoding","UTF-8");
		Field charset = null;
		try {
			charset = Charset.class.getDeclaredField("defaultCharset");
			charset.setAccessible(true);
			charset.set(null,null);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			LOGGER.error("event=char-set-exception ",e);
		}
		LOGGER.info("event=Set-Default-TimeZone zone={} charset={}",TimeZone.getDefault(),Charset.defaultCharset());

		GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
		for(String fontName : e.getAvailableFontFamilyNames()){
			LOGGER.info("event=sys-font-name v={}",fontName);
		}

		SpringApplication.run(CmsApplication.class, args);

	}

	@Bean
	public HibernateJpaSessionFactoryBean sessionFactory(EntityManagerFactory emf) {
		HibernateJpaSessionFactoryBean fact = new HibernateJpaSessionFactoryBean();
		fact.setEntityManagerFactory(emf);
		return fact;
	}
}
