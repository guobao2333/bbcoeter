package io.github.guobao2333.bbcoeter.renderer;

import io.github.guobao2333.bbcoeter.ast.ASTNode;
import io.github.guobao2333.bbcoeter.ast.ASTNode.NodeType;

/**
 * BBCode渲染器 - 将AST转换为BBCode
 */
public class BBCodeRenderer {
    private boolean escapeHtml = true;
    
    public void setEscapeHtml(boolean escapeHtml) {
        this.escapeHtml = escapeHtml;
    }
    
    public boolean isEscapeHtml() {
        return escapeHtml;
    }
    
    /**
     * 渲染AST为BBCode字符串
     */
    public String render(ASTNode root) {
        if (root == null) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        renderNode(root, sb);
        return sb.toString();
    }
    
    private void renderNode(ASTNode node, StringBuilder sb) {
        switch (node.getType()) {
            case DOCUMENT:
                renderChildren(node, sb);
                break;
            case PARAGRAPH:
                sb.append("<p>");
                renderChildren(node, sb);
                sb.append("</p>\n");
                break;
            case TEXT:
                renderText(node, sb);
                break;
            case BOLD:
                sb.append("<b>");
                renderChildren(node, sb);
                sb.append("</b>");
                break;
            case ITALIC:
                sb.append("<i>");
                renderChildren(node, sb);
                sb.append("</i>");
                break;
            case UNDERLINE:
                sb.append("<u>");
                renderChildren(node, sb);
                sb.append("</u>");
                break;
            case STRIKETHROUGH:
                sb.append("<strike>");
                renderChildren(node, sb);
                sb.append("</strike>");
                break;
            case LINK:
                renderLink(node, sb);
                break;
            case IMAGE:
                renderImage(node, sb);
                break;
            case CODE_BLOCK:
                sb.append("<div class=\"blockcode\"><blockquote>");
                sb.append(htmlEscape(node.getContent()));
                renderChildren(node, sb);
                sb.append("</blockquote></div>");
                break;
            case QUOTE:
                sb.append("<div class=\"quote\"><blockquote>");
                renderChildren(node, sb);
                sb.append("</blockquote></div>\n");
                break;
            case LIST:
                renderList(node, sb);
                break;
            case LIST_ITEM:
                sb.append("<li>");
                renderChildren(node, sb);
                sb.append("</li>");
                break;
            case TABLE:
                renderTable(node, sb);
                break;
            case TABLE_ROW:
                renderTableRow(node, sb);
                break;
            case TABLE_CELL:
                renderTableCell(node, sb);
                break;
            case COLOR:
                renderColor(node, sb);
                break;
            case SIZE:
                renderSize(node, sb);
                break;
            case FONT:
                renderFont(node, sb);
                break;
            case LINEBREAK:
                sb.append("<br />");
                break;
            case HORIZONTAL_RULE:
                sb.append("<hr class=\"l\" />");
                break;
        }
    }
    
    private void renderText(ASTNode node, StringBuilder sb) {
        String text = node.getContent();
        if (escapeHtml) {
            text = htmlEscape(text);
        }
        // 处理换行和空格
        text = text.replace("\r\n", "<br />")
                  .replace("\n", "<br />")
                  .replace("  ", "&nbsp;&nbsp;");
        sb.append(text);
    }
    
    private void renderLink(ASTNode node, StringBuilder sb) {
        String href = node.getAttribute("href");
        sb.append("<a href=\"").append(htmlEscape(href != null ? href : "")).append("\" target=\"_blank\">");
        renderChildren(node, sb);
        sb.append("</a>");
    }
    
    private void renderImage(ASTNode node, StringBuilder sb) {
        String src = node.getAttribute("src");
        String width = node.getAttribute("width");
        String height = node.getAttribute("height");
        
        sb.append("<img src=\"").append(htmlEscape(src != null ? src : "")).append("\"");
        if (width != null) sb.append(" width=\"").append(width).append("\"");
        if (height != null) sb.append(" height=\"").append(height).append("\"");
        sb.append(" border=\"0\" alt=\"\" />");
    }
    
    private void renderList(ASTNode node, StringBuilder sb) {
        String style = node.getAttribute("style");
        if (style != null && !style.isEmpty()) {
            if (style.equals("1")) {
                sb.append("<ul type=\"1\" class=\"litype_1\">");
            } else if (style.equals("a")) {
                sb.append("<ul type=\"a\" class=\"litype_2\">");
            } else if (style.equals("A")) {
                sb.append("<ul type=\"A\" class=\"litype_3\">");
            } else {
                sb.append("<ul>");
            }
        } else {
            sb.append("<ul>");
        }
        renderChildren(node, sb);
        sb.append("</ul>");
    }
    
    private void renderTable(ASTNode node, StringBuilder sb) {
        String width = node.getAttribute("width");
        String bgcolor = node.getAttribute("bgcolor");
        
        sb.append("<table class=\"t_table\"");
        if (width != null) sb.append(" width=\"").append(width).append("\"");
        if (bgcolor != null) sb.append(" style=\"background-color: ").append(bgcolor).append("\"");
        sb.append(">");
        renderChildren(node, sb);
        sb.append("</table>");
    }
    
    private void renderTableRow(ASTNode node, StringBuilder sb) {
        String bgcolor = node.getAttribute("bgcolor");
        sb.append("<tr");
        if (bgcolor != null) sb.append(" style=\"background-color: ").append(bgcolor).append("\"");
        sb.append(">");
        renderChildren(node, sb);
        sb.append("</tr>");
    }
    
    private void renderTableCell(ASTNode node, StringBuilder sb) {
        String width = node.getAttribute("width");
        sb.append("<td");
        if (width != null) sb.append(" width=\"").append(width).append("\"");
        sb.append(">");
        renderChildren(node, sb);
        sb.append("</td>");
    }
    
    private void renderColor(ASTNode node, StringBuilder sb) {
        String color = node.getAttribute("color");
        sb.append("<font color=\"").append(color != null ? color : "").append("\">");
        renderChildren(node, sb);
        sb.append("</font>");
    }
    
    private void renderSize(ASTNode node, StringBuilder sb) {
        String size = node.getAttribute("size");
        sb.append("<font size=\"").append(size != null ? size : "").append("\">");
        renderChildren(node, sb);
        sb.append("</font>");
    }
    
    private void renderFont(ASTNode node, StringBuilder sb) {
        String face = node.getAttribute("face");
        sb.append("<font face=\"").append(face != null ? face : "").append("\">");
        renderChildren(node, sb);
        sb.append("</font>");
    }
    
    private void renderChildren(ASTNode node, StringBuilder sb) {
        for (ASTNode child : node.getChildren()) {
            renderNode(child, sb);
        }
    }
    
    private String htmlEscape(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#039;");
    }
}
