package com.wjy.personal_blog.utils;

import org.commonmark.node.Document;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class MarkdownUtil {

    /* 将markdown转为html */
    public static String markdownToHtml(String markdown) {
       if (markdown==null||markdown.isEmpty()){
           return "";
       }

        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String markdownToHtml = renderer.render(document);

        return markdownToHtml;

    }
}
