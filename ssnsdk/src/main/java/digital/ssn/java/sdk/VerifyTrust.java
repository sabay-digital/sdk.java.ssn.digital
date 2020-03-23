package digital.ssn.java.sdk;

import java.io.IOException;
import com.google.gson.*;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;

// VerifyTrustRequest describes the JSON structure for making a request to the verify trust API
class VerifyTrustRequest {
    private String account;
    private String asset_code;
    private String asset_issuer;

    VerifyTrustRequest(String acc, String assetCode, String assetIssuer) {
        account = acc;
        asset_code = assetCode;
        asset_issuer = assetIssuer;
    }
}

// VerifyTrustResponse describes the JSON structure for the response from the verify trust API
class VerifyTrustResponse {
    public int status;
    public String title;
}

public class VerifyTrust {
    // VerifyTrust checks whether the provided asset and assetIssuer is trusted by destination
    public static boolean VerifyTrust(String destination, String asset, String assetIssuer, String api) {
        // Load HTTP Client for requests
        HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory();
        // Gson parser
        Gson gson = new Gson();

        // Prepare request
        GenericUrl vtReqURL = new GenericUrl(api+"/verify/trust");
        VerifyTrustRequest reqBody = new VerifyTrustRequest(destination, asset, assetIssuer);

        // Make the request
        try {
            HttpRequest vtReq = requestFactory.buildPostRequest(vtReqURL, ByteArrayContent.fromString(null,gson.toJson(reqBody)));
            vtReq.getHeaders().setContentType("application/json");
            HttpResponse vtResp = vtReq.execute();
            try {
                if (vtResp.getStatusCode() == 200) {
                    return true;
                }
            } finally {
                vtResp.disconnect();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        return false;
    }
}