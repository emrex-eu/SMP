package eu.emrex;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(VerificationServlet.class);


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {

        VerificationRequest v = Util.getJsonObjectFromRequest(request, VerificationRequest.class);
        Person elmoP = getPersonFromElmo(v.getData());
        Person vreqP = getPersonFromVerificationRequest(v);

        VerificationReply r = new VerificationReply();
        matchPersons(r, elmoP, vreqP);
        r.setSessionId(v.getSessionId());
        r.setData(v.getData());

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

        Date ebd = elmoP.getBirthDate();
        Date vbd = vreqP.getBirthDate();
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
        
        r.addMessage("Added " + score + " to score based on Levenshtein check on name.");
        
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
        if(list.getLength() == 0) {
        	throw new IllegalArgumentException("Failed to get report from XML.");
        }
        Node report = list.item(0);

        Person p = new Person();
        p.setBirthDate(getDate(getValueForTag(report, "learner/bday"), getValueForTag(report, "learner/bday/@dtf")));
        p.setFamilyName(getValueForTag(report, "learner/familyName"));
        p.setGivenNames(getValueForTag(report, "learner/givenNames"));
        p.setGender("-"); // TODO: We need to expand ELMO to include Gender

        return p;
    }


    private Person getPersonFromVerificationRequest(VerificationRequest v) {
        Person p = new Person();
        p.setBirthDate(getDate(v.getBirthDate(), "yyyyMMdd"));
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
}
