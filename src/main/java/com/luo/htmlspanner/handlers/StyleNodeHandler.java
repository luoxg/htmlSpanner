package com.luo.htmlspanner.handlers;

import android.text.SpannableStringBuilder;

import com.luo.htmlspanner.LogUtil;
import com.luo.htmlspanner.SpanStack;
import com.luo.htmlspanner.TagNodeHandler;
import com.luo.htmlspanner.css.CSSCompiler;
import com.osbcp.cssparser.CSSParser;
import com.osbcp.cssparser.Rule;

import org.htmlcleaner.ContentNode;
import org.htmlcleaner.TagNode;

/**
 * TagNodeHandler that reads <style> blocks and parses the CSS rules within.
 */
public class StyleNodeHandler extends TagNodeHandler {

    private static final String TAG = "StyleNodeHandler";
    
    @Override
    public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end, SpanStack spanStack) {

        if (getSpanner().isAllowStyling()) {

            if (node.getChildren().size() == 1) {
                Object childNode = node.getChildren().get(0);

                if (childNode instanceof ContentNode) {
                    parseCSSFromText(childNode.toString(), spanStack);
                }
            }
        }

    }

    private void parseCSSFromText(String text, SpanStack spanStack) {
        try {
            for (Rule rule : CSSParser.parse(text)) {
                spanStack.registerCompiledRule(CSSCompiler.compile(rule, getSpanner()));
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "Unparseable CSS definition", e);
        }
    }

    @Override
    public boolean rendersContent() {
        return true;
    }
}
