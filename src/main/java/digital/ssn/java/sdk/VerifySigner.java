package digital.ssn.java.sdk;

import java.io.IOException;
import com.google.gson.*;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;

// VerifySignerRequest describes the JSON structure for making a request to the verify signer API
class VerifySignerRequest {
    private String signer;
    private String ssn_account;

    VerifySignerRequest(String signPK, String ssnPK) {
        signer = signPK;
        ssn_account = ssnPK;
    }
}

// VerifySignerResponse describes the JSON structure for the response from the verify signer API
class VerifySignerResponse {
    public int status;
    public String title;
}

public class VerifySigner {
    // VerifySigner checks whether the provided signer is a signer on an SSN account
    public static boolean VerifySigner(String signer, String ssnAccount, String api) {
        // Load HTTP Client for requests
        HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory();
        // Gson parser
        Gson gson = new Gson();

        // Prepare request
        GenericUrl vsReqURL = new GenericUrl(api+"/verify/signer");
        VerifySignerRequest reqBody = new VerifySignerRequest(signer, ssnAccount);

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