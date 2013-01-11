/**
 * 
 */
package org.openjena.jenasesame.util;

import java.util.Iterator;

import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;

import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 *
 */
public class JenaStatementToSesameStatementIterator implements Iterator<Statement>
{
    
    private StmtIterator iterator;
    private ValueFactory valueFactory;

    /**
     * 
     */
    public JenaStatementToSesameStatementIterator(ValueFactory factory, StmtIterator stmt)
    {
        this.valueFactory = factory;
        this.iterator = stmt;
    }

    @Override
    public boolean hasNext()
    {
        return this.iterator.hasNext();
    }

    @Override
    public Statement next()
    {
        return Convert.tripleToStatement(this.valueFactory, this.iterator.next().asTriple());
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("Cannot remove items from this iterator.");
    }
    
}
