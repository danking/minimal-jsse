server-key.pem server-cert.pem:
	openssl req -x509 -newkey rsa:4096 -keyout server-key.pem -out server-cert.pem -days 365 -subj '/CN=localhost'

keystore.p12: server-key.pem server-cert.pem
	openssl pkcs12 -export -out keystore.p12 -inkey server-key.pem -in server-cert.pem

client-key.pem client-cert.pem:
	openssl req -x509 -newkey rsa:4096 -keyout client-key.pem -out client-cert.pem -days 365 -subj '/CN=localhost'

truststore.p12: client-key.pem client-cert.pem
	keytool -import -alias client-cert -file client-cert.pem -keystore truststore.p12

.PHONY: runserver
runserver: keystore.p12 truststore.p12
	scalac EchoServer.scala
	scala EchoServer

.PHONY: runclient
runclient: client-key.pem client-cert.pem server-cert.pem
	curl -vv --cacert server-cert.pem \
	   --key client-key.pem \
     --cert client-cert.pem \
     https://localhost:8080/a

clean:
	rm -rf server-cert.pem server-key.pem client-cert.pem client-key.pem keystore.p12 truststore.p12
