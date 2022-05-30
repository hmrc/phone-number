
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

sm --start CIP_PHONE_NUMBER_VALIDATION_ALL

Run the services against the current versions in dev, stop the CIP_PHONE_NUMBER service and start manually

    sm --start CIP_PHONE_NUMBER_VALIDATION_ALL -f
    sm --stop CIP_PHONE_NUMBER
    cd cip-phone-number
    sbt 'run 6081'

For reference here are the details for running each of the services individually

    cd cip-phone-number-frontend
    sbt 'run 6080'
 
    cd cip-phone-number
    sbt 'run 6081'

    cd cip-phone-number-validation
    sbt 'run 6082'

#### Example query
```
curl --request POST \
  --url http://localhost:9000/customer-insight-platform/phone-number/validate-format \
  --header 'content-type: application/json' \
  --data '{"phoneNumber" : "07843274323"}'
```
### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").


