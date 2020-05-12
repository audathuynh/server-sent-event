curl -X PUT -H 'Content-Type: application/octet-stream'\
       	-H 'deliveryMode: 0'\
       	-H 'correlationId: correlationId'\
       	-H 'replyTo: a.replyTo'\
       	-H 'type: XMLMessage'\
       	-H 'header-Type: APP NAME CLASS'\
       	-H 'header-Version: MSG VERSION'\
        -H 'header-key1: value1'\
        -H 'header-key2: value2'\
       	-d @data.xml\
       	http://localhost:8080/rest/v0.1/publish?routing-key=a.b

