# Money transfers

The RESTful application for processing money transfers between accounts.

The application supports the following operations with bank accounts:
* Create a new account by the provided `firstName` and `lastName`
* Deposit money to the account by the provided `accountId` and `amount`
* Withdraw money from the account by the provided `accountId` and `amount`
* Transfer money from one account to another
* Get information about the existing bank account by `accountId`

The application is supposed to be thread-safe and lock-free unless the `AtomicStorage` implementation breaks this invariant.

For the sake of simplicity I've chosen `ConcurrentHashMap` as an implementation of `AtomicStorage`. This approach is definitely not an ideal one and has a bunch of issues. For example, it is not scalable (though, because the task is to implement everything in-memory, that's a still valid implementation I think).

To make the current implementation scalable, you just need to implement a new `AtomicStorage`, which supports scaling. For example, H2 database can be used (which is actually both in-memory and scalable), but it didn't fit easily in the current implementation, so I decided to go with `ConcurrentHashMap` for now and just explain it here, in readme.

Also, one more thing I should note is that the current implementation of bank transfers is a bit not real-world applicable: it executes all requests eagerly, not lazily, and thus under high load it might take quite long time to wait for the API response. Because I noted it quite late and because this implementation is still lock-free and should work, I decided to leave it, but in the meanwhile I also added a demo implementation of lazily approach (which can be found in `service/deferred` package), which uses lock-free queue to store all requests and executes them in background one by one. In this case API will return response immediately for each request, and there will be a separate endpoint to see the current status of the transaction.

You can also find build, run and REST API instructions below.

## Build

```bash
mvn clean install enforcer:enforce
```

## Run

If you've built the application using Maven, run:
```
java -jar money-transfers-api/target/money-transfers-api-1.0-SNAPSHOT.jar
```
Or you can just run already built jar:
```
java -jar money-transfers.jar
```

## REST API description

I didn't have time to understand how to add support for Swagger/OpenAPI to Ktor, because it seems that it doesn't support them natively, so I will describe API specification below.

The current implementation of API supports the following endpoints:

* `/accounts`:
    * `POST`
        * expects `{"firstName": <first name>, "lastName": <last name>}` in body
        * responses with `HTTP 201 Created` and location for GET request
    * `GET /<account_id>`
        * responses with the current information about the account
        * response includes `accountId`, `firstName`, `lastName`, `amount`
* `/operations`:
    * `POST /deposit`
        * expects `{"to": <account_id>, "amount": <amount>}` in body
        * deposits `amount` money to the provided account
        * responses with `{"accountId": <account_id>, "totalAmount": <total_amount>}` in body, where `totalAmount` is the current total amount of money for the account
    * `POST /withdraw`
        * expects `{"from": <account_id>, "amount": <amount>}` in body
        * proceeds withdrawal of `amount` money from the provided account
        * returns `HTTP Bad Request 400` in case there is not enough money on the account.
        * otherwise, responses with `{"accountId": <account_id>, "totalAmount": <total_amount>}` in body
    * `POST /transfer`
        * expects `{"from": <account_id>, "to": <account_id>, "amount": <amount>}` in body
        * transfers `amount` money from the `from` account to the `to` account
        * in case of failure (not enough money on `from` account) returns `HTTP Bad Request 400`
        * otherwise, returns `HTTP 200` and nothing in the body

## Testing

I have written a bunch of unit and component tests for different packages and classes, the coverage is not 100%, but still quite high.

Also I have written a stress test for service to check that it works correctly in multithreading scenario.
