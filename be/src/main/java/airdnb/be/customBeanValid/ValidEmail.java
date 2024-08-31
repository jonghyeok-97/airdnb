package airdnb.be.customBeanValid;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ValidEmailValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ValidEmail {

    String regexp() default "^[^@]+@[^@\\-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

/**
 * @Retention(RetentionPolicy.RUNTIME) - 어노테이션 유지 정책은 컴파일 후에도 남아 있으며, 런타임 시점에 반영
 * @Target(ElementType.FIELD) - 어노테이션 적용 대상을 필드로 제한
 *
 * 모든 이메일에 인증 메일을 전송하지 않게 하여 서버의 부하를 낮추고자 함.
 * Gmail 경우, 로컬에 + 에 포함 가능 -> 도메인 부분만 엄격하게 검증
 *
 * 로컬 : @ 문자를 제외한 모든 문자가 하나 이상 포함
 * @ : 전체 문자에서 단 하나
 * 도메인
 *       [^@\-] : @ 뒤에 @ 나 - 로 시작하지 않음
 *       [A-Za-z0-9-]+ :  알파벳, 숫자가 1개 이상 포함되어야 함
 *       (\.[A-Za-z0-9-]+)* : 위의 표현식이 . 을 포함해서 0번이상 반복될 수 있음
 *       (\.[A-Za-z]{2,})$ : 끝은 알파벳 2자리 이상으로 끝나야 함
 *
 */