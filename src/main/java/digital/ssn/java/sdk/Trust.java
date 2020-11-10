package digital.ssn.java.sdk;

import java.io.IOException;
import com.google.gson.*;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Trust {

    private static final Logger LOGGER = LoggerFactory.getLogger(Trust.class);

    // Verify checks whether the provided asset and assetIssuer is trusted by destination with timeOut 20s
    public static boolean Verify(String destination, String asset, String assetIssuer, String api) throws IOException {
        return Verify(destination, asset, assetIssuer, api, 20000);
    }

    // Verify checks whether the provided asset and assetIssuer is trusted by destination with dynamic timeOut
    public static boolean Verify(String destination, String asset, String assetIssuer, String api, Integer timeOut) throws IOException {
        // Load HTTP Client for requests
        HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory();
        // Gson parser
        Gson gson = new Gson();
        boolean isVerify = false;

        // Prepare request
        GenericUrl vtReqURL = new GenericUrl(api+"/verify/trust");
        VerifyTrustRequest reqBody = new VerifyTrustRequest(destination, asset, assetIssuer);
        LOGGER.info("Trust-Verify request body : {} ", gson.toJson(reqBody));

        // Make the request
        try {
            HttpRequest vtReq = requestFactory.buildPostRequest(vtReqURL, ByteArrayContent.fromString(null,gson.toJson(reqBody)));
            vtReq.getHeaders().setContentType("application/json");
            vtReq.setWriteTimeout(timeOut);
            vtReq.setConnectTimeout(timeOut);
            HttpResponse vtResp = vtReq.execute();
            try {
                if (vtResp.getStatusCode() == 200) {
                    isVerify = true;
                }
            } finally {
                vtResp.disconnect();
            }
        } catch (IOException e) {
            LOGGER.error("Error : {} ", e);
            throw e;
        }
        LOGGER.info("Trust-Verify response body : {} ", isVerify);
        return isVerify;
    }
}

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
