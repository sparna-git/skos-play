/*
 * (c) Copyright 2009 Talis Information Ltd. All rights reserved. [See end of file]
 */

package org.openjena.jenasesame.util;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class Convert
{
    public static Node bnodeToNode(final BNode value)
    {
        return Node.createAnon(new AnonId(value.getID()));
    }
    
    public static Node literalToNode(final Literal value)
    {
        if(value.getLanguage() != null)
        {
            return Node.createLiteral(value.getLabel(), value.getLanguage(), false);
        }
        if(value.getDatatype() != null)
        {
            return Node.createLiteral(value.getLabel(), null, Node.getType(value.getDatatype().stringValue()));
        }
        // Plain literal
        return Node.createLiteral(value.getLabel());
    }
    
    public static RDFNode literalToRDFNode(final Model nextModel, final Literal value)
    {
        if(value.getDatatype() != null)
        {
            return nextModel.createTypedLiteral(value.stringValue(), value.getDatatype().stringValue());
        }
        else if(value.getLanguage() != null && !"".equals(value.getLanguage()))
        {
            return nextModel.createLiteral(value.stringValue(), value.getLanguage());
        }
        else
        {
            return nextModel.createLiteral(value.stringValue());
        }
    }
    
    public static BNode nodeBlankToBNode(final ValueFactory factory, final Node node)
    {
        return factory.createBNode(node.getBlankNodeLabel());
    }
    
    public static Value nodeLiteralToLiteral(final ValueFactory factory, final Node node)
    {
        if(node.getLiteralDatatype() != null)
        {
            final URI x = factory.createURI(node.getLiteralDatatypeURI());
            return factory.createLiteral(node.getLiteralLexicalForm(), x);
        }
        if(!node.getLiteralLanguage().equals(""))
        {
            return factory.createLiteral(node.getLiteralLexicalForm(), node.getLiteralLanguage());
        }
        
        return factory.createLiteral(node.getLiteralLexicalForm());
    }
    
    public static Value nodeToValue(final ValueFactory factory, final Node node)
    {
        if(node.isLiteral())
        {
            return Convert.nodeLiteralToLiteral(factory, node);
        }
        if(node.isURI())
        {
            return Convert.nodeURIToValue(factory, node);
        }
        if(node.isBlank())
        {
            return Convert.nodeBlankToBNode(factory, node);
        }
        throw new IllegalArgumentException("Not a concrete node");
    }
    
    public static Resource nodeToValueResource(final ValueFactory factory, final Node node)
    {
        if(node.isURI())
        {
            return Convert.nodeURIToValue(factory, node);
        }
        if(node.isBlank())
        {
            return Convert.nodeBlankToBNode(factory, node);
        }
        throw new IllegalArgumentException("Neither a URI nor a blank node");
    }
    
    public static URI nodeURIToValue(final ValueFactory factory, final Node node)
    {
        return factory.createURI(node.getURI());
    }
    
    public static Node resourceToNode(final Resource resource)
    {
        return Convert.valueToNode(resource);
    }
    
    public static com.hp.hpl.jena.rdf.model.Resource resourceToResource(final Model nextModel, final Resource resource)
    {
        if(resource instanceof URI)
        {
            return nextModel.createResource(resource.stringValue());
        }
        else
        {
            return nextModel.createResource(new AnonId(resource.stringValue()));
        }
    }
    
    public static com.hp.hpl.jena.rdf.model.Statement statementToJenaStatement(final Model nextModel,
            final Statement stmt)
    {
        final com.hp.hpl.jena.rdf.model.Resource s = Convert.resourceToResource(nextModel, stmt.getSubject());
        final Property p = Convert.uriToProperty(nextModel, stmt.getPredicate());
        final RDFNode o = Convert.valueToRDFNode(nextModel, stmt.getObject());
        
        return nextModel.createStatement(s, p, o);
    }
    
    /* BEGIN ADDED BY THOMAS FRANCART */
    public static org.openrdf.model.URI propertyToURI(
    		final ValueFactory factory,
    		final com.hp.hpl.jena.rdf.model.Property p
    ) {
    	return factory.createURI(p.getURI());
    }  
    
    public static org.openrdf.model.Statement statementToSesameStatement(
    		final ValueFactory factory,
            final com.hp.hpl.jena.rdf.model.Statement stmt
    ) {
    	final org.openrdf.model.Resource subject = Convert.nodeToValueResource(factory, stmt.getSubject().asNode());
    	final org.openrdf.model.URI predicate = Convert.propertyToURI(factory, stmt.getPredicate());
    	final org.openrdf.model.Value object = Convert.nodeToValue(factory, stmt.getObject().asNode());
    	
        return factory.createStatement(subject, predicate, object);
    }
    /* END ADDED BY THOMAS FRANCART */
    
    public static Triple statementToTriple(final Statement stmt)
    {
        final Node s = Convert.resourceToNode(stmt.getSubject());
        final Node p = Convert.uriToNode(stmt.getPredicate());
        final Node o = Convert.valueToNode(stmt.getObject());
        return new Triple(s, p, o);
    }
    
    // ----
    // Problems with the ValueFactory
    
    public static Statement tripleToStatement(final ValueFactory factory, final Triple triple)
    {
        return factory.createStatement(Convert.nodeToValueResource(factory, triple.getSubject()),
                Convert.nodeURIToValue(factory, triple.getPredicate()),
                Convert.nodeToValue(factory, triple.getObject()));
    }
    
    public static Node uriToNode(final URI value)
    {
        return Node.createURI(value.stringValue());
    }
    
    public static com.hp.hpl.jena.rdf.model.Property uriToProperty(final Model nextModel, final URI resource)
    {
        return nextModel.createProperty(resource.stringValue());
    }
    
    public static Node valueToNode(final Value value)
    {
        if(value instanceof Literal)
        {
            return Convert.literalToNode((Literal)value);
        }
        if(value instanceof URI)
        {
            return Convert.uriToNode((URI)value);
        }
        if(value instanceof BNode)
        {
            return Convert.bnodeToNode((BNode)value);
        }
        throw new IllegalArgumentException("Not a concrete value");
    }
    
    public static RDFNode valueToRDFNode(final Model nextModel, final Value value)
    {
        if(value instanceof Resource)
        {
            return Convert.resourceToResource(nextModel, (Resource)value);
        }
        else
        {
            return Convert.literalToRDFNode(nextModel, (Literal)value);
        }
    }
}

/*
 * (c) Copyright 2009 Talis Information Ltd. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met: 1. Redistributions of source code must retain the
 * above copyright notice, this list of conditions and the following disclaimer. 2. Redistributions
 * in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */