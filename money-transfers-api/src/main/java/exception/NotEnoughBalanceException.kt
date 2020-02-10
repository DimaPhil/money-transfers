package exception

import java.lang.RuntimeException

class NotEnoughBalanceException(message: String) : RuntimeException(message)
