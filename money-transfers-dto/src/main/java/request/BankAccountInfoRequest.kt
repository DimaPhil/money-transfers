package request

/**
 * The request for retrieving the total amount of money available for the user account.
 *
 * @param accountId The user account identifier.
 */
data class BankAccountInfoRequest(
    val accountId: Long
)
