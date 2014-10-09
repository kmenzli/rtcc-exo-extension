# Java Client for Weemo

The Java client for Weemo is a library that can be used on your server to communicate with Weemo back-end services.  Its primary purpose is to enable your server as an authentication provider for Weemo video calls.

The Java Client is distributed with a demonstration program you can use for testing purposes.

Requirements:

- Java version 1.6 or later.
- Java Compiler (javac) version 1.6 or later.

You must obtain Authentication Provider credentials from Weemo.


## Installing the Client

Place the ```WeemoServerAuth-1.0-java6.jar``` file in a path reachable by your server.  Place the SSL Certificates (.pem and .p12 files) in a directory that is readable by your server.


## Using the Example Server

The Java client library is distributed with an example sevice that
serves two purposes.  First, it can be used to test your credentials.
Secondly, it can be used to serve tokens to your Javascript or Mobile
clients if you have not yet completed your back-end.

To use the example server, configure ```SimpleJavaService.java``` with
the following pieces of information.

| parameter      | description |
|----------------|-------------|
| CA_FILE        | Path to the "weemo-ca.pem" file given to you by Weemo. |
| P12_FILE       | Path to the "client.p12" file give to you by Weemo. |
| P12_PASS       | The p12 "passphrase" given to you. |
| CLIENT_ID      | The 30 character "Client ID" given to you by Weemo. |
| CLIENT_SECRET  | The 30 character secret accompanying the ID above. |
| AUTH_URL       | "https://oauths.weemo.com/auth/" |

You will also need to set these two variables.

| parameter         | description |
|-------------------|-------------|
| identifier_client | A domain you registered with Weemo. Each domain identifies a group of users. |
| id_profile        | A profile you registered with Weemo.  This describes a user's capabilities. |

In a production system, you will probably look up the ```uid```, ```identifier_client``` and ```id_profile``` based on a user's session, or from a login and password.  In our example, the ```uid``` is a parameter and the others are constants.

Compile and run the simple server this way.

    javac -cp WeemoServerAuth-1.0-java6.jar SimpleJavaService.java util/ParameterFilter.java
    java -cp WeemoServerAuth-1.0-java6.jar:util:. SimpleJavaService

Note: on Windows, you should replace the ':' in the classpath with ';' as shown below.

    java -cp WeemoServerAuth-1.0-java6.jar;util;. SimpleJavaService

## Testing the Example Server

You can use curl to obtain a token for a user id this way.

    curl "http://localhost:8000/gettoken.php?uid=test99"

If everything is ok, you will find a JSON object as the response.

    {"token":"lnrsodluk3vmr1087nn1j83i51"}


## More details about the client

Add the external JAR ```WeemoServerAuth-1.0-java6.jar``` into your java project, and import weemo classes:

```
import com.weemo.auth.WeemoException;
import com.weemo.auth.WeemoServerAuth;
```

Your service needs only create one instance of an authentication client for the lifetime of the service.

```
 auth = new WeemoServerAuth(AUTH_URL, CA_FILE, P12_FILE, P12_PASS, CLIENT_ID, CLIENT_SECRET);
```

Obtain a token by calling the ```getAuthToken``` method on behalf of a particular user.

```
String authToken = auth.getAuthToken(UID, DOMAIN, PROFILE);
```

You should receive a JSON object as a reply with a ```token``` field.  If an error occurred, the HTTP status will be set to a 5xx value and the JSON object will contain an ```error``` field.



## Troubleshooting the Client Installation


The list below includes some error messages we have seen along with a diagnosis of their probable cause.

    error setting certificate verify locations:

The path name of WEEMO_CACERT is incorrect.

    This P12 not found on this server

The path name of the WEEMO_CLIENTP12 is incorrect.

    Unable to parse the p12 file.  Is this a .p12 file?  Is the password correct?

You have probably mistyped the WEEMO_CERTPASSWORD.

    [MULTITENANT] Api_account or Api_secret invalid for this PKCS12

You have successfully configured the SSL-related parameters, but there is a problem with your WEEMO_CLIENT_ID or WEEMO_CLIENT_SECRET.  Please double check.

    [MULTITENANT] Provider have not auto-prov domain functionality

You may have specified a domain ("identifier_client") that does not match your provisioning.  Please double check.
