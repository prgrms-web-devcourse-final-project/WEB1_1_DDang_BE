package team9.ddang.global.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)  // 메서드에 적용
@Retention(RetentionPolicy.RUNTIME) // 런타임에 동작
public @interface ExtractEmail {
}
