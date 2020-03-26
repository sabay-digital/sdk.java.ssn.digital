package digital.ssn.java.sdk;

import java.util.List;
import java.io.IOException;
import com.google.gson.*;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;

public class ResolvedPaymentAddress {
    // ResolvedPaymentAddress describes the JSON structure for the response from the payment address resolver API
    public String network_address;
    public String payment_type;
    public String service_name;
    public ResolverResponseDetails details;
    public int status;
    public String title;

    // ResolvePA sends a payment address to the PA service for resolving. The resolverURL should be a resolver that supports the V2 design
    public static ResolvedPaymentAddress ResolvePA(String paymentAddress, String hash, String signature, String ssnAcc, String resolverURL) {
        // Load HTTP Client for requests
        HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory();
        // Gson parser
        Gson gson = new Gson();

        // Prepare request
        GenericUrl paReqURL = new GenericUrl(resolverURL+"/resolve/"+paymentAddress);
        ResolverRequest reqBody = new ResolverRequest(hash, signature, ssnAcc, ssnAcc);

        // Make the request
        String paBody = "";
        try {
            HttpRequest paReq = requestFactory.buildPostRequest(paReqURL, ByteArrayContent.fromString(null,gson.toJson(reqBody)));
            paReq.getHeaders().setContentType("application/json");
            HttpResponse paResp = paReq.execute();
            try {
                paBody = paResp.parseAsString();
            } finally {
                paResp.disconnect();
            }
        } catch (IOException e) {
            System.out.println(e);
        }

        return gson.fromJson(paBody, ResolvedPaymentAddress.class);
    }

    public String getPaymentInfo() {
        return details.payment_info;
    }
}

// ResolverResponseDetails describes the JSON structure for the nested details part of the response from the payment address resolver API
class ResolverResponseDetails {
    public String payment_info;
    public String memo;
    public List<ResolverPaymentDetails> payment;
    public List<ResolverPaymentDetails> service_fee;
}

// ResolverPaymentDetails describes the JSON structure for the nested payment details part of the response from the payment address resolver API
class ResolverPaymentDetails {
    public String amount;
    public String asset_code;
}

// ResolverRequest describes the JSON structure for making a request to the payment address resolver V2 API.
class ResolverRequest {
    private String hash;
    private String signature;
    private String signer;
    private String ssn_account;
    
    ResolverRequest(String h, String sig, String signerKey, String ssnAcc) {
        hash = h;
        signature = sig;
        signer = signerKey;
        ssn_account = ssnAcc;
    }
}