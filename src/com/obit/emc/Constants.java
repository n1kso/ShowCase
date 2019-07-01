package com.obit.emc;

import com.bssys.server.Context;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Constants {

    private String paramValue;

    public Constants(Element task, Context con) throws SQLException {
        String sql = "select * from sysparam where name='sign.orgs.ignore'";
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            this.paramValue = rs.getString("PARAM_VALUE");
        }
    }

    /**
     * Позволяет получить xml со списком организаций, ЭД которых не проверяются на ЭП
     *
     * @return строку с xml
     */
    public String getParamValue() {
        return this.paramValue;
    }

    /**
     * Выводи списка организаций, ЭД которых не проходят проверку ЭП
     *
     * @return масиив id-ми организаций
     */
    public ArrayList<String> getListOrg() throws SQLException, ParserConfigurationException, IOException, SAXException {
        ArrayList<String> temp = new ArrayList<>();
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(new ByteArrayInputStream(this.paramValue.getBytes()));

        NodeList nodeList = document.getElementsByTagName("ORG_IGNORE");
        for (int i = 0, size = nodeList.getLength(); i < size; i++) {
            temp.add(nodeList.item(i).getAttributes().getNamedItem("ID").getNodeValue());
        }
        return temp;
    }
}
