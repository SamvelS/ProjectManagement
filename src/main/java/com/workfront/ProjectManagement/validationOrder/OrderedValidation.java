package com.workfront.ProjectManagement.validationOrder;

import javax.validation.GroupSequence;

@GroupSequence({FirstOrder.class, SecondOrder.class, ThirdOrder.class})
public interface OrderedValidation {
}
