package com.gruis.library;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mifactura.xadessigner.signer.model.DocumentMessage;
import com.mifactura.xadessigner.signer.model.Taxpayer;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Sender {
    public static final String PRODUCTION_URL = "https://api.comprobanteselectronicos.go.cr/recepcion/v1";
    public static final String STAGING_URL = "https://api.comprobanteselectronicos.go.cr/recepcion-sandbox/v1";
    public static final String PRODUCTION_AUTH_URL = "https://idp.comprobanteselectronicos.go.cr/auth/realms/rut/protocol/openid-connect/token";
    public static final String STAGING_AUTH_URL = "https://idp.comprobanteselectronicos.go.cr/auth/realms/rut-stag/protocol/openid-connect/token";
    public static final String PRODUCTION_CLIENT_ID = "api-prod";
    public static final String STAGING_CLIENT_ID = "api-stag";


    int mode;

    public Sender(int mode) {
        this.mode = mode;
    }

    public void sendDocument(final String signedXmlPath, String userName, String password) throws Exception {

        XPath xPath = XPathFactory.newInstance().newXPath();
        File file = new File(signedXmlPath);
        byte[] bytes = FileUtils.readFileToString(file, "UTF-8").getBytes("UTF-8");
        String base64 = Base64.encodeBase64String(bytes);

        DocumentMessage message = new DocumentMessage();
        message.setComprobanteXml(base64);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();

        Document xml = doc;
        NodeList nodes = (NodeList) xPath.evaluate("/FacturaElectronica/Clave", xml.getDocumentElement(), XPathConstants.NODESET);
        message.setClave(nodes.item(0).getTextContent());
        nodes = (NodeList) xPath.evaluate("/FacturaElectronica/FechaEmision", xml.getDocumentElement(), XPathConstants.NODESET);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        message.setFecha(format.format(new Date()));
        //message.setFecha(nodes.item(0).getTextContent());

        Taxpayer emisor = new Taxpayer();
        Taxpayer receptor = new Taxpayer();

        nodes = (NodeList) xPath.evaluate("/FacturaElectronica/Emisor/Identificacion/Tipo", xml.getDocumentElement(), XPathConstants.NODESET);
        emisor.setTipoIdentificacion(nodes.item(0).getTextContent());
        nodes = (NodeList) xPath.evaluate("/FacturaElectronica/Emisor/Identificacion/Numero", xml.getDocumentElement(), XPathConstants.NODESET);
        emisor.setNumeroIdentificacion(nodes.item(0).getTextContent());

        nodes = (NodeList) xPath.evaluate("/FacturaElectronica/Receptor/Identificacion/Tipo", xml.getDocumentElement(), XPathConstants.NODESET);
        receptor.setTipoIdentificacion(nodes.item(0).getTextContent());
        nodes = (NodeList) xPath.evaluate("/FacturaElectronica/Receptor/Identificacion/Numero", xml.getDocumentElement(), XPathConstants.NODESET);
        receptor.setNumeroIdentificacion(nodes.item(0).getTextContent());

        message.setReceptor(receptor);
        message.setEmisor(emisor);

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost((mode == 0 ? STAGING_URL : PRODUCTION_URL) + "/recepcion");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(message);

        System.out.println(json);

        String token = getToken(userName, password);

        StringEntity params = new StringEntity(json);
        request.addHeader("content-type", "application/javascript");
        request.addHeader("Authorization", "bearer " + token);
        request.setEntity(params);

        HttpResponse responseEntity = httpClient.execute(request);
        System.out.println("Response code: " + responseEntity.getStatusLine().getStatusCode());
        HttpEntity entity = responseEntity.getEntity();

        String responseString = EntityUtils.toString(entity, "UTF-8");

        System.out.println(responseString);
    }

    public void checkDocumentStatus(final String key, String username, String password) throws Exception {
        String token = getToken(username, password);
        String url = (mode == 0 ? STAGING_URL : PRODUCTION_URL) + "/recepcion/" + key;
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        request.addHeader("Authorization", "bearer " + token);
        HttpResponse responseClient = httpClient.execute(request);
        System.out.println("Response code: " + responseClient.getStatusLine().getStatusCode());
        HttpEntity entity = responseClient.getEntity();
        String responseString = EntityUtils.toString(entity, "UTF-8");
        System.out.println("<responseString>");
        System.out.println(responseString);
        System.out.println("</responseString>");
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> res = objectMapper.readValue(responseString, new TypeReference<Map<String, Object>>(){});
        String respuestaXML = (String) res.get("respuesta-xml");
        respuestaXML = new String(Base64.decodeBase64(respuestaXML), "UTF-8");
        System.out.println(respuestaXML);
    }

    private String getToken(String username, String password) throws Exception {
        String token = "";
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost("https://idp.comprobanteselectronicos.go.cr/auth/realms/rut-stag/protocol/openid-connect/token");
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("grant_type", "password"));
        urlParameters.add(new BasicNameValuePair("client_id", "api-stag"));
        urlParameters.add(new BasicNameValuePair("client_secret", ""));
        urlParameters.add(new BasicNameValuePair("scope", ""));
        urlParameters.add(new BasicNameValuePair("username", username));
        urlParameters.add(new BasicNameValuePair("password", password));

        request.addHeader("content-type", "application/x-www-form-urlencoded");
        request.setEntity(new UrlEncodedFormEntity(urlParameters));
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity, "UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> res = objectMapper.readValue(responseString, new TypeReference<Map<String, Object>>(){});
        token = (String) res.get("access_token");
        return token;
    }

}
