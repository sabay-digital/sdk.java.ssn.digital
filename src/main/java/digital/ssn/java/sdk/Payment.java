package digital.ssn.java.sdk;

import java.io.IOException;
import com.google.gson.*;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;

public class Payment {
    // Should implement a constructor here so you don't need to keep passing params

    // Create sends transaction information to the SSN API to build an XDR envelope
    public static String Create(String from, String to, String amount, String assetCode, String assetIssuer, String memo, String api) {
        // Load HTTP Client for requests
        HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory();
        // Gson parser
        Gson gson = new Gson();

        // Prepare request
        GenericUrl cpReqURL = new GenericUrl(api+"/create/transaction");
        CreatePaymentRequest reqBody = new CreatePaymentRequest(from, to, amount, assetCode, assetIssuer, memo);

        // Make the request
        String cpBody = "";
        try {
            HttpRequest cpReq = requestFactory.buildPostRequest(cpReqURL, ByteArrayContent.fromString(null,gson.toJson(reqBody)));
            cpReq.getHeaders().setContentType("application/json");
            HttpResponse cpResp = cpReq.execute();
            try {
                cpBody = cpResp.parseAsString();
            } finally {
                cpResp.disconnect();
            }
        } catch (IOException e) {
            System.out.println(e);
        }

        CreatePaymentResponse resp = gson.fromJson(cpBody, CreatePaymentResponse.class);
        return resp.envelope_xdr;
    }

    // Submit takes a base64 encoded XDR envelope and submits it to the network via provided API
    public static String Submit(String xdr, String api) {
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

// CreatePaymentRequest describes the JSON structure for making a request to the create payment API
class CreatePaymentRequest {
    private String from;
    private String to;
    private String amount;
    private String asset_code;
    private String asset_issuer;
    private String memo;

    CreatePaymentRequest(String f, String t, String amt, String assetCode, String assetIssuer, String m) {
        from = f;
        to = t;
        amount = amt;
        asset_code = assetCode;
        asset_issuer = assetIssuer;
        memo = m;
    }
}

// CreatePaymentResponse describes the JSON structure for the response from the create payment API
class CreatePaymentResponse {
    public int status;
    public String title;
    public String envelope_xdr;
}

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