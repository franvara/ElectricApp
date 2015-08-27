package com.uc3m.volttrip;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RssParserDom
{
    private URL rssUrl;

    public RssParserDom(String url)
    {
        try
        {
            this.rssUrl = new URL(url);
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public List<Gasolinera> parse()
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        List<Gasolinera> estaciones = new ArrayList<Gasolinera>();

        try
        {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document dom = builder.parse(this.getInputStream());
            Element root = dom.getDocumentElement();
            NodeList results = root.getElementsByTagName("Result");

            for (int i=0; i<results.getLength(); i++)
            {
                Gasolinera estacion = new Gasolinera();

                Node result = results.item(i);
                NodeList datosEstacion = result.getChildNodes();

                for (int j=0; j<datosEstacion.getLength(); j++)
                {
                    Node dato = datosEstacion.item(j);
                    String etiqueta = dato.getNodeName();

                    if (etiqueta.equals("Direccion"))
                    {
                        String texto = obtenerTexto(dato);

                        estacion.setDireccion(texto);
                    }
                    else if (etiqueta.equals("Rotulo"))
                    {
                        estacion.setRotulo(dato.getFirstChild().getNodeValue());
                    }
                    else if (etiqueta.equals("X"))
                    {
                        String texto = obtenerTexto(dato);

                        estacion.setX(texto);
                    }
                    else if (etiqueta.equals("Y"))
                    {
                        estacion.setY(dato.getFirstChild().getNodeValue());
                    }

                }

                estaciones.add(estacion);
            }
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return estaciones;
    }

    private String obtenerTexto(Node dato)
    {
        StringBuilder texto = new StringBuilder();
        NodeList fragmentos = dato.getChildNodes();

        for (int k=0;k<fragmentos.getLength();k++)
        {
            texto.append(fragmentos.item(k).getNodeValue());
        }

        return texto.toString();
    }

    private InputStream getInputStream()
    {
        try
        {
            return rssUrl.openConnection().getInputStream();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}