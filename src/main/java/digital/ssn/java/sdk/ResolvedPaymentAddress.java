package digital.ssn.java.sdk;

import java.util.List;
import java.util.HashMap;
import java.io.IOException;
import com.google.gson.*;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResolvedPaymentAddress {
    // ResolvedPaymentAddress describes the JSON structure for the response from the payment address resolver API
    public String network_address;
    public String payment_type;
    public String service_name;
    public ResolverResponseDetails details;
    public int status;
    public String title;

    private static final Logger LOGGER = LoggerFactory.getLogger(ResolvedPaymentAddress.class);

    // ResolvePA sends a payment address to the PA service for resolving. The resolverURL should be a resolver that supports the V2 design with timeOut 20s
    public static ResolvedPaymentAddress ResolvePA(String paymentAddress, String hash, String signature, String ssnAcc, String resolverURL) throws IOException {
        return ResolvePA(paymentAddress, hash, signature, ssnAcc, resolverURL, 20000);
    }

    // ResolvePA sends a payment address to the PA service for resolving. The resolverURL should be a resolver that supports the V2 design with dynamic timeOut
    public static ResolvedPaymentAddress ResolvePA(String paymentAddress, String hash, String signature, String ssnAcc, String resolverURL, Integer timeOut) throws IOException {
        // Load HTTP Client for requests
        HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory();
        // Gson parser
        Gson gson = new Gson();

        // Prepare request
        GenericUrl paReqURL = new GenericUrl(resolverURL+"/resolve/"+paymentAddress);
        ResolverRequest reqBody = new ResolverRequest(hash, signature, ssnAcc, ssnAcc);
        LOGGER.info("ResolvePaymentAddress request body : {} ", gson.toJson(reqBody));

        // Make the request
        String paBody = "";
        try {
            HttpRequest paReq = requestFactory.buildPostRequest(paReqURL, ByteArrayContent.fromString(null,gson.toJson(reqBody)));
            paReq.getHeaders().setContentType("application/json");
            paReq.setWriteTimeout(timeOut);
            paReq.setConnectTimeout(timeOut);
            HttpResponse paResp = paReq.execute();
            try {
                paBody = paResp.parseAsString();
                LOGGER.info("ResolvePaymentAddress response body : {} ", paBody);
            } finally {
                paResp.disconnect();
            }
        } catch (IOException e) {
            LOGGER.error("Error : {} ", e);
            throw e;
        }

        return gson.fromJson(paBody, ResolvedPaymentAddress.class);
    }

    public String getPaymentInfo() {
        return details.payment_info;
    }

    public String getMemo() {
        return details.memo;
    }

    public HashMap<String, String> getPaymentArray() {
        HashMap<String, String> map = new HashMap<String, String>();

        for (int i = 0; i < details.payment.size(); i++) {
            map.put(details.payment.get(i).asset_code, details.payment.get(i).amount);
        }
        return map;
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