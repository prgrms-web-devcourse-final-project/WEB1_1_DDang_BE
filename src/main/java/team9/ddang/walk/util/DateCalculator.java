package team9.ddang.walk.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateCalculator {

    private DateCalculator() {}

    public static long calculateAgeFromNow(LocalDate birthDate){
        return ChronoUnit.YEARS.between(birthDate, LocalDate.now()) + 1;
    }
}
