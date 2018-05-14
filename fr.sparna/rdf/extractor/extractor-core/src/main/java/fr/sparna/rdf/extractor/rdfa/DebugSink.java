/**
 * Copyright 2012-2013 the Semargl contributors. See AUTHORS for more details.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.sparna.rdf.extractor.rdfa;


import java.io.PrintStream;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.semarglproject.rdf.ParseException;
import org.semarglproject.sink.QuadSink;
import org.semarglproject.vocab.RDF;


public class DebugSink implements QuadSink {

	private SimpleValueFactory vf = SimpleValueFactory.getInstance();
	private PrintStream out;
	
    public DebugSink(PrintStream out) {
		super();
		this.out = out;
	}

	public DebugSink() {
		this(System.out);
	}


    private Resource convertNonLiteral(String arg) {
        if (arg.startsWith(RDF.BNODE_PREFIX)) {
            return vf.createBNode(arg.substring(2));
        }
        return vf.createIRI(arg);
    }

    @Override
    public final void addNonLiteral(String subj, String pred, String obj) {
        addTriple(convertNonLiteral(subj), vf.createIRI(pred), convertNonLiteral(obj));
    }

    @Override
    public final void addPlainLiteral(String subj, String pred, String content, String lang) {
        if (lang == null) {
            addTriple(convertNonLiteral(subj), vf.createIRI(pred), vf.createLiteral(content));
        } else {
            addTriple(convertNonLiteral(subj), vf.createIRI(pred),
                    vf.createLiteral(content, lang));
        }
    }

    @Override
    public final void addTypedLiteral(String subj, String pred, String content, String type) {
        Literal literal = vf.createLiteral(content, vf.createIRI(type));
        addTriple(convertNonLiteral(subj), vf.createIRI(pred), literal);
    }



    @Override
    public final void addNonLiteral(String subj, String pred, String obj, String graph) {
        if (graph == null) {
            addNonLiteral(subj, pred, obj);
        } else {
            addQuad(convertNonLiteral(subj), vf.createIRI(pred), convertNonLiteral(obj),
                    convertNonLiteral(graph));
        }
    }

    @Override
    public final void addPlainLiteral(String subj, String pred, String content, String lang, String graph) {
        if (graph == null) {
            addPlainLiteral(subj, pred, content, lang);
        } else {
            if (lang == null) {
                addQuad(convertNonLiteral(subj), vf.createIRI(pred), vf.createLiteral(content),
                        convertNonLiteral(graph));
            } else {
                addQuad(convertNonLiteral(subj), vf.createIRI(pred),
                        vf.createLiteral(content, lang), convertNonLiteral(graph));
            }
        }
    }

    @Override
    public final void addTypedLiteral(String subj, String pred, String content, String type, String graph) {
        if (graph == null) {
            addTypedLiteral(subj, pred, content, type);
        } else {
            Literal literal = vf.createLiteral(content, vf.createIRI(type));
            addQuad(convertNonLiteral(subj), vf.createIRI(pred), literal, convertNonLiteral(graph));
        }
    }

    protected void addQuad(Resource subject, IRI predicate, Value object, Resource graph) {
    	Statement s = vf.createStatement(subject, predicate, object, graph);
    	out.println("quad : "+s);
    }
    
    protected void addTriple(Resource subject, IRI predicate, Value object) {
    	Statement s = vf.createStatement(subject, predicate, object);
    	out.println("triple : "+s);
    }

    @Override
    public void startStream() throws ParseException {

    }

    @Override
    public void endStream() throws ParseException {

    }

    @Override
    public boolean setProperty(String key, Object value) {
        return true;
    }

    @Override
    public void setBaseUri(String baseUri) {
    }

}
