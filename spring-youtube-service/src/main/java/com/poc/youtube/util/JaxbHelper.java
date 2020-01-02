package com.poc.youtube.util;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.springframework.stereotype.Component;

import com.poc.youtube.model.UTVideo;

/**
 * The JaxbHelper class helps to marshall Java POJO into XML String and
 * unmarshal XML string into Java POJO.
 * 
 * @version 1.0
 */
@Component
public class JaxbHelper {

	public JaxbHelper() {
	}

	/**
	 * Marshalls given Java object into XML string.
	 * 
	 * @param v The UTVideo object which needs to be converted into XML string.
	 * @return A XML string.
	 */
	public String pojoToXml(UTVideo v) {
		StringWriter xmlString = new StringWriter();

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(UTVideo.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.marshal(v, xmlString);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return xmlString.toString().trim();
	}

	/**
	 * Unmarshalls given XML string into Java object.
	 * 
	 * @param v The XML string which needs to be converted into Java object.
	 * @return A UTVideo object.
	 */
	public UTVideo xmlToPojo(String xmlString) {
		UTVideo video = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(UTVideo.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			video = (UTVideo) jaxbUnmarshaller.unmarshal(new StringReader(xmlString.trim()));

		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return video;
	}

}
