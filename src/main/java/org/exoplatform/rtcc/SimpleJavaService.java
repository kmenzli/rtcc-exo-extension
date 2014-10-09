package org.exoplatform.rtcc;

import com.weemo.auth.WeemoException;
import com.weemo.auth.WeemoServerAuth;
import org.exoplatform.rtcc.util.ParameterFilter;

import com.sun.net.httpserver.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;

/**
 * Created by kmenzli on 09/10/2014.
 */
public class SimpleJavaService {
    public static void main(String[] args) throws Exception {

        // Enter below your path to certificate files, to Weemo Auth Server and your credentials
        final String CA_FILE        = "d:\\workspace\\intellij\\kmenzli\\rtcc-exo-extension\\src\\main\\resources\\cert\\authCA.crt";
        final String P12_FILE       = "d:\\workspace\\intellij\\kmenzli\\rtcc-exo-extension\\src\\main\\resources\\cert\\client.p12";
        final String AUTH_URL       = "https://auth.rtccloud.net/auth/";
        final String P12_PASS       = "XnyexbUF";
        final String CLIENT_ID      = "c16lmy11mj2ncwlgtvojxhr63fphsh";
        final String CLIENT_SECRET  = "p4ustyfijy9mna0xhl3rxox2hv280x";

        // Initialize WeemoServerAuth Object
        WeemoServerAuth auth = null;

        try {
            auth = new WeemoServerAuth(AUTH_URL, CA_FILE, P12_FILE, P12_PASS, CLIENT_ID, CLIENT_SECRET);
        }
        catch (WeemoException e) {
            e.printStackTrace();
            return ;
        }

        // Start a simple Web Server
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        HttpContext context = server.createContext("/gettoken", new MyHandler(auth));
        context.getFilters().add(new ParameterFilter());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Starting server on port " + port);

    }

    static class MyHandler implements HttpHandler {

        WeemoServerAuth auth = null;

        public MyHandler(WeemoServerAuth wauth) {
            auth = wauth;
        }

        public void handle(HttpExchange t) throws IOException {

            // The JSON response we will send back
            String response;

            // Extract Query and Post paramters and place in a single dict called 'params'
            Map params = (Map)t.getAttribute("parameters");
            System.out.println("Parameters:" + params);

            // Determine UID of caller.  Return an error if not a valid user.
            String uid = (String) params.get("uid");

            //String domain = "yourdomain.com";         // group of users
            String domain = "domain2";         // group of usersdomain2
            //String profile = "premium";               // premium profile
            String profile = "standard";               // premium profile

            if (uid == null) {
                System.out.println("No UID found in request");
                response = "{ \"error\" : \"UID\", \"error_description\" : \"No UID found in request\" }";
            }
            else {

                try {
                    String authToken = auth.getAuthToken(uid, domain, profile);
                    System.out.println("AuthToken:" + authToken);
                    response = authToken;
                }
                catch (WeemoException e) {
                    e.printStackTrace();
                    response = "{ \"error\" : \"AUTH\", \"error_description\" : \"An error occurred\" }";
                }

                System.out.println("Response:" + response);
            }

            // add the required response header for JSON
            Headers h = t.getResponseHeaders();
            h.add("Content-Type", "application/json");
            h.add("Access-Control-Allow-Origin", "*");

            // write the response body
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
