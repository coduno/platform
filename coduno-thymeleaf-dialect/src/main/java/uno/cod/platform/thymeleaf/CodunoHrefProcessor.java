package uno.cod.platform.thymeleaf;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractAttributeModifierAttrProcessor;
import org.thymeleaf.spring4.requestdata.RequestDataValueProcessorUtils;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.standard.processor.attr.AbstractStandardSingleAttributeModifierAttrProcessor;

public class CodunoHrefProcessor extends AbstractStandardSingleAttributeModifierAttrProcessor {
    public static final int ATTR_PRECEDENCE = 1000;
    public static final String ATTR_NAME = "href";
    private String appUrl;

    public CodunoHrefProcessor(String appUrl) {
        super(ATTR_NAME);
        this.appUrl = appUrl;
    }

    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }



    @Override
    protected String getTargetAttributeName(
            final Arguments arguments, final Element element, final String attributeName) {
        return ATTR_NAME;
    }



    @Override
    protected String getTargetAttributeValue(
            final Arguments arguments, final Element element, final String attributeName) {

        final String tmpattributeValue = element.getAttributeValue(attributeName);
        final Configuration configuration = arguments.getConfiguration();
        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);

        final String urlValue = String.format("%s%s%s", "@{", appUrl, tmpattributeValue.substring(2));
        final IStandardExpression expression = expressionParser.parseExpression(configuration, arguments, urlValue);

        final Object result = expression.execute(configuration, arguments);
        final String attributeValue = result == null ? "" : result.toString();

        return RequestDataValueProcessorUtils.processUrl(arguments.getConfiguration(), arguments, attributeValue);
    }




    @Override
    protected AbstractAttributeModifierAttrProcessor.ModificationType getModificationType(
            final Arguments arguments, final Element element, final String attributeName, final String newAttributeName) {
        return AbstractAttributeModifierAttrProcessor.ModificationType.SUBSTITUTION;
    }



    @Override
    protected boolean removeAttributeIfEmpty(
            final Arguments arguments, final Element element, final String attributeName, final String newAttributeName) {
        return false;
    }

}
