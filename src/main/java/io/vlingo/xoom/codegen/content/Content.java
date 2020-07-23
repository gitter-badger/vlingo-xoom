package io.vlingo.xoom.codegen.content;

import io.vlingo.xoom.codegen.template.TemplateFile;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import javax.lang.model.element.TypeElement;

public abstract class Content {

    public final TemplateStandard standard;

    protected Content(final TemplateStandard standard) {
        this.standard = standard;
    }

    public static Content with(final TemplateStandard standard,
                               final TemplateFile templatefile,
                               final String text) {
        return new TextBasedContent(standard, templatefile, text);
    }

    public static Content with(final TemplateStandard standard,
                               final TypeElement typeElement) {
        return new TypeBasedContent(standard, typeElement);
    }

    public abstract void create();

    public abstract String retrieveClassName();

    public abstract String retrievePackage();

    public abstract String retrieveFullyQualifiedName();

    public abstract boolean canWrite();

    public abstract boolean contains(final String term);

    public boolean has(final TemplateStandard standard) {
        return this.standard.equals(standard);
    }

}