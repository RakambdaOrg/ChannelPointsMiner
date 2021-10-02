package fr.raksrinana.twitchminer.config;

public @interface Comment{
	/**
	 * Description of the field.
	 */
	String value();
	
	/**
	 * Default value of the field.
	 */
	String defaultValue() default "";
}
