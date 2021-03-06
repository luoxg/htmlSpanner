package com.luo.htmlspanner;

import android.text.Spannable;
import android.text.SpannableStringBuilder;

import com.luo.htmlspanner.css.CompiledRule;
import com.luo.htmlspanner.style.Style;

import org.htmlcleaner.TagNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Simple stack structure that Spans can be pushed on.
 * <p>
 * Handles the lookup and application of CSS styles.
 *
 * @author Alex Kuiper
 */
public class SpanStack {

    private static final String TAG = "SpanStack";

    private Stack<SpanCallback> spanItemStack = new Stack<SpanCallback>();

    private Set<CompiledRule> rules = new HashSet<CompiledRule>();

    private Map<TagNode, List<CompiledRule>> lookupCache = new HashMap<TagNode, List<CompiledRule>>();

    public void registerCompiledRule(CompiledRule rule) {
        this.rules.add(rule);
    }

    public Style getStyle(TagNode node, Style baseStyle) {

        if (!lookupCache.containsKey(node)) {

            LogUtil.v(TAG, "Looking for matching CSS rules for node: " + "<" + node.getName() + " id='" + option(node.getAttributeByName("id")) + "' class='" + option(node.getAttributeByName("class")) + "'>");

            List<CompiledRule> matchingRules = new ArrayList<CompiledRule>();
            for (CompiledRule rule : rules) {
                if (rule.matches(node)) {
                    matchingRules.add(rule);
                }
            }

            LogUtil.v(TAG, "Found " + matchingRules.size() + " matching rules.");
            lookupCache.put(node, matchingRules);
        }

        Style result = baseStyle;

        for (CompiledRule rule : lookupCache.get(node)) {

            LogUtil.v(TAG, "Applying rule " + rule);

            Style original = result;
            result = rule.applyStyle(result);

            LogUtil.v(TAG, "Original style: " + original);
            LogUtil.v(TAG, "Resulting style: " + result);
        }

        return result;
    }

    private static String option(String s) {
        if (s == null) {
            return "";
        } else {
            return s;
        }
    }

    public void pushSpan(final Object span, final int start, final int end) {

        if (end > start) {
            SpanCallback callback = new SpanCallback() {
                @Override
                public void applySpan(HtmlSpanner spanner, SpannableStringBuilder builder) {
                    builder.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            };

            spanItemStack.push(callback);
        } else {
            LogUtil.d(TAG, "refusing to put span of type " + span.getClass().getSimpleName() + " and length " + (end - start));
        }
    }

    public void pushSpan(SpanCallback callback) {
        spanItemStack.push(callback);
    }

    public void applySpans(HtmlSpanner spanner, SpannableStringBuilder builder) {
        while (!spanItemStack.isEmpty()) {
            spanItemStack.pop().applySpan(spanner, builder);
        }
    }
}
