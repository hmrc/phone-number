
## cip-phone-number

### Summary
Proxy/Forwarder server for cip phone-number services

- cip-phone-number-validation
- cip-phone-number-verification
- cip-phone-number-history
- cip-phone-number-insights

### Testing
#### Unit tests
`sbt clean test`

#### Integration tests
`sbt clean it:test`

### Running app

In order to run this microservice cip-phone-number you willl need to run the downstream services first. Then run 
`sbt clean run` and this will run on port 8080

#### Example query
```
curl --request POST \
  --url http://localhost:8080/customer-insight-platform/phone-number/validate-format \
  --header 'content-type: application/json' \
  --data '{"phoneNumber" : "07843274323"}'
```
### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

