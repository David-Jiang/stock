package com.stock.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsViewResolver;
import org.springframework.web.servlet.view.tiles3.TilesConfigurer;
import org.springframework.web.servlet.view.tiles3.TilesView;
import org.springframework.web.servlet.view.tiles3.TilesViewResolver;

@Configuration
@EnableWebMvc
public class WebMvcConfig extends WebMvcConfigurerAdapter {
	
	/** 存放css/js等在resources/static內*/
	@Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
	}
	
	/** 設置tiles*/
	@Bean
	public TilesConfigurer tilesConfigurer() {
		final TilesConfigurer configurer = new TilesConfigurer();
		configurer.setDefinitions(new String[] { "/WEB-INF/tiles.xml" });
		configurer.setCheckRefresh(true);
		return configurer;
	}
	
	/** 設置tiles*/
	@Bean
	public TilesViewResolver tilesViewResolver() {
		final TilesViewResolver resolver = new TilesViewResolver();
		resolver.setViewClass(TilesView.class);
		return resolver;
	}
	
	/** 設置i18n*/
	@Bean(name = "localeResolver")
    public LocaleResolver getLocaleResolver(){
        return new CookieLocaleResolver();
    }

	/** 設置i18n,url帶參數送lang=xx 切換語系*/
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
		lci.setParamName("lang");
		return lci;
	}
	
	/** 設置i18n*/
	@Override
	public void addInterceptors(final InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}
	
	/** 預設顯示pdf, filename=rpt_xxxx, ex: new ModelAndView("rpt_pdf", parameterMap);*/
	@Bean
	public JasperReportsViewResolver getJasperReportsViewResolver() {
	  JasperReportsViewResolver resolver = new JasperReportsViewResolver();
	  resolver.setPrefix("classpath:/reports/");
	  resolver.setSuffix(".jasper");
	  resolver.setReportDataKey("datasource");
	  resolver.setViewNames("rpt_*");
	  resolver.setViewClass(JasperReportsMultiFormatView.class);
	  resolver.setOrder(0);
	  return resolver;
	}
}
