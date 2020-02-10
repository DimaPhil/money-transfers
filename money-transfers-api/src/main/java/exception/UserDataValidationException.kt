package exception

import java.lang.RuntimeException

class UserDataValidationException(message: String) : RuntimeException(message)
