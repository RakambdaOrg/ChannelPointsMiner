package fr.raksrinana.channelpointsminer.factory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionIdFactory{
	private static final char[] HEX_CHARS = {
			'0',
			'1',
			'2',
			'3',
			'4',
			'5',
			'6',
			'7',
			'8',
			'9',
			'a',
			'b',
			'c',
			'd',
			'e',
			'f'
	};
	
	public static String create(){
		return RandomStringUtils.random(32, HEX_CHARS);
	}
}
