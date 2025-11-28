package io.github.guobao2333.bbcoeter.parser;

import io.github.guobao2333.bbcoeter.ast.ASTNode;
import io.github.guobao2333.bbcoeter.ast.ASTNode.NodeType;
import io.github.guobao2333.bbcoeter.dom.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTML解析器 - 使用DOM适配器构建AST
 */
public class HTMLParser {
    private final DOMAdapter domAdapter;
    
    public HTMLParser(DOMAdapter domAdapter) {
        this.domAdapter = Objects.requireNonNull(domAdapter, "DOMAdapter cannot be null");
    }
    
    /**
     * 解析HTML字符串为AST
     */
    public ASTNode parse(String html) {
        if (html == null || html.isEmpty()) {
            return new ASTNode(NodeType.DOCUMENT);
        }
        
        DOMAdapter.DOMDocument doc = domAdapter.parseHTML(html);
        cleanUnsafeContent(doc);
        
        ASTNode root = new ASTNode(NodeType.DOCUMENT);
        DOMAdapter.DOMElement body = doc.body();
        
        if (body != null) {
            for (DOMAdapter.DOMElement child : body.children()) {
                ASTNode childNode = convertElement(child);
                if (childNode != null) {
                    root.appendChild(childNode);
                }
            }
        }
        
        return root;
    }
    
    private void cleanUnsafeContent(DOMAdapter.DOMDocument doc) {
        // 移除脚本和危险标签
        doc.select("script, style, noscript, select, object, embed, iframe")
           .forEach(DOMAdapter.DOMElement::remove);
        
        // 移除事件处理器 
        doc.select("*").forEach(elem -> {
            Set<String> attrs = new HashSet<>(elem.attributes());
            for (String attr : attrs) {
                if (attr.toLowerCase().startsWith("on")) {
                    elem.attr(attr, "");
                }
            }
        });
    }
    
    private ASTNode convertElement(DOMAdapter.DOMElement element) {
        String tagName = element.tagName().toLowerCase();
        ASTNode node = createNodeForHtmlTag(tagName, element);
        
        if (node == null) {
            // 未知标签，提取文本内容
            String text = element.text();
            if (!text.isEmpty()) {
                return new ASTNode(NodeType.TEXT, text);
            }
            return null;
        }
        
        // 叶子节点直接返回
        if (node.getType() == NodeType.IMAGE || 
            node.getType() == NodeType.LINEBREAK || 
            node.getType() == NodeType.HORIZONTAL_RULE ||
            node.getType() == NodeType.CODE_BLOCK) {
            return node;
        }
        
        // 递归处理子元素
        for (DOMAdapter.DOMElement child : element.children()) {
            ASTNode childNode = convertElement(child);
            if (childNode != null) {
                node.appendChild(childNode);
            }
        }
        
        // 如果没有子元素，检查是否有文本内容
        if (node.getChildren().isEmpty()) {
            String text = element.text();
            if (!text.isEmpty()) {
                node.appendChild(new ASTNode(NodeType.TEXT, text));
            }
        }
        
        return node;
    }
    
    private ASTNode createNodeForHtmlTag(String tagName, DOMAdapter.DOMElement element) {
        ASTNode node;
        
        switch (tagName) {
            case "p":
                return new ASTNode(NodeType.PARAGRAPH);
            case "b":
            case "strong":
                return new ASTNode(NodeType.BOLD);
            case "i":
            case "em":
                return new ASTNode(NodeType.ITALIC);
            case "u":
                return new ASTNode(NodeType.UNDERLINE);
            case "s":
            case "strike":
            case "del":
                return new ASTNode(NodeType.STRIKETHROUGH);
            case "a":
                node = new ASTNode(NodeType.LINK);
                String href = element.attr("href");
                if (href != null && !href.isEmpty()) {
                    node.setAttribute("href", href);
                }
                return node;
            case "img":
                node = new ASTNode(NodeType.IMAGE);
                String src = element.attr("src");
                if (src != null && !src.isEmpty()) {
                    node.setAttribute("src", src);
                }
                String width = element.attr("width");
                String height = element.attr("height");
                if (width != null) node.setAttribute("width", width);
                if (height != null) node.setAttribute("height", height);
                return node;
            case "code":
            case "pre":
                node = new ASTNode(NodeType.CODE_BLOCK);
                node.setContent(element.text());
                return node;
            case "blockquote":
                return new ASTNode(NodeType.QUOTE);
            case "ul":
            case "ol":
                node = new ASTNode(NodeType.LIST);
                node.setAttribute("style", tagName.equals("ol") ? "1" : "");
                return node;
            case "li":
                return new ASTNode(NodeType.LIST_ITEM);
            case "table":
                node = new ASTNode(NodeType.TABLE);
                String tableWidth = element.attr("width");
                String tableBgcolor = extractBgColor(element);
                if (tableWidth != null) node.setAttribute("width", tableWidth);
                if (tableBgcolor != null) node.setAttribute("bgcolor", tableBgcolor);
                return node;
            case "tr":
                node = new ASTNode(NodeType.TABLE_ROW);
                String rowBgcolor = extractBgColor(element);
                if (rowBgcolor != null) node.setAttribute("bgcolor", rowBgcolor);
                return node;
            case "td":
            case "th":
                node = new ASTNode(NodeType.TABLE_CELL);
                String cellWidth = element.attr("width");
                if (cellWidth != null) node.setAttribute("width", cellWidth);
                return node;
            case "br":
                return new ASTNode(NodeType.LINEBREAK);
            case "hr":
                return new ASTNode(NodeType.HORIZONTAL_RULE);
            case "font":
                return handleFontTag(element);
            case "div":
            case "span":
                return new ASTNode(NodeType.PARAGRAPH);
            default:
                return null;
        }
    }
    
    private ASTNode handleFontTag(DOMAdapter.DOMElement element) {
        String color = element.attr("color");
        String size = element.attr("size");
        String face = element.attr("face");
        
        if (color != null) {
            ASTNode node = new ASTNode(NodeType.COLOR);
            node.setAttribute("color", color);
            return node;
        } else if (size != null) {
            ASTNode node = new ASTNode(NodeType.SIZE);
            node.setAttribute("size", size);
            return node;
        } else if (face != null) {
            ASTNode node = new ASTNode(NodeType.FONT);
            node.setAttribute("face", face);
            return node;
        } else {
            return new ASTNode(NodeType.PARAGRAPH);
        }
    }
    
    private static final Pattern EXTRACT_BG_COLOR_PATTERN = Pattern.compile("background-color:\\s*([^;]+)", Pattern.CASE_INSENSITIVE);
    
    private String extractBgColor(DOMAdapter.DOMElement element) {
        String bgcolor = element.attr("bgcolor");
        if (bgcolor != null && !bgcolor.isEmpty()) {
            return bgcolor;
        }
        
        String style = element.attr("style");
        if (style != null) {
            Matcher matcher = EXTRACT_BG_COLOR_PATTERN.matcher(style);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        }
        
        return null;
    }
}
