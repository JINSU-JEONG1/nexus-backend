package com.nexus.backend.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * API 에러 정의
 *
 * @author jsjeong
 */
@Getter
@AllArgsConstructor
public enum ApiResponseError {

	NO_ERROR					("0000", "OK"),

	NO_AUTH_ERROR				("401", "권한이 없습니다."),

	ERROR_INVALID_LOGIN			("9201", "로그인정보가 유효하지 않습니다."),

	BAD_REQUEST					("9400", "잘못된 요청입니다."),
	NO_AUTH_TOKEN_EXPIRED		("9401", "토큰이 만료되었습니다."),
	NO_AUTH_TOKEN_INVALID		("9402", "토큰정보가 유효하지 않습니다."),
	ERROR_ACCESS_DENIED			("9403", "실행 권한이 없습니다."),
	NO_AUTH_TOKEN_BLACKLIST		("9404", "차단된 토큰입니다."),
	NO_AUTH_REFRESH_TOKEN_INVALID("9405", "토큰정보가 유효하지 않습니다."),
	NO_AUTH_PERMISSION_DENIED	("9406", "사용 권한이 없습니다."),
	ERROR_INVALID_JWT			("9407", "토큰값이 유효하지 않습니다."),
	ERROR_EXPIRED_JWT			("9408", "토큰값이 만료되었습니다."),
	ERROR_INVALID_ISS			("9409", "토큰값의 ISS가 유효하지 않습니다."),
	ERROR_INVALID_NO_JTI		("9410", "토큰값의 Jti 고유식별자가 누락되었습니다."),
	ERROR_INVALID_JTI			("9411", "토큰값의 Jti 고유식별자가 유효하지 않습니다."),

	NO_AUTH_NOT_FOUND_USER		("9412", "유효하지 않은 사용자 정보입니다."),

	ERROR_SERVER_ERROR			("9500", "서버 오류가 발생하였습니다."),
	ERROR_CALL_API				("9801", "API 호출오류."),

	ERROR_PARAMETERS			("9901", "파라미터 오류."),
	ERROR_NOT_SUPPORTED_METHOD	("9902", "지원하지 않는 Method 입니다."),
	ERROR_INTERNAL_API_PARAMETERS("9903", "내부 API 파라미터 오류."),

	ERROR_JSON					("9998", "시스템 내부오류 발생하였습니다.[json]"),
	ERROR_DEFAULT				("9999", "오류가 발생하였습니다.");

	private final String code;
	private final String message;

	public static boolean isOk(String code) {
		return ApiResponseError.NO_ERROR.code.equals(code);
	}

}
