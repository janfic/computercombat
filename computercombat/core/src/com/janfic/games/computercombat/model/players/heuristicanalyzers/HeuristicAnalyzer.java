package com.janfic.games.computercombat.model.players.heuristicanalyzers;

import com.janfic.games.computercombat.model.moves.MoveResult;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import groovy.lang.GroovyShell;

import java.util.List;

/**
 *
 * @author janfc
 */
public abstract class HeuristicAnalyzer {

    public abstract float analyze(List<MoveResult> results);

    /**
     * Instantiates a HeuristicAnalyzer from a String of its constructor.
     *
     * @param code
     * @return
     */
    public static HeuristicAnalyzer getHeuristicAnalyzerFromCode(String code) {
        CompilerConfiguration config = new CompilerConfiguration();
        config.addCompilationCustomizers(new ImportCustomizer().addStarImports(
            "com.janfic.games.computercombat.model.players.heuristicanalyzers"
        ));
        GroovyShell shell = new GroovyShell(config);
        HeuristicAnalyzer h = (HeuristicAnalyzer) shell.evaluate(code);
        return h;
    }
}
