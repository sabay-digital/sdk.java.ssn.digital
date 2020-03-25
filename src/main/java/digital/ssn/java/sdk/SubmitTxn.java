package digital.ssn.java.sdk;

import java.io.IOException;
import com.google.gson.*;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;

// SubmitTransactionRequest describes the JSON structure for making a request to the submit transaction API
class SubmitTransactionRequest {
    private String tx;

    SubmitTransactionRequest(String t) {
        tx = t;
    }
}

// SubmitTransactionResponse describes the JSON structure for the response from the submit transaction API
class SubmitTransactionResponse {
    public String hash;
    public int ldeger;
    public String envelope_xdr;
    public String result_xdr;
    public String result_meta_xdr;
    public String type;
    public int status;
    public String title;
    public String detail;
}

public class SubmitTxn {
    // SubmitTxn takes a base64 encoded XDR envelope and submits it to the network via provided API
    public static String SubmitTxn(String xdr, String api) {
        // Load HTTP Client for requests
        HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory();
        // Gson parser
        Gson gson = new Gson();

        // Prepare request
        GenericUrl stReqURL = new GenericUrl(api+"/transactions");
        SubmitTransactionRequest reqBody = new SubmitTransactionRequest(xdr);

        // Make the request
        String stBody = "";
        try {
            HttpRequest stReq = requestFactory.buildPostRequest(stReqURL, ByteArrayContent.fromString(null,gson.toJson(reqBody)));
            stReq.getHeaders().setContentType("application/json");
            HttpResponse stResp = stReq.execute();
            try {
                stBody = stResp.parseAsString();
            } finally {
                stResp.disconnect();
            }
        } catch (IOException e) {
            System.out.println(e);
        }

        CreatePaymentResponse resp = gson.fromJson(stBody, CreatePaymentResponse.class);
        return resp.envelope_xdr;
    }
}