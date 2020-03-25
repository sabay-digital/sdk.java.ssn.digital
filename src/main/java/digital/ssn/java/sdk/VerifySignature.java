package digital.ssn.java.sdk;

import java.io.IOException;
import com.google.gson.*;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;

// VerifySignatureRequest describes the JSON structure for making a request to the verify signature API
class VerifySignatureRequest {
    private String public_key;
    private String signature;
    private String message;

    VerifySignatureRequest(String mesg, String sig, String pk) {
        public_key = pk;
        signature = sig;
        message = mesg;
    }
}

// VerifySignatureResponse describes the JSON structure for the response from the verify signature API
class VerifySignatureResponse {
    public int status;
    public String title;
}

public class VerifySignature {
    // VerifySignature checks whether the provided message, signature and public key are valid
    public static boolean VerifySignature(String message, String signature, String publicKey, String api) {
        // Load HTTP Client for requests
        HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory();
        // Gson parser
        Gson gson = new Gson();

        // Prepare request
        GenericUrl vsReqURL = new GenericUrl(api+"/verify/signature");
        VerifySignatureRequest reqBody = new VerifySignatureRequest(message, signature, publicKey);

        // Make the request
        try {
            HttpRequest vsReq = requestFactory.buildPostRequest(vsReqURL, ByteArrayContent.fromString(null,gson.toJson(reqBody)));
            vsReq.getHeaders().setContentType("application/json");
            HttpResponse vsResp = vsReq.execute();
            try {
                if (vsResp.getStatusCode() == 200) {
                    return true;
                }
            } finally {
                vsResp.disconnect();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        return false;
    }
}