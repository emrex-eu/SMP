package eu.emrex;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.jboss.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import eu.emrex.model.Person;
import eu.emrex.model.VerificationReply;
import eu.emrex.model.VerificationRequest;

/**
 * Servlet that verifies the returned ELMO object.
 * 
 * Will check signature and compare the person in the ELMO object and the person logged into the application.
 * 
 * @author Richard Borge (r.e.borge@fsat.no)
 * 
 */
@WebServlet(name = "VerificationServlet", urlPatterns = { "/data/verify" })
public class VerificationServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final Logger logger = Logger.getLogger(VerificationServlet.class);


    // private final Logger logger = LoggerFactory.getLogger(VerificationServlet.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {

        VerificationRequest v = Util.getJsonObjectFromRequest(request, VerificationRequest.class);
        v.setData(v.getData().replaceAll("\r\n", "\n").replaceAll("\r", "\n"));
        Person elmoP = getPersonFromElmo(v.getData());
        Person vreqP = getPersonFromVerificationRequest(v);

        VerificationReply r = new VerificationReply();
        matchPersons(r, elmoP, vreqP);
        r.setSessionId(v.getSessionId());

        System.out.println("SMP logger test");
        try {
            boolean verified = verifySignature(r.getMessages(), v.getPubKey(), v.getData());
            r.setVerified(verified);
            logger.info("verifySignature(): " + verified);

            // r.addMessage("XML data length: " + v.getData().length());
            if (verified == false) {
            }
        } catch (Exception e) {
            logger.info("Exception trying to verify signature: " + e.getStackTrace());
            r.addMessage(e.getStackTrace().toString());
            r.setVerified(false);
        }
        Gson gson = new GsonBuilder().create();
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.getWriter().println(gson.toJson(r));
    }


    private void matchPersons(VerificationReply r, Person elmoP, Person vreqP) {
        int match = 0;
        int score = 0;
        // TODO: Until we have expanded ELMO with gender...
        if (!"-".equals(elmoP.getGender()) && !elmoP.getGender().equals(vreqP.getGender())) {
            r.addMessage("Added 100 to score: Gender does not match.");
            match += 100;
        }

        String ebd = elmoP.getBirthDate();
        String vbd = vreqP.getBirthDate();
        if (ebd == null || vbd == null) {
            r.addMessage("Added 100 to score: Birth date not set for " + (ebd == null ? "elmo" : "local") + " person.");
            match += 100;
        } else if (!ebd.equals(vbd)) {
            r.addMessage("Added 100 to score: Birth date does not match.");
            match += 100;
        }

        logger.info(elmoP.getFamilyName() + " - " + vreqP.getFamilyName());
        logger.info(elmoP.getGivenNames() + " - " + vreqP.getGivenNames());

        score += Util.levenshteinDistance(elmoP.getFamilyName(), vreqP.getFamilyName());
        score += Util.levenshteinDistance(elmoP.getGivenNames(), vreqP.getGivenNames());

        if (score > 0) {
            r.addMessage("Added " + score + " to score based on Levenshtein check on name.");
        }

        match += score;

        r.setScore(match);
    }


    private Person getPersonFromElmo(String xml) {
        xml = xml.replaceAll("[\\n\\r]", "");
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(false);
        DocumentBuilder docBuilder = null;
        Document doc = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
        } catch (Exception e) {
            logger.error("Failed to parse XML", e);
            throw new IllegalArgumentException("Failed to parse XML", e);
        }

        NodeList list = doc.getElementsByTagName("report");
        if (list.getLength() == 0) {
            throw new IllegalArgumentException("Failed to get report from XML.");
        }
        Node report = list.item(0);

        Person p = new Person();
        p.setBirthDate(getValueForTag(report, "learner/bday"));
        p.setFamilyName(getValueForTag(report, "learner/familyName"));
        p.setGivenNames(getValueForTag(report, "learner/givenNames"));
        p.setGender("-"); // TODO: We need to expand ELMO to include Gender

        return p;
    }


    private Person getPersonFromVerificationRequest(VerificationRequest v) {
        Person p = new Person();
        p.setBirthDate(v.getBirthDate());
        p.setFamilyName(v.getFamilyName());
        p.setGender(v.getGender());
        p.setGivenNames(v.getGivenNames());

        return p;
    }


    private Date getDate(String d, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(d);
        } catch (ParseException e) {
            return null;
        }
    }


    private String getValueForTag(Node node, String exp) {
        XPath xpath = XPathFactory.newInstance().newXPath();
        try {
            return xpath.evaluate(exp, node);
        } catch (Exception e) {
            logger.info("XPATH error", e);
            return null;
        }
    }


    private static X509Certificate getCertificate(String certString) throws IOException, GeneralSecurityException {
        InputStream is = new ByteArrayInputStream(certString.getBytes());
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) cf.generateCertificate(is);
        is.close();
        return cert;
    }


    public boolean verifySignature(List<String> msgs, String certificate, String data) throws Exception {
        // Create a DOM XMLSignatureFactory that will be used to generate the enveloped signature.
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

        // Instantiate the document to be signed.
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        InputStream is = new ByteArrayInputStream(data.getBytes(StandardCharsets.ISO_8859_1)); // StandardCharsets.ISO_8859_1
        // msgs.add("Data bytes length: " + data.getBytes(StandardCharsets.ISO_8859_1).length);
        Document doc = dbf.newDocumentBuilder().parse(is);

        // Find Signature element.
        NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
        if (nl.getLength() == 0) {
            throw new Exception("Cannot find Signature element");
        }

        X509Certificate cert = getCertificate(certificate);
        PublicKey pubKey = cert.getPublicKey();
        DOMValidateContext valContext = new DOMValidateContext(pubKey, nl.item(0));

        // Unmarshal the XMLSignature.
        XMLSignature signature = fac.unmarshalXMLSignature(valContext);

        // Validate the XMLSignature.
        boolean coreValidity = signature.validate(valContext);

        // Check core validation status.
        if (coreValidity == false) {
            logger.error("Signature failed core validation");
            boolean sv = signature.getSignatureValue().validate(valContext);
            logger.error("signature validation status: " + sv);
            msgs.add("Signature core verification failed, status: " + sv);
            if (sv == false) {
                // Check the validation status of each Reference.
                Iterator<?> i = signature.getSignedInfo().getReferences().iterator();
                for (int j = 0; i.hasNext(); j++) {
                    boolean refValid = ((Reference) i.next()).validate(valContext);
                    System.out.println("ref[" + j + "] validity status: " + refValid);
                    msgs.add("ref[" + j + "] validity status: " + refValid);
                }
            }
        } else {
            // msgs.add("Signature verification passed");
        }

        return coreValidity;
    }
}
