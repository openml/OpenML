package org.openml.util;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;



public class OpenMLNameSpaceContext implements NamespaceContext {


	@Override
	public String getNamespaceURI(String prefix) {
		if (prefix.equals("oml"))
            return "http://open-ml.org/openml";
       else
            return XMLConstants.NULL_NS_URI;
	}

	@Override
	public String getPrefix(String namespaceURI) {
            return null;
	}

	@Override
	public Iterator getPrefixes(String namespaceURI) {
		return null;
	}

}
