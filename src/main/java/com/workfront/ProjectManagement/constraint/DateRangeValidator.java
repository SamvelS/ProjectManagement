package com.workfront.ProjectManagement.constraint;

import org.apache.commons.beanutils.BeanUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class DateRangeValidator implements ConstraintValidator<DateRangeMatch, Object> {

    private String startDateFieldName;
    private String endDateFieldName;
    private String message;

    @Override
    public void initialize(DateRangeMatch constraintAnnotation) {
        startDateFieldName = constraintAnnotation.startDate();
        endDateFieldName = constraintAnnotation.endDate();
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        boolean valid = true;
        try
        {
            final Object firstObj = BeanUtils.getProperty(value, startDateFieldName);
            final Object secondObj = BeanUtils.getProperty(value, endDateFieldName);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            Date firstDate = simpleDateFormat.parse(firstObj.toString());
            Date secondDate = simpleDateFormat.parse(secondObj.toString());
            valid = firstDate.compareTo(secondDate) <= 0;
        }
        catch (final Exception ignore)
        {
            // ignore
        }

        if (!valid){
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(startDateFieldName)
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
        }

        return valid;
    }
}
