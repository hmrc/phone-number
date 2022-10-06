
## cip-phone-number

### Summary

Proxy/Forwarder server for cip phone-number services

The default port for cip-phone-number-frontend is 6080
The default port for cip-phone-number is port 6081
The default port for cip-phone-number-validation is port 6082
The default port for cip-phone-number-verification is port 6083
The default port for cip-phone-number-stubs is port 6099

### Testing

#### Unit tests
    sbt clean test

#### Integration tests
    sbt clean it:test

### Running app

    sm --start CIP_PHONE_NUMBER_ALL

Run the services against the current versions in dev, stop the CIP_PHONE_NUMBER service and start manually

    sm --start CIP_PHONE_NUMBER_ALL -r
    sm --stop CIP_PHONE_NUMBER
    cd cip-phone-number
    sbt run

For reference here are the details for running each of the services individually

    cd cip-phone-number-frontend
    sbt run
 
    cd cip-phone-number
    sbt run

    cd cip-phone-number-validation
    sbt run

    cd cip-phone-number-verification
    sbt run

### Curl microservice (for curl microservice build jobs)

#### Verify

    -XPOST -H "Content-type: application/json" -d '{
	    "phoneNumber": "<phone-number>"
    }' 'https://cip-phone-number.protected.mdtp/customer-insight-platform/phone-number/verify'

#### Check notification status

    -XGET -H "Content-type: application/json"
    'https://cip-phone-number.protected.mdtp/customer-insight-platform/phone-number/notifications/<notificationId>'

#### Verify Passcode

    -XPOST -H "Content-type: application/json" -d '{
	    "phoneNumber": "<phone-number>",
        "passcode": "<passcode>"
    }' 'https://cip-phone-number.protected.mdtp/customer-insight-platform/phone-number/verify/passcode'

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
