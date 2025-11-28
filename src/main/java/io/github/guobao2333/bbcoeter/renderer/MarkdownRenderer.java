package io.github.guobao2333.bbcoeter.renderer;

import io.github.guobao2333.bbcoeter.ast.ASTNode;
import io.github.guobao2333.bbcoeter.ast.ASTNode.NodeType;

/**
 * Markdown渲染器 - 展示如何轻松添加新的格式支持
 */
public class MarkdownRenderer {
    
    /**
     * 渲染AST为Markdown字符串
     */
    public String render(ASTNode root) {
        if (root == null) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        renderNode(root, sb, 0);
        return sb.toString();
    }
    
    private void renderNode(ASTNode node, StringBuilder sb, int listDepth) {
        switch (node.getType()) {
            case DOCUMENT:
                for (ASTNode child : node.getChildren()) {
                    renderNode(child, sb, listDepth);
                }
                break;
            case PARAGRAPH:
                renderChildren(node, sb, listDepth);
                sb.append("\n\n");
                break;
            case TEXT:
                sb.append(node.getContent());
                break;
            case BOLD:
                sb.append("**");
                renderChildren(node, sb, listDepth);
                sb.append("**");
                break;
            case ITALIC:
                sb.append("*");
                renderChildren(node, sb, listDepth);
                sb.append("*");
                break;
            case UNDERLINE:
                // Markdown不直接支持下划线，使用HTML
                sb.append("<u>");
                renderChildren(node, sb, listDepth);
                sb.append("</u>");
                break;
            case STRIKETHROUGH:
                sb.append("~~");
                renderChildren(node, sb, listDepth);
                sb.append("~~");
                break;
            case LINK:
                sb.append("[");
                renderChildren(node, sb, listDepth);
                String href = node.getAttribute("href");
                sb.append("](").append(href != null ? href : "").append(")");
                break;
            case IMAGE:
                sb.append("![");
                renderChildren(node, sb, listDepth);
                String src = node.getAttribute("src");
                sb.append("](").append(src != null ? src : "").append(")");
                break;
            case CODE_BLOCK:
                sb.append("```\n");
                sb.append(node.getContent());
                renderChildren(node, sb, listDepth);
                sb.append("\n```\n");
                break;
            case QUOTE:
                sb.append("> ");
                renderChildren(node, sb, listDepth);
                sb.append("\n");
                break;
            case LIST:
                for (ASTNode child : node.getChildren()) {
                    renderNode(child, sb, listDepth);
                }
                sb.append("\n");
                break;
            case LIST_ITEM:
                sb.append("  ".repeat(listDepth)).append("- ");
                renderChildren(node, sb, listDepth + 1);
                sb.append("\n");
                break;
            case TABLE:
                renderTable(node, sb);
                break;
            case HORIZONTAL_RULE:
                sb.append("\n---\n\n");
                break;
            case LINEBREAK:
                sb.append("  \n");
                break;
            default:
                renderChildren(node, sb, listDepth);
                break;
        }
    }
    
    private void renderTable(ASTNode node, StringBuilder sb) {
        // 简化的表格渲染
        for (ASTNode row : node.getChildren()) {
            if (row.getType() == NodeType.TABLE_ROW) {
                sb.append("|");
                for (ASTNode cell : row.getChildren()) {
                    if (cell.getType() == NodeType.TABLE_CELL) {
                        sb.append(" ");
                        renderChildren(cell, sb, 0);
                        sb.append(" |");
                    }
                }
                sb.append("\n");
            }
        }
        sb.append("\n");
    }
    
    private void renderChildren(ASTNode node, StringBuilder sb, int listDepth) {
        for (ASTNode child : node.getChildren()) {
            renderNode(child, sb, listDepth);
        }
    }
}
