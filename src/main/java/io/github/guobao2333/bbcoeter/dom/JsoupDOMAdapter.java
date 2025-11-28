package io.github.guobao2333.bbcoeter.dom;

import io.github.guobao2333.bbcoeter.dom.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 基于Jsoup的DOM适配器实现
 * 依赖: org.jsoup:jsoup:1.15.3 或更高版本
 */
public class JsoupDOMAdapter implements DOMAdapter {

    @Override
    public DOMDocument parseHTML(String html) {
        if (html == null) {
            html = "";
        }
        Document doc = Jsoup.parse(html);
        return new JsoupDocument(doc);
    }

    @Override
    public DOMDocument createDocument() {
        Document doc = Document.createShell("");
        return new JsoupDocument(doc);
    }

    /**
     * Jsoup文档实现
     */
    private static class JsoupDocument implements DOMDocument {
        private final Document document;

        JsoupDocument(Document document) {
            this.document = Objects.requireNonNull(document, "Document cannot be null");
        }

        @Override
        public List<DOMElement> select(String cssSelector) {
            return document.select(cssSelector).stream()
                .map(JsoupElement::new)
                .collect(Collectors.toList());
        }

        @Override
        public String html() {
            return document.html();
        }

        @Override
        public String text() {
            return document.text();
        }

        @Override
        public DOMElement body() {
            Element body = document.body();
            return body != null ? new JsoupElement(body) : null;
        }
    }

    /**
     * Jsoup元素实现
     */
    private static class JsoupElement implements DOMElement {
        private final Element element;

        JsoupElement(Element element) {
            this.element = Objects.requireNonNull(element, "Element cannot be null");
        }

        @Override
        public String tagName() {
            return element.tagName();
        }

        @Override
        public String attr(String attributeName) {
            return element.attr(attributeName);
        }

        @Override
        public void attr(String attributeName, String value) {
            element.attr(attributeName, value);
        }

        @Override
        public boolean hasAttr(String attributeName) {
            return element.hasAttr(attributeName);
        }

        @Override
        public Set<String> attributes() {
            Set<String> attrs = new HashSet<>();
            for (Attribute attr : element.attributes()) {
                attrs.add(attr.getKey());
            }
            return attrs;
        }

        @Override
        public String html() {
            return element.html();
        }

        @Override
        public void html(String html) {
            element.html(html);
        }

        @Override
        public String text() {
            return element.text();
        }

        @Override
        public void text(String text) {
            element.text(text);
        }

        @Override
        public List<DOMElement> children() {
            return element.children().stream()
                .map(JsoupElement::new)
                .collect(Collectors.toList());
        }

        @Override
        public List<DOMElement> select(String cssSelector) {
            return element.select(cssSelector).stream()
                .map(JsoupElement::new)
                .collect(Collectors.toList());
        }

        @Override
        public DOMElement parent() {
            Element parent = element.parent();
            return parent != null ? new JsoupElement(parent) : null;
        }

        @Override
        public void replaceWith(String html) {
            Element replacement = Jsoup.parseBodyFragment(html).body().child(0);
            element.replaceWith(replacement);
        }

        @Override
        public void remove() {
            element.remove();
        }

        @Override
        public String outerHtml() {
            return element.outerHtml();
        }
    }
}