package io.github.anamspaula.realestate;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModularidadeTest {

    private final ApplicationModules modulos = ApplicationModules.of(BackendApplication.class);

    @Test
    void verificaEstruturaModular() {
        modulos.verify();
    }

    @Test
    void geraDocumentacao() {
        new Documenter(modulos).writeDocumentation();
    }
}
