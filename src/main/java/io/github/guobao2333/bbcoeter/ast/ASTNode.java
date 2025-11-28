package io.github.guobao2333.bbcoeter.ast;

import java.util.*;

/**
 * 统一AST节点定义
 * 这是所有格式转换的中间表示
 */
public class ASTNode {
    /**
     * 节点类型枚举
     */
    public enum NodeType {
        DOCUMENT,      // 文档根节点
        PARAGRAPH,     // 段落
        TEXT,          // 纯文本
        BOLD,          // 粗体
        ITALIC,        // 斜体
        UNDERLINE,     // 下划线
        STRIKETHROUGH, // 删除线
        LINK,          // 链接
        IMAGE,         // 图片
        CODE_BLOCK,    // 代码块
        CODE_INLINE,   // 行内代码
        QUOTE,         // 引用
        LIST,          // 列表
        LIST_ITEM,     // 列表项
        TABLE,         // 表格
        TABLE_ROW,     // 表格行
        TABLE_CELL,    // 表格单元格
        LINEBREAK,     // 换行
        HORIZONTAL_RULE, // 水平线
        FONT,          // 字体样式
        COLOR,         // 颜色
        SIZE,          // 字号
        HTML_RAW       // 原始HTML
    }
    
    private final NodeType type;
    private String content;
    private final Map<String, String> attributes;
    private final List<ASTNode> children;
    private ASTNode parent;
    
    public ASTNode(NodeType type) {
        this.type = Objects.requireNonNull(type, "Node type cannot be null");
        this.content = "";
        this.attributes = new LinkedHashMap<>();
        this.children = new ArrayList<>();
    }
    
    public ASTNode(NodeType type, String content) {
        this(type);
        this.content = content != null ? content : "";
    }
    
    // Public getters
    public NodeType getType() { 
        return type; 
    }
    
    public String getContent() { 
        return content; 
    }
    
    public void setContent(String content) { 
        this.content = content != null ? content : ""; 
    }
    
    public Map<String, String> getAttributes() { 
        return Collections.unmodifiableMap(attributes); 
    }
    
    public void setAttribute(String key, String value) { 
        if (key != null && value != null) {
            attributes.put(key, value); 
        }
    }
    
    public String getAttribute(String key) { 
        return attributes.get(key); 
    }
    
    public boolean hasAttribute(String key) { 
        return attributes.containsKey(key); 
    }
    
    public List<ASTNode> getChildren() { 
        return Collections.unmodifiableList(children); 
    }
    
    public void appendChild(ASTNode child) { 
        if (child != null) {
            children.add(child);
            child.parent = this;
        }
    }
    
    public void removeChild(ASTNode child) {
        if (child != null) {
            children.remove(child);
            child.parent = null;
        }
    }
    
    public ASTNode getParent() { 
        return parent; 
    }
    
    public boolean isLeaf() { 
        return children.isEmpty(); 
    }
    
    // 包级访问 - 用于内部修改children列表
    //
    public List<ASTNode> getChildrenInternal() {
        return children;
    }
    
    @Override
    public String toString() {
        return "ASTNode{type=" + type + 
               ", content='" + (content.length() > 20 ? content.substring(0, 20) + "..." : content) + 
               "', attrs=" + attributes + 
               ", children=" + children.size() + "}";
    }
}
