
## phone-number

### Summary

Proxy/Forwarder server for phone-number services

The default port for phone-number-frontend is 6080
The default port for phone-number is port 6081
The default port for phone-number-validation is port 6082
The default port for phone-number-verification is port 6083
The default port for phone-number-stubs is port 6099

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
    cd phone-number
    sbt run

For reference here are the details for running each of the services individually

    cd phone-number-frontend
    sbt run
 
    cd phone-number
    sbt run

    cd phone-number-verification
    sbt run

### Curl microservice (for curl microservice build jobs)

#### Verify

    -XPOST -H "Content-type: application/json" -H "Authorization: k8ZPlzVV-aZ8n8qjiTIEca2Eey00LCz0QyjdADVg78rOv37Min82skYG7veJtzYtKTtmVFay1" -d '{
	    "phoneNumber": "<phone-number>"
    }' 'https://phone-number.protected.mdtp/customer-insight-platform/phone-number/verify'

#### Check notification status

    -XGET -H "Content-type: application/json" -H "Authorization: k8ZPlzVV-aZ8n8qjiTIEca2Eey00LCz0QyjdADVg78rOv37Min82skYG7veJtzYtKTtmVFay1"
    'https://phone-number.protected.mdtp/customer-insight-platform/phone-number/notifications/<notificationId>'

#### Verify passcode

    -XPOST -H "Content-type: application/json" -H "Authorization: k8ZPlzVV-aZ8n8qjiTIEca2Eey00LCz0QyjdADVg78rOv37Min82skYG7veJtzYtKTtmVFay1" -d '{
	    "phoneNumber": "<phone-number>",
        "passcode": "<passcode>"
    }' 'https://phone-number.protected.mdtp/customer-insight-platform/phone-number/verify/passcode'

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
