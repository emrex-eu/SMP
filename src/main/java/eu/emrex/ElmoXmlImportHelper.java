package eu.emrex;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.emrex.model.ElmoDocument;
import eu.emrex.model.ElmoResult;

public class ElmoXmlImportHelper {

    private final Logger logger = LoggerFactory.getLogger(ElmoXmlImportHelper.class);


    public ElmoDocument getDocument(Node report) throws Exception {
        ElmoDocument doc = new ElmoDocument();
        String firstName = getValueForTag(report, "learner/givenNames");
        String lastName = getValueForTag(report, "learner/familyName");
        doc.setPersonName(firstName + " " + lastName);
        doc.setInstitutionName(getValueForTag(report, "issuer/title"));

        List<ElmoResult> results = new ArrayList<ElmoResult>();

        XPath xpath = XPathFactory.newInstance().newXPath();

        NodeList topLevelLOS = (NodeList) xpath.evaluate("learningOpportunitySpecification", report,
            XPathConstants.NODESET);

        for (int i = 0; i < topLevelLOS.getLength(); i++) {
            Node node = (Node) topLevelLOS.item(i);
            Node type = (Node) xpath.evaluate("type", node, XPathConstants.NODE);

            if (type == null) {
                continue;
            }

            if (type.getTextContent().equalsIgnoreCase("degree")) {
                results.add(resultFromDegree(results, node, xpath));
            } else if (type.getTextContent().equalsIgnoreCase("module")) {
                results.add(resultFromModule(results, node, xpath));
            } else if (type.getTextContent().equalsIgnoreCase("module group")) {
                results.add(resultFromModule(results, node, xpath));
            }

        }

        doc.setResults(results);
        return doc;
    }


    private ElmoResult resultFromDegree(List<ElmoResult> results, Node los, XPath xpath) throws Exception {
        ElmoResult res = new ElmoResult();
        res.setCode(getValueForTag(los, "qualification/identifier[@type='local']"));
        res.setName(getEnglishTitle(los, xpath, "qualification/title"));
        res.setCredits(getValueForTag(los, "credit/value"));

        NodeList topLevelLOS = (NodeList) xpath.evaluate("hasPart/learningOpportunitySpecification", los,
            XPathConstants.NODESET);
        for (int i = 0; i < topLevelLOS.getLength(); i++) {
            Node node = (Node) topLevelLOS.item(i);
            Node type = (Node) xpath.evaluate("type", node, XPathConstants.NODE);

            if (type == null) {
                continue;
            }

            if (type.getTextContent().equalsIgnoreCase("degree")) {
                results.add(resultFromDegree(results, node, xpath));
            } else if (type.getTextContent().equalsIgnoreCase("module")) {
                results.add(resultFromModule(results, node, xpath));
            } else if (type.getTextContent().equalsIgnoreCase("module group")) {
                results.add(resultFromModule(results, node, xpath));
            }

        }

        return res;
    }


    private ElmoResult resultFromModule(List<ElmoResult> results, Node los, XPath xpath) throws Exception {
        ElmoResult res = new ElmoResult();
        res.setCode(getValueForTag(los, "identifier[@type='local']"));
        res.setName(getEnglishTitle(los, xpath, "title"));
        res.setCredits(getValueForTag(los, "credit/value"));
        res.setResult(getValueForTag(los, "specifies/learningOpportunityInstance/result"));

        NodeList topLevelLOS = (NodeList) xpath.evaluate("hasPart/learningOpportunitySpecification", los,
            XPathConstants.NODESET);
        for (int i = 0; i < topLevelLOS.getLength(); i++) {
            Node node = (Node) topLevelLOS.item(i);
            Node type = (Node) xpath.evaluate("type", node, XPathConstants.NODE);

            if (type == null) {
                continue;
            }

            if (type.getTextContent().equalsIgnoreCase("degree")) {
                results.add(resultFromDegree(results, node, xpath));
            } else if (type.getTextContent().equalsIgnoreCase("module")) {
                results.add(resultFromModule(results, node, xpath));
            } else if (type.getTextContent().equalsIgnoreCase("module group")) {
                results.add(resultFromModule(results, node, xpath));
            }
        }

        return res;
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


    private String getEnglishTitle(Node node, XPath xpath, String expr) throws Exception {
        NodeList nList = (NodeList) xpath.evaluate(expr, node, XPathConstants.NODESET);
        String title = "No title";
        for (int i = 0; i < nList.getLength(); i++) {
            Node n = (Node) nList.item(i);
            title = n.getTextContent();
            NamedNodeMap map = n.getAttributes();
            Node lang = map.getNamedItem("xml:lang");
            if ("en".equals(lang.getTextContent())) {
                return title;
            }
        }
        return title;
    }

}
