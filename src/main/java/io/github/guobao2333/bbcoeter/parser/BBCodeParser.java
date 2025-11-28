package io.github.guobao2333.bbcoeter.parser;

import io.github.guobao2333.bbcoeter.ast.ASTNode;
import io.github.guobao2333.bbcoeter.ast.ASTNode.NodeType;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * BBCode解析器 - 基于递归下降的AST构建
 */
public class BBCodeParser {
    private static final Pattern TAG_PATTERN = Pattern.compile(
        "\\[(/?)(\\*|[a-z0-9]+)(?:=([^\\]]+))?\\]",
        Pattern.CASE_INSENSITIVE
    );
    
    /**
     * 解析BBCode字符串为AST
     */
    public ASTNode parse(String input) {
        if (input == null || input.isEmpty()) {
            return new ASTNode(NodeType.DOCUMENT);
        }
        
        ASTNode root = new ASTNode(NodeType.DOCUMENT);
        Deque<ASTNode> stack = new ArrayDeque<>();
        stack.push(root);
        
        Matcher matcher = TAG_PATTERN.matcher(input);
        int lastEnd = 0;
        
        while (matcher.find()) {
            // 处理标签前的文本
            if (matcher.start() > lastEnd) {
                String text = input.substring(lastEnd, matcher.start());
                if (!text.isEmpty()) {
                    addTextNode(stack.peek(), text);
                }
            }
            
            String slash = matcher.group(1);
            String tagName = matcher.group(2).toLowerCase();
            String attribute = matcher.group(3);
            
            if (slash != null && !slash.isEmpty()) {
                // 闭合标签
                handleClosingTag(stack, tagName);
            } else {
                // 开放标签
                handleOpeningTag(stack, tagName, attribute);
            }
            
            lastEnd = matcher.end();
        }
        
        // 处理剩余文本
        if (lastEnd < input.length()) {
            String text = input.substring(lastEnd);
            if (!text.isEmpty()) {
                addTextNode(stack.peek(), text);
            }
        }
        
        return root;
    }
    
    private void handleOpeningTag(Deque<ASTNode> stack, String tagName, String attribute) {
        ASTNode current = stack.peek();
        ASTNode newNode = createNodeForTag(tagName, attribute);
        
        if (newNode != null) {
            current.appendChild(newNode);
            
            // 自闭合标签不入栈
            if (!isSelfClosingTag(tagName)) {
                stack.push(newNode);
            }
        } else {
            // 未知标签，作为文本处理
            addTextNode(current, "[" + tagName + (attribute != null ? "=" + attribute : "") + "]");
        }
    }
    
    private void handleClosingTag(Deque<ASTNode> stack, String tagName) {
        // 查找匹配的开放标签
        Iterator<ASTNode> iterator = stack.iterator();
        ASTNode matchedNode = null;
        
        while (iterator.hasNext()) {
            ASTNode node = iterator.next();
            if (matchesTag(node.getType(), tagName)) {
                matchedNode = node;
                break;
            }
        }
        
        if (matchedNode != null) {
            // 找到匹配的标签，弹出到该标签
            while (!stack.isEmpty() && stack.peek() != matchedNode) {
                stack.pop();
            }
            if (!stack.isEmpty()) {
                stack.pop();
            }
        } else {
            // 未找到匹配标签，作为文本处理
            addTextNode(stack.peek(), "[/" + tagName + "]");
        }
    }
    
    private ASTNode createNodeForTag(String tagName, String attribute) {
        ASTNode node;
        
        switch (tagName) {
            case "*":
                return new ASTNode(NodeType.LIST_ITEM);
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
                return new ASTNode(NodeType.STRIKETHROUGH);
            case "url":
                node = new ASTNode(NodeType.LINK);
                if (attribute != null) {
                    node.setAttribute("href", attribute);
                }
                return node;
            case "img":
                node = new ASTNode(NodeType.IMAGE);
                if (attribute != null) {
                    node.setAttribute("src", attribute);
                }
                return node;
            case "code":
                return new ASTNode(NodeType.CODE_BLOCK);
            case "quote":
                return new ASTNode(NodeType.QUOTE);
            case "list":
                node = new ASTNode(NodeType.LIST);
                if (attribute != null) {
                    node.setAttribute("style", attribute);
                }
                return node;
            case "table":
                node = new ASTNode(NodeType.TABLE);
                if (attribute != null) {
                    parseTableAttributes(node, attribute);
                }
                return node;
            case "tr":
                node = new ASTNode(NodeType.TABLE_ROW);
                if (attribute != null) {
                    node.setAttribute("bgcolor", attribute);
                }
                return node;
            case "td":
            case "th":
                node = new ASTNode(NodeType.TABLE_CELL);
                if (attribute != null) {
                    node.setAttribute("width", attribute);
                }
                return node;
            case "color":
                node = new ASTNode(NodeType.COLOR);
                if (attribute != null) {
                    node.setAttribute("color", attribute);
                }
                return node;
            case "size":
                node = new ASTNode(NodeType.SIZE);
                if (attribute != null) {
                    node.setAttribute("size", attribute);
                }
                return node;
            case "font":
                node = new ASTNode(NodeType.FONT);
                if (attribute != null) {
                    node.setAttribute("face", attribute);
                }
                return node;
            case "hr":
                return new ASTNode(NodeType.HORIZONTAL_RULE);
            default:
                return null;
        }
    }
    
    private boolean matchesTag(NodeType type, String tagName) {
        switch (tagName) {
            case "b":
            case "strong":
                return type == NodeType.BOLD;
            case "i":
            case "em":
                return type == NodeType.ITALIC;
            case "u":
                return type == NodeType.UNDERLINE;
            case "s":
            case "strike":
                return type == NodeType.STRIKETHROUGH;
            case "url":
                return type == NodeType.LINK;
            case "img":
                return type == NodeType.IMAGE;
            case "code":
                return type == NodeType.CODE_BLOCK;
            case "quote":
                return type == NodeType.QUOTE;
            case "list":
                return type == NodeType.LIST;
            case "table":
                return type == NodeType.TABLE;
            case "tr":
                return type == NodeType.TABLE_ROW;
            case "td":
            case "th":
                return type == NodeType.TABLE_CELL;
            case "color":
                return type == NodeType.COLOR;
            case "size":
                return type == NodeType.SIZE;
            case "font":
                return type == NodeType.FONT;
            default:
                return false;
        }
    }
    
    private boolean isSelfClosingTag(String tagName) {
        return "hr".equalsIgnoreCase(tagName);
    }
    
    private void parseTableAttributes(ASTNode node, String attr) {
        String[] parts = attr.split(",");
        if (parts.length > 0) {
            node.setAttribute("width", parts[0].trim());
        }
        if (parts.length > 1) {
            node.setAttribute("bgcolor", parts[1].trim());
        }
    }
    
    private void addTextNode(ASTNode parent, String text) {
        if (text == null || text.isEmpty()) {
            return;
        }
        
        // 合并连续的文本节点
        List<ASTNode> children = parent.getChildrenInternal();
        if (!children.isEmpty()) {
            ASTNode lastChild = children.get(children.size() - 1);
            if (lastChild.getType() == NodeType.TEXT) {
                lastChild.setContent(lastChild.getContent() + text);
                return;
            }
        }
        
        ASTNode textNode = new ASTNode(NodeType.TEXT, text);
        parent.appendChild(textNode);
    }
}
