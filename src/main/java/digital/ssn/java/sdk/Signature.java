package digital.ssn.java.sdk;

import java.io.IOException;
import com.google.gson.*;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Signature {

    private static final Logger LOGGER = LoggerFactory.getLogger(Signature.class);

    // Verify checks whether the provided message, signature and public key are valid with timeOut 20s
    public static boolean Verify(String message, String signature, String publicKey, String api) throws IOException {
        return Verify(message, signature, publicKey, api, 20000);
    }

    // Verify checks whether the provided message, signature and public key are valid with dynamic timeOut
    public static boolean Verify(String message, String signature, String publicKey, String api, Integer timeOut) throws IOException {
        // Load HTTP Client for requests
        HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory();
        // Gson parser
        Gson gson = new Gson();
        boolean isVerify = false;

        // Prepare request
        GenericUrl vsReqURL = new GenericUrl(api+"/verify/signature");
        VerifySignatureRequest reqBody = new VerifySignatureRequest(message, signature, publicKey);
        LOGGER.info("Verify request body : {} ", gson.toJson(reqBody));

        // Make the request
        try {
            HttpRequest vsReq = requestFactory.buildPostRequest(vsReqURL, ByteArrayContent.fromString(null,gson.toJson(reqBody)));
            vsReq.getHeaders().setContentType("application/json");
            vsReq.setWriteTimeout(timeOut);
            vsReq.setConnectTimeout(timeOut);
            HttpResponse vsResp = vsReq.execute();
            try {
                if (vsResp.getStatusCode() == 200) {
                    isVerify = true;
                }
            } finally {
                vsResp.disconnect();
            }
        } catch (IOException e) {
            LOGGER.error("Error : {} ", e);
            throw e;
        }
        LOGGER.info("Verify response body : {} ", isVerify);
        return isVerify;
    }

    // VerifySigner checks whether the provided signer is a signer on an SSN account with timeOut 20s
    public static boolean VerifySigner(String signer, String ssnAccount, String api) throws IOException {
        return VerifySigner(signer, ssnAccount, api, 20000);
    }

    // VerifySigner checks whether the provided signer is a signer on an SSN account with dynamic timeOut
    public static boolean VerifySigner(String signer, String ssnAccount, String api, Integer timeOut) throws IOException {
        // Load HTTP Client for requests
        HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory();
        // Gson parser
        Gson gson = new Gson();
        boolean isVerifySinger = false;

        // Prepare request
        GenericUrl vsReqURL = new GenericUrl(api+"/verify/signer");
        VerifySignerRequest reqBody = new VerifySignerRequest(signer, ssnAccount);
        LOGGER.info("VerifySinger request body : {} ", gson.toJson(reqBody));

        // Make the request
        try {
            HttpRequest vsReq = requestFactory.buildPostRequest(vsReqURL, ByteArrayContent.fromString(null,gson.toJson(reqBody)));
            vsReq.getHeaders().setContentType("application/json");
            vsReq.setWriteTimeout(timeOut);
            vsReq.setConnectTimeout(timeOut);
            HttpResponse vsResp = vsReq.execute();
            try {
                if (vsResp.getStatusCode() == 200) {
                    isVerifySinger = true;
                }
            } finally {
                vsResp.disconnect();
            }
        } catch (IOException e) {
            LOGGER.error("Error : {} ", e);
            throw e;
        }
        LOGGER.info("VerifySinger response body : {} ", isVerifySinger);
        return isVerifySinger;
    }
}

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

// VerifySignerRequest describes the JSON structure for making a request to the verify signer API
class VerifySignerRequest {
    private String signer;
    private String ssn_account;

    VerifySignerRequest(String signPK, String ssnPK) {
        signer = signPK;
        ssn_account = ssnPK;
    }
}
