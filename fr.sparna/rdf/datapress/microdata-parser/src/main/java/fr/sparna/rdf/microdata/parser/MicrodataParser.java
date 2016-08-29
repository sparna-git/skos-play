/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.sparna.rdf.microdata.parser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.URI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.impl.ValueFactoryImpl;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.sparna.rdf.microdata.parser.itemscope.ItemProp;
import fr.sparna.rdf.microdata.parser.itemscope.ItemPropValue;
import fr.sparna.rdf.microdata.parser.itemscope.ItemScope;
import fr.sparna.rdf.microdata.parser.itemscope.ItemScopeParser;
import fr.sparna.rdf.microdata.parser.itemscope.ItemScopeParserException;
import fr.sparna.rdf.microdata.parser.itemscope.ItemScopeParserReport;

/**
 * Default implementation of <a href="http://www.w3.org/TR/microdata/">Microdata</a> extractor,
 * based on {@link org.apache.any23.extractor.Extractor.TagSoupDOMExtractor}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 */
public class MicrodataParser {

	private static final String DCTERMS_NS = "http://purl.org/dc/terms/";
	
	private static final String XHTML = "http://www.w3.org/1999/xhtml/vocab#";
	
    private static final URI MICRODATA_ITEM = RDFUtils.uri("http://www.w3.org/1999/xhtml/microdata#item");

    private String documentLanguage;

    private boolean isStrict = false;

    private String defaultNamespace;
    
    /**
     * This extraction performs the
     * <a href="http://www.w3.org/TR/microdata/#rdf">Microdata to RDF conversion algorithm</a>.
     * A slight modification of the specification algorithm has been introduced
     * to avoid performing actions 5.2.1, 5.2.2, 5.2.3, 5.2.4 if step 5.2.6 doesn't detect any
     * Microdata.
     */
    public void parse(
    		java.net.URI uri,
            Document in,
            RDFHandler out
    ) throws MicrodataParserException {

        final ItemScopeParserReport parserReport = ItemScopeParser.getMicrodata(in);
        if(parserReport.getErrors().length > 0) {
        	// TODO : proper error handling
        	for (ItemScopeParserException anError : parserReport.getErrors()) {
				anError.printStackTrace();
			}
        }
        final ItemScope[] itemScopes = parserReport.getDetectedItemScopes();
        if (itemScopes.length == 0) {
            return;
        }

        // isStrict = extractionParameters.getFlag("any23.microdata.strict");
        if (!this.isStrict) {
            // defaultNamespace = extractionParameters.getProperty("any23.microdata.ns.default");
        }

        documentLanguage = getDocumentLanguage(in);

        URI documentURI = SimpleValueFactory.getInstance().createURI(uri.toString());
        
        // start RDF in handler
        out.startRDF();
        
        /**
         * 5.2.6
         */
        final Map<ItemScope, Resource> mappings = new HashMap<ItemScope, Resource>();
        for (ItemScope itemScope : itemScopes) {
            Resource subject = processType(itemScope, documentURI, out, mappings);
            out.handleStatement(
            		RDFUtils.statement(
                    documentURI,
                    MICRODATA_ITEM,
                    subject
                    )
            );
        }

        /**
         * 5.2.1
         */
        processTitle(in, documentURI, out);
        /**
         * 5.2.2
         */
        processHREFElements(in, documentURI, out);
        /**
         * 5.2.3
         */
        processMetaElements(in, documentURI, out);

        /**
         * 5.2.4
         */
        processCiteElements(in, documentURI, out);
        
        // end RDF in handler
        out.endRDF();
    }

    /**
     * Returns the {@link Document} language if declared, <code>null</code> otherwise.
     *
     * @param in a instance of {@link Document}.
     * @return the language declared, could be <code>null</code>.
     */
    private String getDocumentLanguage(Document in) {
        String lang = DomUtils.find(in, "string(/HTML/@lang)");
        if (lang.equals("")) {
            return null;
        }
        return lang;
    }

    /**
     * Returns the {@link Node} language if declared, or the {@link Document} one
     * if not defined.
     *
     * @param node a {@link Node} instance.
     * @return the {@link Node} language or the {@link Document} one. Could be <code>null</code>
     */
    private String getLanguage(Node node) {
        Node nodeLang = node.getAttributes().getNamedItem("lang");
        if (nodeLang == null) {
            // if the element does not specify a lang, use the document one
            return documentLanguage;
        }
        return nodeLang.getTextContent();
    }

    /**
     * Implements step 5.2.1 of <a href="http://dev.w3.org/html5/md/Overview.html#rdf">Microdata to RDF</a>
     * extraction algorithm.
     *
     * @param in          {@link Document} to be processed.
     * @param documentURI Document current {@link URI}.
     * @param out         a valid not <code>null</code> {@link ExtractionResult}
     */
    private void processTitle(Document in, URI documentURI, RDFHandler out) {
        NodeList titles = in.getElementsByTagName("title");
        // just one title is allowed.
        if (titles.getLength() == 1) {
            Node title = titles.item(0);
            String titleValue = title.getTextContent();
            Literal object;
            String lang = getLanguage(title);
            if (lang == null) {
                // unable to decide the language, leave it unknown
                object = RDFUtils.literal(titleValue);
            } else {
                object = RDFUtils.literal(titleValue, lang);
            }
            out.handleStatement(
            		RDFUtils.statement(
                    documentURI,
                    RDFUtils.uri(DCTERMS_NS+"title"),
                    object
                    )
            );
        }
    }

    /**
     * Implements step 5.2.2 of <a href="http://dev.w3.org/html5/md/Overview.html#rdf">Microdata to RDF</a>
     * extraction algorithm.
     *
     * @param in          {@link Document} to be processed.
     * @param documentURI Document current {@link URI}.
     * @param out         a valid not <code>null</code> {@link ExtractionResult}
     */
    private void processHREFElements(Document in, URI documentURI, RDFHandler out) {
        NodeList anchors = in.getElementsByTagName("a");
        for (int i = 0; i < anchors.getLength(); i++) {
            processHREFElement(anchors.item(i), documentURI, out);
        }
        NodeList areas = in.getElementsByTagName("area");
        for (int i = 0; i < areas.getLength(); i++) {
            processHREFElement(areas.item(i), documentURI, out);
        }
        NodeList links = in.getElementsByTagName("link");
        for (int i = 0; i < links.getLength(); i++) {
            processHREFElement(links.item(i), documentURI, out);
        }
    }

    /**
     * Implements sub-step for 5.2.3 of <a href="http://dev.w3.org/html5/md/Overview.html#rdf">Microdata to RDF</a>
     * extraction algorithm.
     *
     * @param item        {@link Node} to be processed.
     * @param documentURI Document current {@link URI}.
     * @param out         a valid not <code>null</code> {@link ExtractionResult}
     */
    private void processHREFElement(Node item, URI documentURI, RDFHandler out) {
        Node rel = item.getAttributes().getNamedItem("rel");
        if (rel == null) {
            return;
        }
        Node href = item.getAttributes().getNamedItem("href");
        if (href == null) {
            return;
        }
        URL absoluteURL;
        if (!isAbsoluteURL(href.getTextContent())) {
            try {
                absoluteURL = toAbsoluteURL(
                        documentURI.toString(),
                        href.getTextContent(),
                        '/'
                );
            } catch (MalformedURLException e) {
                // okay, it's not an absolute URL, return
                return;
            }
        } else {
            try {
                absoluteURL = new URL(href.getTextContent());
            } catch (MalformedURLException e) {
                // cannot happen
                return;
            }
        }
        String[] relTokens = rel.getTextContent().split(" ");
        Set<String> tokensWithNoDuplicates = new HashSet<String>();
        for (String relToken : relTokens) {
            if (relToken.contains(":")) {
                // if contain semi-colon, skip
                continue;
            }
            if (relToken.equals("alternate") || relToken.equals("stylesheet")) {
                tokensWithNoDuplicates.add("ALTERNATE-STYLESHEET");
                continue;
            }
            tokensWithNoDuplicates.add(relToken.toLowerCase());
        }
        for (String token : tokensWithNoDuplicates) {
            URI predicate;
            if (isAbsoluteURL(token)) {
                predicate = RDFUtils.uri(token);
            } else {
                predicate = RDFUtils.uri(XHTML + token);
            }
            out.handleStatement(
            		RDFUtils.statement(
                    documentURI,
                    predicate,
                    RDFUtils.uri(absoluteURL.toString())
                    )
            );
        }
    }

    /**
     * Implements step 5.2.3 of <a href="http://dev.w3.org/html5/md/Overview.html#rdf">Microdata to RDF</a>
     * extraction algorithm.
     *
     * @param in          {@link Document} to be processed.
     * @param documentURI Document current {@link URI}.
     * @param out         a valid not <code>null</code> {@link ExtractionResult}
     */
    private void processMetaElements(Document in, URI documentURI, RDFHandler out) {
        NodeList metas = in.getElementsByTagName("meta");
        for (int i = 0; i < metas.getLength(); i++) {
            Node meta = metas.item(i);
            String name    = DomUtils.readAttribute(meta, "name"   , null);
            String content = DomUtils.readAttribute(meta, "content", null);
            if (name != null && content != null) {
                if (isAbsoluteURL(name)) {
                    processMetaElement(
                            RDFUtils.uri(name),
                            content,
                            getLanguage(meta),
                            documentURI,
                            out
                    );
                } else {
                    processMetaElement(
                            name,
                            content,
                            getLanguage(meta),
                            documentURI,
                            out
                    );
                }
            }
        }
    }

    /**
     * Implements sub step for 5.2.3 of <a href="http://dev.w3.org/html5/md/Overview.html#rdf">Microdata to RDF</a>
     * extraction algorithm.
     *
     * @param uri
     * @param content
     * @param language
     * @param documentURI
     * @param out
     */
    private void processMetaElement(
            URI uri,
            String content,
            String language,
            URI documentURI,
            RDFHandler out
    ) {
        if (content.contains(":")) {
            // if it contains U+003A COLON, exit
            return;
        }
        Literal subject;
        if (language == null) {
            // ok, we don't know the language
            subject = RDFUtils.literal(content);
        } else {
            subject = RDFUtils.literal(content, language);
        }
        out.handleStatement(RDFUtils.statement(
                documentURI,
                uri,
                subject
                )
        );
    }

    /**
     * Implements sub step for 5.2.3 of <a href="http://dev.w3.org/html5/md/Overview.html#rdf">Microdata to RDF</a>
     * extraction algorithm.
     *
     * @param name
     * @param content
     * @param language
     * @param documentURI
     * @param out
     */
    private void processMetaElement(
            String name,
            String content,
            String language,
            URI documentURI,
            RDFHandler out) {
        Literal subject;
        if (language == null) {
            // ok, we don't know the language
            subject = RDFUtils.literal(content);
        } else {
            subject = RDFUtils.literal(content, language);
        }
        out.handleStatement(RDFUtils.statement(
                documentURI,
                RDFUtils.uri(XHTML + name.toLowerCase()),
                subject
                )
        );
    }

    /**
     * Implements sub step for 5.2.4 of <a href="http://dev.w3.org/html5/md/Overview.html#rdf">Microdata to RDF</a>
     * extraction algorithm.
     *
     * @param in
     * @param documentURI
     * @param out
     */
    private void processCiteElements(Document in, URI documentURI, RDFHandler out) {
        NodeList blockQuotes = in.getElementsByTagName("blockquote");
        for (int i = 0; i < blockQuotes.getLength(); i++) {
            processCiteElement(blockQuotes.item(i), documentURI, out);
        }
        NodeList quotes = in.getElementsByTagName("q");
        for (int i = 0; i < quotes.getLength(); i++) {
            processCiteElement(quotes.item(i), documentURI, out);
        }
    }

    private void processCiteElement(Node item, URI documentURI, RDFHandler out) {
        if (item.getAttributes().getNamedItem("cite") != null) {
            out.handleStatement(RDFUtils.statement(
                    documentURI,
                    RDFUtils.uri(DCTERMS_NS+"source"),
                    RDFUtils.uri(item.getAttributes().getNamedItem("cite").getTextContent())
                   )
            );
        }
    }

    /**
     * Recursive method implementing 5.2.6.1 "generate the triple for the item" of
     * <a href="http://dev.w3.org/html5/md/Overview.html#rdf">Microdata to RDF</a>
     * extraction algorithm.
     *
     * @param itemScope
     * @param documentURI
     * @param out
     * @param mappings
     * @return
     * @throws MicrodataParserException
     */
    private Resource processType(
            ItemScope itemScope,
            URI documentURI, RDFHandler out,
            Map<ItemScope, Resource> mappings
    ) throws MicrodataParserException {
        Resource subject;
        if (mappings.containsKey(itemScope)) {
            subject = mappings.get(itemScope);
        } else if (isAbsoluteURL(itemScope.getItemId())) {
            subject = RDFUtils.uri(itemScope.getItemId());
        } else {
            subject = RDFUtils.getBNode(Integer.toString(itemScope.hashCode()));
        }
        mappings.put(itemScope, subject);

        // ItemScope.type could be null, but surely it's a valid URL
        String itemScopeType = "";
        if (itemScope.getType() != null) {
            String itemType;
            itemType = itemScope.getType().toString();
            out.handleStatement(RDFUtils.statement(subject, RDF.TYPE, RDFUtils.uri(itemType)));
            itemScopeType = itemScope.getType().toString();
        }
        for (String propName : itemScope.getProperties().keySet()) {
            List<ItemProp> itemProps = itemScope.getProperties().get(propName);
            for (ItemProp itemProp : itemProps) {
                try {
                    processProperty(
                            subject,
                            propName,
                            itemProp,
                            itemScopeType,
                            documentURI,
                            mappings,
                            out
                    );
                } catch (MalformedURLException e) {
                    throw new MicrodataParserException(
                            "Error while processing on subject '" + subject +
                                    "' the itemProp: '" + itemProp + "' "
                    );
                }
            }
        }
        return subject;
    }

    private void processProperty(
            Resource subject,
            String propName,
            ItemProp itemProp,
            String itemScopeType,
            URI documentURI,
            Map<ItemScope, Resource> mappings,
            RDFHandler out
    ) throws MalformedURLException, MicrodataParserException {
        URI predicate;
        if (!isAbsoluteURL(propName) && itemScopeType.equals("") && isStrict) {
            return;
        } else if (!isAbsoluteURL(propName) && itemScopeType.equals("") && !isStrict) {
            predicate = RDFUtils.uri(
                    toAbsoluteURL(
                            defaultNamespace,
                            propName,
                            '/'
                    ).toString()
            );
        } else {
            predicate = RDFUtils.uri(
                    toAbsoluteURL(
                            itemScopeType,
                            propName,
                            '/'
                    ).toString());
        }
        Value value;
        Object propValue = itemProp.getValue().getContent();
        ItemPropValue.Type propType = itemProp.getValue().getType();
        if (propType.equals(ItemPropValue.Type.Nested)) {
            value = processType((ItemScope) propValue, documentURI, out, mappings);
        } else if (propType.equals(ItemPropValue.Type.Plain)) {
            value = RDFUtils.literal((String) propValue, documentLanguage);
        } else if (propType.equals(ItemPropValue.Type.Link)) {
            value = RDFUtils.uri(
                    toAbsoluteURL(
                            documentURI.toString(),
                            (String) propValue,
                            '/'
                    ).toString()
            );
        } else if (propType.equals(ItemPropValue.Type.Date)) {
            value = RDFUtils.literal(ItemPropValue.formatDateTime((Date) propValue), XMLSchema.DATE);
        } else {
            throw new RuntimeException("Invalid Type '" +
                    propType + "' for ItemPropValue with name: '" + propName + "'");
        }
        out.handleStatement(RDFUtils.statement(subject, predicate, value));
    }

    private boolean isAbsoluteURL(String urlString) {
        boolean result = false;
        try {
            URL url = new URL(urlString);
            String protocol = url.getProtocol();
            if (protocol != null && protocol.trim().length() > 0)
                result = true;
        } catch (MalformedURLException e) {
            return false;
        }
        return result;
    }

    private URL toAbsoluteURL(String ns, String part, char trailing)
            throws MalformedURLException {
        if (isAbsoluteURL(part)) {
            return new URL(part);
        }
        char lastChar = ns.charAt(ns.length() - 1);
        if (lastChar == '#' || lastChar == '/') {
            return new URL(ns + part);
        } else {
        	// find position of last '/'
        	return new URL(ns.substring(0, ns.lastIndexOf("/")) + trailing + part); 
        }
        // return new URL(ns + trailing + part);
    }

//    private void notifyError(ItemScopeParserException[] errors, RDFHandler out) {
//        for(ItemScopeParserException mpe : errors) {
//            out.notifyIssue(
//                    "ERROR",
//                    mpe.toJSON(),
//                    mpe.getErrorLocationBeginRow(),
//                    mpe.getErrorLocationBeginCol()
//            );
//        }
//    }

	public boolean isStrict() {
		return isStrict;
	}

	public void setStrict(boolean isStrict) {
		this.isStrict = isStrict;
	}

	public String getDefaultNamespace() {
		return defaultNamespace;
	}

	public void setDefaultNamespace(String defaultNamespace) {
		this.defaultNamespace = defaultNamespace;
	}
   


}