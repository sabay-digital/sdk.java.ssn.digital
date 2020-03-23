package digital.ssn.java.sdk;

import java.io.IOException;
import com.google.gson.*;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;

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

public class CreatePayment {
    // CreatePayment sends transaction information to the SSN API to build an XDR envelope
    public static String CreatePayment(String from, String to, String amount, String assetCode, String assetIssuer, String memo, String api) {
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
}