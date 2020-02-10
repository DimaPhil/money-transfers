package validator

import exception.UserDataValidationException

abstract class Validator<T> {
    private val validations: MutableList<Validation<T>> = mutableListOf()

    protected fun addValidation(validation: (T) -> Boolean, errorMessage: String) {
        validations += Validation(validation, errorMessage)
    }

    fun validate(objectToValidate: T) {
        validations.forEach { it.validate(objectToValidate) }
    }

    private class Validation<T>(val validationAction: (T) -> Boolean, val errorMessage: String) {
        fun validate(obj: T) {
            if (!validationAction(obj)) {
                throw UserDataValidationException(errorMessage)
            }
        }
    }
}
