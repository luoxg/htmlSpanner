package com.luo.htmlspanner.handlers.attributes;

import android.text.SpannableStringBuilder;

import com.luo.htmlspanner.LogUtil;
import com.luo.htmlspanner.SpanStack;
import com.luo.htmlspanner.handlers.StyledTextHandler;
import com.luo.htmlspanner.spans.BorderSpan;
import com.luo.htmlspanner.style.Style;

import org.htmlcleaner.TagNode;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 6/23/13
 * Time: 3:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class BorderAttributeHandler extends WrappingStyleHandler {

    private static final String TAG = "BorderAttributeHandler";

    public BorderAttributeHandler(StyledTextHandler handler) {
        super(handler);
    }

    @Override
    public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end, Style useStyle, SpanStack spanStack) {

        if (node.getAttributeByName("border") != null) {
            LogUtil.d(TAG, "Adding BorderSpan from " + start + " to " + end);
            spanStack.pushSpan(new BorderSpan(useStyle, start, end, getSpanner().isUseColoursFromStyle()), start, end);
        }
        super.handleTagNode(node, builder, start, end, useStyle, spanStack);
    }
}
