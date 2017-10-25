package br.com.sysk.berkeleyQueue.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.sysk.berkeleyQueue.keyStrategy.KeyStrategy;
import br.com.sysk.berkeleyQueue.keyStrategy.LongStrategy;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Id {

	@SuppressWarnings("rawtypes")
	Class<? extends KeyStrategy> strategy() default LongStrategy.class;
}
