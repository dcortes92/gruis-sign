package com.gruis.library;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import xades4j.algorithms.EnvelopedSignatureTransform;
import xades4j.algorithms.XPathTransform;
import xades4j.production.*;
import xades4j.properties.*;
import xades4j.providers.KeyingDataProvider;
import xades4j.providers.SignaturePolicyInfoProvider;
import xades4j.providers.impl.FileSystemKeyStoreKeyingDataProvider;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;

public class Signer {
    public static final String storageConnectionString =
            "DefaultEndpointsProtocol=http;"
                    + "AccountName=gruismifactura;"
                    + "AccountKey=csZObBn7o5+4KIXtW/sSmGdH5fjG1y8UNz2VzPWFuG1Y8X+n1hvsiVHru8/vY5VJve4W7lwKFmtI1r8hd+hXmw==";

    public void sign(int userId, String key, String certURL, String pin, String xml, String userName, String password, int mode) {
        KeyingDataProvider kp;
        Sender sender = new Sender(mode);

        try {
            CloudStorageAccount account = CloudStorageAccount.parse(storageConnectionString);
            CloudBlobClient serviceClient = account.createCloudBlobClient();

            CloudBlobContainer container = serviceClient.getContainerReference("documents");

            CloudBlockBlob blob = container.getBlockBlobReference(certURL);

            File theDir = new File("D:\\Home\\" + userId + "");
            if (!theDir.exists()) {
                try {
                    theDir.mkdir();
                } catch (SecurityException se) {
                    //handle it
                }
            }

            String fileNameBase = "D:\\Home\\" + userId + "\\";

            blob.download(new FileOutputStream(fileNameBase + blob.getName() + "Cert.p12"));


            SignaturePolicyInfoProvider policyInfoProvider = new SignaturePolicyInfoProvider() {
                public SignaturePolicyBase getSignaturePolicy() {
                    return new SignaturePolicyIdentifierProperty(
                            new ObjectIdentifier("https://tribunet.hacienda.go.cr/docs/esquemas/2017/v4.2/facturaElectronica"),
                            new ByteArrayInputStream("Politica de Factura Digital".getBytes()));
                }
            };

            kp = new FileSystemKeyStoreKeyingDataProvider(
                    "pkcs12",
                    fileNameBase + blob.getName() + "Cert.p12",
                    new FirstCertificateSelector(),
                    new DirectPasswordProvider(pin),
                    new DirectPasswordProvider(pin),
                    false);

            DataObjectDesc obj1 = new DataObjectReference("")
                    .withDataObjectFormat(new DataObjectFormatProperty("application/octet-stream")).
                            withTransform(new XPathTransform("not(ancestor-or-self::ds:Signature)")).
                            withTransform(new EnvelopedSignatureTransform());

            SignedDataObjects dataObjs = new SignedDataObjects(obj1);

            XadesSigningProfile p = new XadesEpesSigningProfile(kp, policyInfoProvider);


            byte[] valueDecoded = Base64.decodeBase64(xml);
            String xmlString = new String(valueDecoded, "UTF-8");
            xmlString = xmlString.replaceFirst("^([\\\\W]+)<", "<");

            // open file
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            factory.setNamespaceAware(true);
//            DocumentBuilder builder = null;
//            builder = factory.newDocumentBuilder();
//            Document doc1 = builder.parse(new File("../firmas/1/nofirmado.xml"));
//            Element elemToSign = doc1.getDocumentElement();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource in = new InputSource(new StringReader(xmlString));
            in.setEncoding("UTF-8");
            Document doc1 = builder.parse(in);
            Element elemToSign = doc1.getDocumentElement();

            XadesSigner signer = p.newSigner();
            signer.sign(dataObjs, elemToSign);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();

            Result output = new StreamResult(fileNameBase + "\\" + key + ".xml");
            //output.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>".getBytes("UTF-8"));
            Source input = new DOMSource(doc1);

            transformer.transform(input, output);
            transformer.reset();

            Thread.currentThread().sleep(3000);
            sender.sendDocument(fileNameBase + key + ".xml", userName, password);
            Thread.currentThread().sleep(2000);
            sender.checkDocumentStatus(key, userName, password);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
