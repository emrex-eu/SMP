package eu.emrex;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eu.emrex.model.DataRequest;
import eu.emrex.model.DataRequestEncrypted;

@WebServlet(name = "DataEncrypt", urlPatterns = { "/data/encrypt" })
public class DataEncrypt extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final Logger logger = LoggerFactory.getLogger(DataEncrypt.class);


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        try {
            String pubKey = Util.readFile(System.getProperty("emrex.pubKey"));

            StringBuffer jb = new StringBuffer();
            String line = null;

            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);

            String inputReq = jb.toString();

            Gson gson = new GsonBuilder().create();
            Type type = new TypeToken<DataRequest>() {
            }.getType();
            DataRequest dataReq = gson.fromJson(inputReq, type);

            // Encrypt data
            String encData = encrypt(inputReq, pubKey);
            DataRequestEncrypted dataReqEnc = new DataRequestEncrypted(dataReq.getSessionId(), encData);

            logger.info("Encrypted data: " + encData);

            // Decrypt data
            String privKey = Util.readFile(System.getProperty("emrex.privKey"));
            logger.info("Read privKey: " + privKey);
            String decData = decrypt(encData, privKey);

            logger.info("Decrypted data: " + decData);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println(gson.toJson(dataReqEnc));
            logger.info("doPost(): " + gson.toJson(dataReqEnc));

        } catch (Exception t) {
            logger.error("Request encryption failed", t);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("Request encryption failed: " + t.getMessage());
        }
    }


    private static X509Certificate getCertificate(String certString) throws IOException, GeneralSecurityException {
        InputStream is = new ByteArrayInputStream(certString.getBytes());
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) cf.generateCertificate(is);
        is.close();
        return cert;
    }


    private String encrypt(String input, String certString) throws IOException, GeneralSecurityException {
        X509Certificate cert = getCertificate(certString);
        PublicKey pubKey = cert.getPublicKey();
        Cipher rsa = Cipher.getInstance("RSA");

        rsa.init(Cipher.ENCRYPT_MODE, pubKey);
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();

        /* We must loop and encode small chunks, otherwise RSA returns empty
         */
        for (int i = 0; i < input.getBytes().length; i += 60) {
            CipherOutputStream os = new CipherOutputStream(byteOutputStream, rsa);
            int bytes = 60;
            if (i + 60 > input.getBytes().length) {
                bytes = input.getBytes().length % 60;
            }
            os.write(input.getBytes(), i, bytes);
            os.flush();
            os.close();
        }
        return new String(Base64.encodeBase64(byteOutputStream.toByteArray()));
    }


    private static String decrypt(String input, String privkeyStr) throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException, InvalidKeySpecException {
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privkeyStr.getBytes());

        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey pk = kf.generatePrivate(privateKeySpec);

        Cipher rsa = Cipher.getInstance("RSA");

        rsa.init(Cipher.DECRYPT_MODE, pk);
        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(input.getBytes());

        InputStream is = new CipherInputStream(byteInputStream, rsa);
        return is.toString();

    }

}
