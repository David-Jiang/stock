package com.stock.service;

import java.util.Locale;

import javax.annotation.Resource;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class LocaleMessageSourceService {

	@Resource
	private MessageSource messageSource;

	public String getMessage(final String code) {
		Locale locale = LocaleContextHolder.getLocale();
		return messageSource.getMessage(code, null, locale);
	}
	
	/** example: new Locale("en", "US") */
	public String getMessage(final String code, final Locale locale) {
		return messageSource.getMessage(code, null, locale);
	}
}
