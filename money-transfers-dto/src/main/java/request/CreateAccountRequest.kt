package request

/**
 * Request for creating a new user bank account. A new account is created by the provided first name and the last name.
 *
 * @param firstName User first name.
 * @param lastName User last name.
 */
data class CreateAccountRequest(
    val firstName: String,
    val lastName: String
)
